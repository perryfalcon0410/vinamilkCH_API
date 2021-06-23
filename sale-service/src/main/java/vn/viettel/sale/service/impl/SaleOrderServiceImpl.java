package vn.viettel.sale.service.impl;

import com.amazonaws.services.dynamodbv2.xspec.L;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.UserDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.promotion.PromotionProgramDTO;
import vn.viettel.core.dto.promotion.PromotionProgramDiscountDTO;
import vn.viettel.core.dto.voucher.VoucherDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.entities.Product;
import vn.viettel.sale.entities.SaleOrder;
import vn.viettel.sale.entities.SaleOrderDetail;
import vn.viettel.sale.entities.SaleOrderDiscount;
import vn.viettel.sale.messaging.OrderDetailTotalResponse;
import vn.viettel.sale.messaging.RedInvoiceFilter;
import vn.viettel.sale.messaging.SaleOrderFilter;
import vn.viettel.sale.messaging.SaleOrderTotalResponse;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.SaleOrderService;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.service.feign.PromotionClient;
import vn.viettel.sale.service.feign.ShopClient;
import vn.viettel.sale.service.feign.UserClient;
import vn.viettel.sale.specification.SaleOderSpecification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SaleOrderServiceImpl extends BaseServiceImpl<SaleOrder, SaleOrderRepository> implements SaleOrderService {
    @Autowired
    SaleOrderRepository saleOrderRepository;
    @Autowired
    SaleOrderDiscountRepository saleOrderDiscountRepository;
    @Autowired
    CustomerClient customerClient;
    @Autowired
    SaleOrderDetailRepository saleOrderDetailRepository;
    @Autowired
    UserClient userClient;
    @Autowired
    ProductPriceRepository productPriceRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    PromotionClient promotionClient;
    @Autowired
    ShopClient shopClient;


    @Override
    public CoverResponse<Page<SaleOrderDTO>, SaleOrderTotalResponse> getAllSaleOrder(SaleOrderFilter saleOrderFilter, Pageable pageable, Long id) {
        List<Long> customerIds = customerClient.getIdCustomerByV1(saleOrderFilter.getSearchKeyword(), saleOrderFilter.getCustomerPhone()).getData();
        Page<SaleOrder> findAll;
        SaleOrderTotalResponse totalResponse = null;
        if (customerIds.isEmpty()) {
            findAll = repository.findAll(Specification.where(SaleOderSpecification.type(-1)), pageable);
        } else {
            findAll = repository.findAll(Specification.where(SaleOderSpecification.hasNameOrPhone(customerIds))
                    .and(SaleOderSpecification.hasFromDateToDate(saleOrderFilter.getFromDate(), saleOrderFilter.getToDate()))
                    .and(SaleOderSpecification.hasOrderNumber(saleOrderFilter.getOrderNumber()))
                    .and(SaleOderSpecification.type(1))
                    .and(SaleOderSpecification.hasShopId(id))
                    .and(SaleOderSpecification.hasUseRedInvoice(saleOrderFilter.getUsedRedInvoice())), pageable);

            List<SaleOrder> totals = repository.findAll(Specification.where(SaleOderSpecification.hasNameOrPhone(customerIds))
                    .and(SaleOderSpecification.hasFromDateToDate(saleOrderFilter.getFromDate(), saleOrderFilter.getToDate()))
                    .and(SaleOderSpecification.hasOrderNumber(saleOrderFilter.getOrderNumber()))
                    .and(SaleOderSpecification.type(1))
                    .and(SaleOderSpecification.hasShopId(id))
                    .and(SaleOderSpecification.hasUseRedInvoice(saleOrderFilter.getUsedRedInvoice())));
            totalResponse = new SaleOrderTotalResponse();
            for (SaleOrder order : totals) {
                totalResponse.addTotalAmount(order.getAmount()).addAllTotal(order.getTotal()).addAllPromotion(order.getTotalPromotion());
            }
        }
        if(findAll.getContent().size() == 0) {
            CoverResponse coverResponse = new CoverResponse(findAll, totalResponse);
            return coverResponse;
        }
        List<CustomerDTO> customers = customerClient.getCustomerInfoV1(null, findAll.getContent().stream().map(item -> item.getCustomerId()).collect(Collectors.toList()));
        List<UserDTO> users = userClient.getUserByIdsV1(findAll.getContent().stream().map(item -> item.getSalemanId())
                .distinct().filter(Objects::nonNull).collect(Collectors.toList()));
        Page<SaleOrderDTO> saleOrderDTOS = findAll.map(item -> mapSaleOrderDTO(item, customers, users));

        CoverResponse coverResponse = new CoverResponse(saleOrderDTOS, totalResponse);
        return coverResponse;
    }

    private SaleOrderDTO mapSaleOrderDTO(SaleOrder saleOrder, List<CustomerDTO> customers, List<UserDTO> users) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        SaleOrderDTO dto = modelMapper.map(saleOrder, SaleOrderDTO.class);
        if(customers != null){
            for(CustomerDTO customer : customers){
                if(customer.getId().equals(saleOrder.getCustomerId())){
                    dto.setCustomerNumber(customer.getCustomerCode());
                    dto.setCustomerName(customer.getFullName());
                    break;
                }
            }
        }
        if(users != null){
            for(UserDTO user : users){
                if(user.getId().equals(saleOrder.getSalemanId())){
                    dto.setSalesManName(user.getFullName());
                    break;
                }
            }
        }
        return dto;
    }

    public SaleOrderDetailDTO getSaleOrderDetail(long saleOrderId, String orderNumber) {
        SaleOrderDetailDTO orderDetail = new SaleOrderDetailDTO();
        List<SaleOrderDetail> saleOrderDetails = saleOrderDetailRepository.findSaleOrderDetail(saleOrderId, null);
        List<Product> products = productRepository.getProducts(saleOrderDetails.stream().map(item -> item.getProductId()).distinct().collect(Collectors.toList()), null);
        orderDetail.setOrderDetail(getDetail(saleOrderDetails, products));
        orderDetail.setInfos(getInfos(saleOrderId, orderNumber));
        orderDetail.setDiscount(getDiscount(saleOrderId, orderNumber));
        orderDetail.setPromotion(getPromotion(saleOrderDetails/*, products*/));
        return orderDetail;
    }

    private InfosOrderDetailDTO getInfos(long saleOrderId, String orderNumber) {
        InfosOrderDetailDTO infosOrderDetailDTO = new InfosOrderDetailDTO();
        SaleOrder saleOrder = saleOrderRepository.findById(saleOrderId).get();

        infosOrderDetailDTO.setOrderNumber(orderNumber);//ma hoa don
        CustomerDTO customer = customerClient.getCustomerByIdV1(saleOrder.getCustomerId()).getData();

        infosOrderDetailDTO.setCustomerName(customer.getLastName() + " " + customer.getFirstName());
        infosOrderDetailDTO.setOrderDate(saleOrder.getOrderDate());
        UserDTO user = userClient.getUserByIdV1(saleOrder.getSalemanId());

        infosOrderDetailDTO.setSaleMan(user.getFirstName() + " " + user.getLastName());//nhan vien

        infosOrderDetailDTO.setCurrency("VND");
        infosOrderDetailDTO.setTotal(saleOrder.getTotal());
        infosOrderDetailDTO.setTotalPaid(saleOrder.getTotalPaid());
        infosOrderDetailDTO.setBalance(saleOrder.getBalance());
        return infosOrderDetailDTO;
    }

    private CoverResponse<List<OrderDetailDTO>, OrderDetailTotalResponse> getDetail(List<SaleOrderDetail> saleOrderDetails, List<Product> products) {
        if(saleOrderDetails == null || saleOrderDetails.isEmpty()) return null;
        int totalQuantity = 0;
        double totalAmount = 0, totalDiscount = 0, totalPayment = 0;
        List<OrderDetailDTO> saleOrderDetailList = new ArrayList<>();

        for (SaleOrderDetail saleOrderDetail : saleOrderDetails) {
            if(!saleOrderDetail.getIsFreeItem()) {
                OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
                orderDetailDTO.setProductId(saleOrderDetail.getProductId());
                if (products != null) {
                    for (Product product : products) {
                        if (product.getId().equals(saleOrderDetail.getProductId())) {
                            orderDetailDTO.setProductCode(product.getProductCode());
                            orderDetailDTO.setProductName(product.getProductName());
                            orderDetailDTO.setUnit(product.getUom1());
                            break;
                        }
                    }
                }

                orderDetailDTO.setQuantity(saleOrderDetail.getQuantity());
                orderDetailDTO.setPricePerUnit(saleOrderDetail.getPrice());
                orderDetailDTO.setAmount(saleOrderDetail.getAmount());
                double discount = 0;
                if (saleOrderDetail.getAutoPromotion() == null && saleOrderDetail.getZmPromotion() == null) {
                    discount = 0F;
                    orderDetailDTO.setDiscount(discount);
                } else if (saleOrderDetail.getAutoPromotion() == null || saleOrderDetail.getZmPromotion() == null) {
                    if (saleOrderDetail.getAutoPromotion() == null) {
                        discount = saleOrderDetail.getZmPromotion();
                        orderDetailDTO.setDiscount(discount);
                    }
                    if (saleOrderDetail.getZmPromotion() == null) {
                        discount = saleOrderDetail.getAutoPromotion();
                        orderDetailDTO.setDiscount(discount);
                    }
                } else {
                    discount = saleOrderDetail.getAutoPromotion() + saleOrderDetail.getZmPromotion();
                    orderDetailDTO.setDiscount(discount);
                }
                orderDetailDTO.setPayment(saleOrderDetail.getTotal());
                totalQuantity = totalQuantity + saleOrderDetail.getQuantity();
                totalAmount = totalAmount + saleOrderDetail.getAmount();
                totalDiscount = totalDiscount + discount;
                totalPayment = totalPayment + saleOrderDetail.getTotal();
                saleOrderDetailList.add(orderDetailDTO);
            }
        }
        OrderDetailTotalResponse totalResponse =
                new OrderDetailTotalResponse(totalQuantity, totalAmount, totalDiscount, totalPayment);

        CoverResponse<List<OrderDetailDTO>, OrderDetailTotalResponse> response =
                new CoverResponse(saleOrderDetailList, totalResponse);
        return response;
    }

    public List<DiscountDTO> getDiscount(long saleOrderId, String orderNumber) {
        List<DiscountDTO> discountDTOList = new ArrayList<>();
        List<VoucherDTO> voucherDTOS = promotionClient.getVoucherBySaleOrderIdV1(saleOrderId).getData();
        if (voucherDTOS.size() > 0) {
            for (VoucherDTO voucher : voucherDTOS) {
                DiscountDTO discountDTO = new DiscountDTO();
                discountDTO.setPromotionName(voucher.getVoucherName());
                discountDTO.setPromotionType("Voucher");
                discountDTO.setVoucherType(voucher.getVoucherCode());
                discountDTO.setDiscountPrice(voucher.getPrice());
                discountDTOList.add(discountDTO);
            }
        }

        List<PromotionProgramDiscountDTO> promotionProgramDiscounts =
                promotionClient.listPromotionProgramDiscountByOrderNumberV1(orderNumber).getData();
        if (promotionProgramDiscounts != null) {
            for (PromotionProgramDiscountDTO promotionProgramDiscount : promotionProgramDiscounts) {
                DiscountDTO discountDTO = new DiscountDTO();
                discountDTO.setDiscountPrice(promotionProgramDiscount.getDiscountAmount());
                discountDTO.setPromotionType("Mã giảm giá");
                PromotionProgramDTO promotionProgram =
                        promotionClient.getByIdV1(promotionProgramDiscount.getId()).getData();
                discountDTO.setPromotionName(promotionProgram.getPromotionProgramName());
                discountDTO.setVoucherType(promotionProgram.getPromotionProgramCode());
                discountDTOList.add(discountDTO);
            }
        }

        List<SaleOrderDiscount> lstSaleOrderDiscount = saleOrderDiscountRepository.findAllBySaleOrderId(saleOrderId);
        if(lstSaleOrderDiscount != null){
            HashMap<Long,DiscountDTO> map = new HashMap<>();
            for(SaleOrderDiscount saleOrderDiscount : lstSaleOrderDiscount){
                if(saleOrderDiscount.getDiscountAmount() != null) {
                    if (map.containsKey(saleOrderDiscount.getPromotionProgramId())) {
                        map.get(saleOrderDiscount.getPromotionProgramId()).setDiscountPrice(
                                map.get(saleOrderDiscount.getPromotionProgramId()).getDiscountPrice() + saleOrderDiscount.getDiscountAmount().doubleValue()
                        );
                    } else {
                        DiscountDTO discountDTO = new DiscountDTO();
                        discountDTO.setDiscountPrice(saleOrderDiscount.getDiscountAmount().doubleValue());
                        discountDTO.setPromotionType(saleOrderDiscount.getPromotionType());
                        discountDTO.setVoucherType(saleOrderDiscount.getPromotionCode());
                        discountDTO.setPromotionName(saleOrderDiscount.getPromotionName());
                        discountDTOList.add(discountDTO);
                        map.put(saleOrderDiscount.getPromotionProgramId(), discountDTO);
                    }
                }
            }
        }

        return discountDTOList;
    }

    public List<PromotionDTO> getPromotion(List<SaleOrderDetail> saleOrderDetails/*, List<Product> products*/) {
        if(saleOrderDetails == null || saleOrderDetails.isEmpty()) return null;
        List<PromotionDTO> promotionDTOList = new ArrayList<>();

        for (SaleOrderDetail promotionDetail : saleOrderDetails) {
            if(promotionDetail.getIsFreeItem()) {
                PromotionDTO promotionDTO = new PromotionDTO();
                /*if (products != null) {
                    for (Product product : products) {
                        if (product.getId().equals(promotionDetail.getProductId())) {
                            promotionDTO.setProductNumber(product.getProductCode());
                            promotionDTO.setProductName(product.getProductName());
                            break;
                        }
                    }
                }*/
                promotionDTO.setProductNumber(promotionDetail.getProductCode());
                promotionDTO.setProductName(promotionDetail.getProductName());
                promotionDTO.setQuantity(promotionDetail.getQuantity());
                promotionDTO.setPromotionProgramName(promotionDetail.getPromotionName());
                promotionDTOList.add(promotionDTO);
            }
        }
        return promotionDTOList;
    }

    public PrintSaleOrderDTO createPrintSaleOrderDTO(Long shopId, CustomerDTO customer, SaleOrder saleOrder
    , List<SaleOrderDetail> lstSaleOrderDetail, List<SaleOrderDiscount> lstSaleOrderDiscount){
        if (shopId == null || customer == null || saleOrder == null)
            return null;

        ShopDTO shop = shopClient.getByIdV1(shopId).getData();
        UserDTO user = userClient.getUserByIdV1(saleOrder.getSalemanId());
        if (shop == null) return null;

        PrintSaleOrderDTO print = new PrintSaleOrderDTO();
        print.setShopName(shop.getShopName());
        print.setShopAddress(shop.getAddress());
        print.setShopPhone(shop.getPhone());
        print.setShopEmail(shop.getEmail());
        print.setCustomerName(customer.getFullName());
        print.setCustomerPhone(customer.getPhone());
        print.setCustomerAddress(customer.getAddress());
        if(user != null)
            print.setUserName(user.getFullName());
        print.setOrderDate(saleOrder.getOrderDate());
        print.setOrderNumber(saleOrder.getOrderNumber());
        print.setCustomerPurchase(saleOrder.getCustomerPurchase());
        print.setAmount(saleOrder.getAmount());
        double amountNotVat = 0;
        //map ctkm với các sản phẩm mua
        HashMap<String, PrintProductSaleOrderDTO> details = new HashMap<>();
        //map ctkm với sản phẩm tặng của zv 21 và zn
        HashMap<String, List<PrintFreeItemDTO>> freeItems = new HashMap<>();

        for(SaleOrderDetail item : lstSaleOrderDetail){
            if(item.getIsFreeItem() != null && !item.getIsFreeItem()){
                if(item.getPriceNotVat() != null && item.getQuantity() != null)
                    amountNotVat += item.getQuantity() * item.getPriceNotVat();
                PrintOrderItemDTO itemDTO = new PrintOrderItemDTO();
                itemDTO.setPrice(item.getPrice());
                itemDTO.setQuantity(item.getQuantity());
                itemDTO.setProductName(item.getProductName());
                itemDTO.setProductCode(item.getProductCode());
                itemDTO.setTotalPrice(item.getAmount());
                itemDTO.setProductId(item.getProductId());

                if(details.containsKey(item.getPromotionCode())){
                    details.get(item.getPromotionCode()).getListOrderItems().add(itemDTO);
                }else{
                    PrintProductSaleOrderDTO orderDTO = new PrintProductSaleOrderDTO();
                    orderDTO.setDisplayType(0);
                    orderDTO.setGroupName(item.getPromotionCode());
                    List<PrintOrderItemDTO> listOrderItems = new ArrayList<>();
                    listOrderItems.add(itemDTO);
                    orderDTO.setListOrderItems(listOrderItems);
                    details.put(item.getPromotionCode(), orderDTO);
                }
            }else if(item.getIsFreeItem() != null && item.getIsFreeItem() && item.getPromotionType() != null &&
                    ("zv21".equalsIgnoreCase(item.getPromotionType().trim()) || "zm".equalsIgnoreCase(item.getPromotionType().trim()))){
                PrintFreeItemDTO freeItem = new PrintFreeItemDTO();
                freeItem.setQuantity(item.getQuantity());
                freeItem.setProductName(item.getProductName());
                freeItem.setProductCode(item.getProductCode());
                if(freeItems.containsKey(item.getPromotionName())){
                    freeItems.get(item.getPromotionName()).add(freeItem);
                }else{
                    List<PrintFreeItemDTO> lst = new ArrayList<>();
                    lst.add(freeItem);
                    freeItems.put(item.getPromotionName(), lst);
                }
            }
        }
        //add free item
        for (SaleOrderDetail item : lstSaleOrderDetail) {
            if (item.getIsFreeItem() != null && item.getIsFreeItem() && item.getPromotionType() != null
                    && !"zv21".equalsIgnoreCase(item.getPromotionType().trim()) && !"zm".equalsIgnoreCase(item.getPromotionType().trim())) {
                for (Map.Entry<String, PrintProductSaleOrderDTO> group : details.entrySet()) {
                    if (group.getKey() != null && group.getKey().contains(item.getPromotionCode())) {
                        PrintFreeItemDTO itemDTO = new PrintFreeItemDTO();
                        itemDTO.setQuantity(item.getQuantity());
                        itemDTO.setProductName(item.getProductName());
                        itemDTO.setProductCode(item.getProductCode());

                        if (group.getValue().getListFreeItems() == null) {
                            group.getValue().setListFreeItems(new ArrayList<>());
                        }
                        group.getValue().getListFreeItems().add(itemDTO);
                        break;
                    }
                }
            }
        }
        //add km
        List<String> lstCheck = Arrays.asList("zv19", "zv20", "zv23");
        PrintZMZV19ZV20ZV23DTO zMZV19ZV20ZV23 = new PrintZMZV19ZV20ZV23DTO();
        HashMap<String,PrintZMZV19ZV20ZV23DTO> lstZM = new HashMap<>();
        for (SaleOrderDiscount item : lstSaleOrderDiscount) {
            if (item.getPromotionType() != null && "zm".equalsIgnoreCase(item.getPromotionType().trim() ) && item.getDiscountAmountVat() != null) {
                if(lstZM.containsKey(item.getPromotionCode())){
                    lstZM.get(item.getPromotionCode()).setAmount(lstZM.get(item.getPromotionCode()).getAmount() + item.getDiscountAmountVat());
                }else{
                    PrintZMZV19ZV20ZV23DTO zm = new PrintZMZV19ZV20ZV23DTO();
                    zm.setPromotionName(item.getPromotionName());
                    zm.setPromotionCode(item.getPromotionCode());
                    zm.setAmount((double) item.getDiscountAmountVat());
                    lstZM.put(item.getPromotionCode(), zm);
                }
            }else if (item.getPromotionType() != null && lstCheck.contains(item.getPromotionType().trim() ) ) {
                double amount = 0;
                if (zMZV19ZV20ZV23.getAmount() != null) amount = zMZV19ZV20ZV23.getAmount();
                if (item.getDiscountAmount() != null) amount += item.getDiscountAmount();
                zMZV19ZV20ZV23.setAmount(amount);
                if(zMZV19ZV20ZV23.getPromotionCode() == null){
                    zMZV19ZV20ZV23.setPromotionCode(item.getPromotionCode());
                    zMZV19ZV20ZV23.setPromotionName(item.getPromotionName());
                }else{
                    if(!zMZV19ZV20ZV23.getPromotionCode().contains(item.getPromotionCode())) {
                        zMZV19ZV20ZV23.setPromotionCode(zMZV19ZV20ZV23.getPromotionCode() + ", " + item.getPromotionCode());
                        zMZV19ZV20ZV23.setPromotionName(zMZV19ZV20ZV23.getPromotionName() + ", " + item.getPromotionName());
                    }
                }
            }else {
                for (Map.Entry<String, PrintProductSaleOrderDTO> group : details.entrySet()) {
                    if (group.getKey() != null && group.getKey().contains(item.getPromotionCode())) {
                        for (PrintOrderItemDTO i : group.getValue().getListOrderItems()){
                            if(item.getProductId() == i.getProductId()){
                                double amount = 0;
                                if(i.getTotalDiscountPrice() != null) amount = i.getTotalDiscountPrice();
                                if (item.getDiscountAmountVat() != null) amount += -item.getDiscountAmountVat();
                                i.setTotalDiscountPrice(amount);
                                i.setDiscountPrice(-(double)Math.round(i.getTotalDiscountPrice()/i.getQuantity()));
                            }
                        }
                        break;
                    }
                }
            }
        }
        print.setTotal(saleOrder.getTotal());
        print.setTotalNotVat(print.getAmountNotVAT());
        print.setAmountNotVAT(amountNotVat);
        print.setExtraAmount(saleOrder.getBalance());
        print.setPaymentAmount(saleOrder.getTotalPaid());
        if (saleOrder.getTotalVoucher() != null)
            print.setVoucherAmount(-saleOrder.getTotalVoucher());
        if(saleOrder.getMemberCardAmount() != null)
            print.setAccumulatedAmount(-saleOrder.getMemberCardAmount());
        if(saleOrder.getDiscountCodeAmount() != null)
            print.setDiscountAmount(-saleOrder.getDiscountCodeAmount());
        if(saleOrder.getTotalPromotion() != null)
            print.setPromotionAmount(-saleOrder.getTotalPromotion());
        if(saleOrder.getTotalPromotionNotVat() != null) {
            print.setPromotionAmountNotVat(-saleOrder.getTotalPromotionNotVat());
            print.setTotalNotVat(print.getAmountNotVAT() - saleOrder.getTotalPromotionNotVat());
        }

//        if(!zv21Products.isEmpty()) print.setZv21Products(zv21Products);
        if(!details.isEmpty()){
            List<PrintProductSaleOrderDTO> printProductSaleOrderDTO = new ArrayList<>(details.values());
            if(!freeItems.isEmpty()) {
                for(Map.Entry<String, List<PrintFreeItemDTO>> entry : freeItems.entrySet()){
                    PrintProductSaleOrderDTO km = new PrintProductSaleOrderDTO();
                    km.setDisplayType(1);
                    km.setGroupName(entry.getKey());
                    km.setListFreeItems(entry.getValue());
                    printProductSaleOrderDTO.add(km);
                }
            }
            print.setProducts(printProductSaleOrderDTO);
        }
        List<PrintZMZV19ZV20ZV23DTO> lstZMValue = new ArrayList<>(lstZM.values());
        if(zMZV19ZV20ZV23.getAmount() != null) lstZMValue.add(zMZV19ZV20ZV23);
        if(!lstZMValue.isEmpty()) print.setLstZM(lstZMValue);
        return print;
    }

    public PrintSaleOrderDTO printSaleOrder(Long id, Long shopId) {
        SaleOrder saleOrder = saleOrderRepository.findById(id).get();
        CustomerDTO customer = customerClient.getCustomerByIdV1(saleOrder.getCustomerId()).getData();
        List<SaleOrderDiscount> lstSaleOrderDiscount = saleOrderDiscountRepository.findAllBySaleOrderId(saleOrder.getId());
        List<SaleOrderDetail> lstSaleOrderDetail = saleOrderDetailRepository.findSaleOrderDetail(id, false);
        return createPrintSaleOrderDTO(shopId, customer, saleOrder, lstSaleOrderDetail, lstSaleOrderDiscount);
    }

    @Override
    public Page<SaleOrderDTO> getAllBillOfSaleList(RedInvoiceFilter redInvoiceFilter, Long shopId, Pageable pageable) {
        String customerName, customerCode;

        List<SaleOrderDTO> saleOrdersList = new ArrayList<>();
        List<Long> ids = customerClient.getIdCustomerBySearchKeyWordsV1(redInvoiceFilter.getSearchKeywords()).getData();
        if (ids.size() == 0 || ids.isEmpty()) {
            throw new ValidateException(ResponseMessage.SALE_ORDER_NOT_FOUND);
        }
        LocalDateTime fromDate = redInvoiceFilter.getFromDate();
        LocalDateTime toDate = redInvoiceFilter.getToDate();
        if(fromDate == null) fromDate = DateUtils.getFirstDayOfCurrentMonth();
        if(toDate == null) toDate = LocalDateTime.now();
        List<SaleOrder> saleOrders = repository.getAllBillOfSaleList(shopId,redInvoiceFilter.getOrderNumber(), ids, fromDate, toDate, PageRequest.of(0, 5000)).getContent();
        if (saleOrders.isEmpty() || saleOrders.size() == 0) {
            throw new ValidateException(ResponseMessage.SALE_ORDER_NOT_FOUND);
        }
        CustomerDTO customer = null;
        List<CustomerDTO> customers = customerClient.getCustomerInfoV1(null, saleOrders.stream().map(item -> item.getCustomerId())
                .distinct().collect(Collectors.toList()));
        for (SaleOrder so : saleOrders) {
            if(customers != null){
                for (CustomerDTO customer1 : customers){
                    if(customer1.getId().equals(so.getCustomerId())){
                        customer = customer1;
                        break;
                    }
                }
            }
            if (customer == null) {
                throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
            }
            customerName = customer.getFullName();
            customerCode = customer.getCustomerCode();

            SaleOrderDTO saleOrder = new SaleOrderDTO();
            saleOrder.setSaleOrderID(so.getId());
            saleOrder.setOrderNumber(so.getOrderNumber());
            saleOrder.setCustomerId(so.getCustomerId());
            saleOrder.setCustomerNumber(customerCode);
            saleOrder.setCustomerName(customerName);
            saleOrder.setOrderDate(so.getOrderDate());
            if (so.getAutoPromotion() == null)
                so.setAutoPromotion((double) 0);
            if (so.getZmPromotion() == null)
                so.setZmPromotion((double) 0);
            if (so.getTotalVoucher() == null)
                so.setTotalPromotion((double) 0);
            if (so.getDiscountCodeAmount() == null)
                so.setDiscountCodeAmount((double) 0);
            saleOrder.setTotalPromotion(so.getAutoPromotion() + so.getZmPromotion() + so.getTotalVoucher() + so.getDiscountCodeAmount()); //tiền giảm giá
            if (so.getCustomerPurchase() == null)
                so.setCustomerPurchase((double) 0);
            saleOrder.setCustomerPurchase(so.getCustomerPurchase());//tiền tích lũy
            if (so.getTotal() == null)
                so.setTotal((double) 0);
            saleOrder.setTotal(so.getTotal());//tiền phải trả

            saleOrdersList.add(saleOrder);
        }
        Page<SaleOrderDTO> saleOrderResponse = new PageImpl<>(saleOrdersList);
        return saleOrderResponse;
    }

    @Override
    public SaleOrderDTO getLastSaleOrderByCustomerId(Long customerId) {
        SaleOrder saleOrder = repository.getLastSaleOrderByCustomerId(customerId).orElse(null);
        if (saleOrder == null)
            throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST_IN_SALE_ORDER);
        SaleOrderDTO saleOrderDTO = modelMapper.map(saleOrder, SaleOrderDTO.class);
        return saleOrderDTO;
    }

    @Override
    public Double getTotalBillForTheMonthByCustomerId(Long customerId, LocalDateTime lastOrderDate) {
        if(lastOrderDate == null){
            return 0D;
        }
        else{
            LocalDate firstMonth = LocalDate.now().withDayOfMonth(1);
            Double total = saleOrderRepository.getTotalBillForTheMonthByCustomerId(customerId, firstMonth, lastOrderDate.toLocalDate());
            if(total == null){
                return 0D;
            }
            else
                return total;
        }
    }
}
