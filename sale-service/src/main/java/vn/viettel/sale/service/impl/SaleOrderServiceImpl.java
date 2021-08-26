package vn.viettel.sale.service.impl;

import com.amazonaws.services.dynamodbv2.xspec.L;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.SortDTO;
import vn.viettel.core.dto.UserDTO;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.promotion.PromotionProgramDTO;
import vn.viettel.core.dto.promotion.PromotionProgramDiscountDTO;
import vn.viettel.core.dto.voucher.VoucherDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.entities.*;
import vn.viettel.sale.messaging.OrderDetailTotalResponse;
import vn.viettel.sale.messaging.RedInvoiceFilter;
import vn.viettel.sale.messaging.SaleOrderFilter;
import vn.viettel.sale.messaging.SaleOrderTotalResponse;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.SaleOrderService;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.service.feign.*;
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

    @Autowired
    ApparamClient apparamClient;

    @Value( "${sale.delivery.type.apparam}" )
    private String apParamDeliveryType;

    @Override
    public CoverResponse<Page<SaleOrderDTO>, SaleOrderTotalResponse> getAllSaleOrder(SaleOrderFilter saleOrderFilter, Pageable pageable, Long shopId) {
        List<Long> customerIds = null;
        int type = 1;
        List<SortDTO> customerSorts = new ArrayList<>();
        Sort orderSort = null;
        List<Long> customerIdsSort = null;
        if(pageable.getSort() != null) {
            for (Sort.Order order : pageable.getSort()) {
                if(order.getProperty().equals("customerNumber")){
                    customerSorts.add(new SortDTO("customerCode", order.getDirection().toString()));
                }else if(order.getProperty().equals("customerName")){
                    customerSorts.add(new SortDTO("nameText", order.getDirection().toString()));
                }else{
                    Sort sorted = Sort.by(order.getDirection(), order.getProperty());
                    if(orderSort == null) orderSort = sorted;
                    else orderSort.and(sorted);
                }
            }
        }

        if(saleOrderFilter.getSearchKeyword() != null || saleOrderFilter.getCustomerPhone() != null){
            customerIds = customerClient.getIdCustomerByV1(saleOrderFilter.getSearchKeyword(), saleOrderFilter.getCustomerPhone()).getData();
            if (customerIds == null || customerIds.isEmpty()) {
                customerIds = Arrays.asList(-1L);
            }
        }

        Pageable orderPage = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        if(orderSort != null) orderPage = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), orderSort);

        String orderNumber = saleOrderFilter.getOrderNumber();
        if(orderNumber != null) orderNumber = orderNumber.trim().toUpperCase();
        LocalDateTime fromDate = DateUtils.convertFromDate(saleOrderFilter.getFromDate());
        LocalDateTime toDate = DateUtils.convertToDate(saleOrderFilter.getToDate());

        List<Long> saleCusIds = repository.getCustomerIds( fromDate, toDate, orderNumber, type, shopId, saleOrderFilter.getUsedRedInvoice());
        if(saleCusIds.isEmpty()) return null;
        List<CustomerDTO> customers = customerClient.getCustomerInfoV1(customerSorts,null,saleCusIds);

        Page<SaleOrder> findAll = null;
        if(customerSorts == null || customerSorts.isEmpty()) {
            findAll = repository.findALlSales(customerIds, fromDate, toDate, orderNumber, type, shopId, saleOrderFilter.getUsedRedInvoice(), orderPage);
        }else{
            customerIdsSort = new ArrayList<>();
            long i =  0;
            for(CustomerDTO customer : customers) {
                customerIdsSort.add(customer.getId());
                customerIdsSort.add(i);
                i++;
            }
            findAll = repository.findALlSales(customerIds, fromDate, toDate, orderNumber, type, shopId, saleOrderFilter.getUsedRedInvoice(), customerIdsSort, orderPage);
        }

        SaleOrderTotalResponse totalResponse = repository.getSaleOrderTotal(shopId, customerIds, orderNumber, type, saleOrderFilter.getUsedRedInvoice(),
                fromDate, toDate);
        List<UserDTO> users = userClient.getUserByIdsV1(findAll.getContent().stream().map(item -> item.getSalemanId())
                .distinct().filter(Objects::nonNull).collect(Collectors.toList()));
        Page<SaleOrderDTO> saleOrderDTOS = findAll.map(item -> mapSaleOrderDTO(item, customers, users));

        CoverResponse coverResponse = new CoverResponse(saleOrderDTOS, totalResponse);

        return coverResponse;
    }

    private SaleOrderDTO mapSaleOrderDTO(SaleOrder saleOrder, List<CustomerDTO> customers, List<UserDTO> users) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        SaleOrderDTO dto = modelMapper.map(saleOrder, SaleOrderDTO.class);
        dto.setCustomerPurchase(saleOrder.getMemberCardAmount());
        dto.setTotalPromotion(saleOrder.getTotalPromotionVat());
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
        if(!saleOrderDetails.isEmpty()) {
            List<Product> products = productRepository.getProducts(saleOrderDetails.stream().map(item -> item.getProductId()).distinct().collect(Collectors.toList()), null);
            orderDetail.setOrderDetail(getDetail(saleOrderDetails, products));
            orderDetail.setPromotion(getPromotion(saleOrderDetails));
        }
        orderDetail.setInfos(getInfos(saleOrderId, orderNumber));
        orderDetail.setDiscount(getDiscount(saleOrderId, orderNumber));
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

        infosOrderDetailDTO.setMemberCardAmount(saleOrder.getMemberCardAmount());
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
            if(saleOrderDetail.getIsFreeItem() == null || !saleOrderDetail.getIsFreeItem()) {
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
                double discount = saleOrderDetail.getAmount() - saleOrderDetail.getTotal();
                if(discount < 0) {
                    discount = discount * -1;
                }else orderDetailDTO.setDiscount(discount);

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
        SaleOrder saleOrder = repository.getById(saleOrderId);
        if(saleOrder == null) return discountDTOList;

        //tiền voucher
        List<VoucherDTO> voucherDTOS = promotionClient.getVoucherBySaleOrderIdV1(saleOrderId).getData();
        int count = 0;
        double total = 0;
        if (voucherDTOS.size() > 0) {
            for (VoucherDTO voucher : voucherDTOS) {
                count++;
                DiscountDTO discountDTO = new DiscountDTO();
                discountDTO.setPromotionName(voucher.getVoucherName());
                discountDTO.setPromotionType("Voucher");
                discountDTO.setVoucherType(voucher.getVoucherCode());
                discountDTO.setDiscountPrice(voucher.getPrice());
                if(count == voucherDTOS.size())
                    discountDTO.setDiscountPrice(saleOrder.getTotalVoucher() - total);
                total += voucher.getPrice();
                discountDTOList.add(discountDTO);
            }
        }

        List<PromotionProgramDiscountDTO> promotionProgramDiscounts =
                promotionClient.listPromotionProgramDiscountByOrderNumberV1(orderNumber).getData();
        if (promotionProgramDiscounts != null) {
            count = 0;
            total = 0;
            for (PromotionProgramDiscountDTO promotionProgramDiscount : promotionProgramDiscounts) {
                DiscountDTO discountDTO = new DiscountDTO();
                discountDTO.setDiscountPrice(promotionProgramDiscount.getDiscountAmount());
                if(count == promotionProgramDiscounts.size())
                    discountDTO.setDiscountPrice(saleOrder.getDiscountCodeAmount() - total);
                total += promotionProgramDiscount.getDiscountAmount();
                discountDTO.setPromotionType("Mã giảm giá");
                PromotionProgramDTO promotionProgram = promotionClient.getByIdV1(promotionProgramDiscount.getId()).getData();
                if(promotionProgram!=null) {
                    discountDTO.setPromotionName(promotionProgram.getPromotionProgramName());
                    discountDTO.setVoucherType(promotionProgram.getPromotionProgramCode());
                }
                discountDTOList.add(discountDTO);
            }
        }

        List<SaleOrderDiscount> lstSaleOrderDiscount = saleOrderDiscountRepository.findAllBySaleOrderId(saleOrderId);
        if(lstSaleOrderDiscount != null){
            HashMap<Long,DiscountDTO> map = new HashMap<>();
            for(SaleOrderDiscount saleOrderDiscount : lstSaleOrderDiscount){
                if(saleOrderDiscount.getDiscountAmountVat() != null) {
                    if (map.containsKey(saleOrderDiscount.getPromotionProgramId())) {
                        map.get(saleOrderDiscount.getPromotionProgramId()).setDiscountPrice(
                                map.get(saleOrderDiscount.getPromotionProgramId()).getDiscountPrice() + saleOrderDiscount.getDiscountAmountVat().doubleValue()
                        );
                    } else {
                        DiscountDTO discountDTO = new DiscountDTO();
                        discountDTO.setDiscountPrice(saleOrderDiscount.getDiscountAmountVat().doubleValue());
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

    public List<PromotionDTO> getPromotion(List<SaleOrderDetail> saleOrderDetails) {
        if(saleOrderDetails == null || saleOrderDetails.isEmpty()) return null;
        List<PromotionDTO> promotionDTOList = new ArrayList<>();
        for (SaleOrderDetail promotionDetail : saleOrderDetails) {
            if(promotionDetail.getIsFreeItem()) {
                PromotionDTO promotionDTO = new PromotionDTO();
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

        ApParamDTO apParam = apparamClient.getApParamByTypeAndvalue(apParamDeliveryType, saleOrder.getDeliveryType().toString()).getData();

        PrintSaleOrderDTO print = new PrintSaleOrderDTO();
        print.setShopName(shop.getShopName());
        print.setShopAddress(shop.getAddress());
        print.setShopPhone(shop.getPhone());
        print.setShopEmail(shop.getEmail());
        print.setCustomerName(customer.getFullName());
        print.setCustomerPhone(customer.getMobiPhone());
        print.setCustomerAddress(customer.getAddress());
        if(user != null)
            print.setUserName(user.getFullName());
        print.setOrderDate(saleOrder.getOrderDate());
        print.setOrderNumber(saleOrder.getOrderNumber());
        if(saleOrder.getId() == null){
            print.setCustomerPurchase( (customer.getTotalBill()!=null?customer.getTotalBill():0.0)
                    + (saleOrder.getCustomerPurchase()!=null?saleOrder.getCustomerPurchase():0.0));
        }else{
            print.setCustomerPurchase((saleOrder.getTotalCustomerPurchase()!=null?saleOrder.getTotalCustomerPurchase():0.0)
                    +(saleOrder.getCustomerPurchase()!=null?saleOrder.getCustomerPurchase():0.0));
        }

        print.setAmount(saleOrder.getAmount());
        if(apParam!=null) print.setDeliveryType(apParam.getApParamName());
        double amountNotVat = 0;
        //map ctkm với các sản phẩm mua
        HashMap<String, PrintProductSaleOrderDTO> details = new HashMap<>();
        //map ctkm với sản phẩm tặng của zv 21 và zm
        HashMap<String, List<PrintFreeItemDTO>> freeItems = new HashMap<>();

        for(SaleOrderDetail item : lstSaleOrderDetail){
            if(item.getIsFreeItem() == null || item.getIsFreeItem() == false){
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
                    (item.getPromotionType().trim().toLowerCase().contains("zv21") || item.getPromotionType().trim().toLowerCase().contains("zm"))){
                if(freeItems.containsKey(item.getPromotionName())){
                    List<PrintFreeItemDTO> lst = freeItems.get(item.getPromotionName());
                    PrintFreeItemDTO freeItem = null;
                    for(PrintFreeItemDTO fE : lst){
                        if(fE.getProductCode().equals(item.getProductCode())){
                            freeItem = fE;
                            lst.remove(fE);
                            break;
                        }
                    }
                    if(freeItem == null){
                        freeItem = new PrintFreeItemDTO();
                        freeItem.setQuantity(item.getQuantity());
                        freeItem.setProductName(item.getProductName());
                        freeItem.setProductCode(item.getProductCode());
                    }else{
                        freeItem.setQuantity(freeItem.getQuantity() + item.getQuantity());
                    }
                    lst.add(freeItem);
                    freeItems.put(item.getPromotionName(), lst);
                }else{
                    List<PrintFreeItemDTO> lst = new ArrayList<>();
                    PrintFreeItemDTO freeItem = new PrintFreeItemDTO();
                    freeItem.setQuantity(item.getQuantity());
                    freeItem.setProductName(item.getProductName());
                    freeItem.setProductCode(item.getProductCode());
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
                        PrintFreeItemDTO freeItem = null;
                        if (group.getValue().getListFreeItems() == null) {
                            group.getValue().setListFreeItems(new ArrayList<>());
                        }

                        for(PrintFreeItemDTO fE : group.getValue().getListFreeItems()){
                            if(fE.getProductCode().equals(item.getProductCode())){
                                freeItem = fE;
                                group.getValue().getListFreeItems().remove(fE);
                                break;
                            }
                        }
                        if(freeItem == null){
                            freeItem = new PrintFreeItemDTO();
                            freeItem.setQuantity(item.getQuantity());
                            freeItem.setProductName(item.getProductName());
                            freeItem.setProductCode(item.getProductCode());
                        }else{
                            freeItem.setQuantity(freeItem.getQuantity() + item.getQuantity());
                        }
                        group.getValue().getListFreeItems().add(freeItem);
                        break;
                    }
                }
            }
        }
        //add km
        List<String> lstCheck = Arrays.asList("ZV19", "ZV20", "ZV23");

        HashMap<String,PrintZMZV19ZV20ZV23DTO> lstZM = new HashMap<>();
        for (SaleOrderDiscount item : lstSaleOrderDiscount) {
            if (item.getPromotionType() != null && ("zm".equalsIgnoreCase(item.getPromotionType().trim() )
            || lstCheck.contains(item.getPromotionType().trim() ) ) && item.getDiscountAmountVat() != null) {
                if(lstZM.containsKey(item.getPromotionCode())){
                    lstZM.get(item.getPromotionCode()).setAmount(lstZM.get(item.getPromotionCode()).getAmount() - item.getDiscountAmountVat());
                }else{
                    PrintZMZV19ZV20ZV23DTO zm = new PrintZMZV19ZV20ZV23DTO();
                    zm.setPromotionType(item.getPromotionType());
                    zm.setPromotionName(item.getPromotionName());
                    zm.setPromotionCode(item.getPromotionCode());
                    zm.setAmount(-item.getDiscountAmountVat());
                    lstZM.put(item.getPromotionCode(), zm);
                }
            }else {
                for (Map.Entry<String, PrintProductSaleOrderDTO> group : details.entrySet()) {
                    if (group.getKey() != null && group.getKey().contains(item.getPromotionCode())) {
                        for (PrintOrderItemDTO i : group.getValue().getListOrderItems()){
                            if(item.getProductId().equals(i.getProductId())){
                                double amount = 0;
                                if(i.getTotalDiscountPrice() != null) amount = i.getTotalDiscountPrice();
                                if (item.getDiscountAmountVat() != null) amount += -item.getDiscountAmountVat();
                                i.setTotalDiscountPrice(amount);
                                i.setDiscountPrice(-(double)Math.round(i.getTotalDiscountPrice()/i.getQuantity()));
                            }
                        }
                    }
                }
            }
        }
        print.setTotal(saleOrder.getTotal());
        print.setAmountNotVAT(amountNotVat);
        print.setTotalNotVat(print.getAmountNotVAT());
        print.setExtraAmount(saleOrder.getBalance());
        print.setPaymentAmount(saleOrder.getTotalPaid());
        if (saleOrder.getTotalVoucher() != null && saleOrder.getTotalVoucher() > 0)
            print.setVoucherAmount(-saleOrder.getTotalVoucher());
        if(saleOrder.getMemberCardAmount() != null && saleOrder.getMemberCardAmount() >0)
            print.setAccumulatedAmount(-saleOrder.getMemberCardAmount());
        if(saleOrder.getTotalPromotionVat() != null && saleOrder.getTotalPromotionVat() > 0)
            print.setPromotionAmount(-saleOrder.getTotalPromotionVat());
        if(saleOrder.getDiscountCodeAmount() != null && saleOrder.getDiscountCodeAmount() > 0){
            Double amountVat = saleOrder.getTotalPromotionVat()!=null?saleOrder.getTotalPromotionVat():0.0;
            print.setPromotionAmount(-(amountVat + saleOrder.getDiscountCodeAmount()));
            print.setDiscountAmount(-saleOrder.getDiscountCodeAmount());
        }

        if(saleOrder.getTotalPromotionNotVat() != null) {
            print.setPromotionAmountNotVat(-saleOrder.getTotalPromotionNotVat());
            print.setTotalNotVat(print.getAmountNotVAT() - saleOrder.getTotalPromotionNotVat());
        }

//        if(!zv21Products.isEmpty()) print.setZv21Products(zv21Products);

        List<PrintProductSaleOrderDTO> printProductSaleOrderDTO = null;
        if(!details.isEmpty()) printProductSaleOrderDTO = new ArrayList<>(details.values());
        if(!freeItems.isEmpty()) {
            if(printProductSaleOrderDTO == null) printProductSaleOrderDTO = new ArrayList<>();
            for(Map.Entry<String, List<PrintFreeItemDTO>> entry : freeItems.entrySet()){
                PrintProductSaleOrderDTO km = new PrintProductSaleOrderDTO();
                km.setDisplayType(1);
                km.setGroupName(entry.getKey());
                km.setListFreeItems(entry.getValue());
                printProductSaleOrderDTO.add(km);
            }
        }
        print.setProducts(printProductSaleOrderDTO);


        List<PrintZMZV19ZV20ZV23DTO> lstZMValue = new ArrayList<>(lstZM.values());
        //Xắp xếp ZM trước sau đó ts các ZV19->23
        Collections.sort(lstZMValue, Comparator.comparing(PrintZMZV19ZV20ZV23DTO::getPromotionType));

        if(!lstZMValue.isEmpty()) print.setLstZM(lstZMValue);

        return print;
    }

    @Override
    public List<String> getTopFiveFavoriteProducts(Long customerId) {
        LocalDateTime toDate = DateUtils.convertToDate(LocalDate.now());
        LocalDateTime fromDate = DateUtils.convertFromDate(toDate.minusMonths(5));
        List<String> topProducts = repository.getTopFiveFavoriteProducts(customerId, fromDate, toDate, PageRequest.of(0,5)).getContent();
        return topProducts;
    }

    public PrintSaleOrderDTO printSaleOrder(Long id, Long shopId) {
        SaleOrder saleOrder = saleOrderRepository.findById(id).get();
        CustomerDTO customer = customerClient.getCustomerByIdV1(saleOrder.getCustomerId()).getData();
        List<SaleOrderDiscount> lstSaleOrderDiscount = saleOrderDiscountRepository.findAllBySaleOrderId(saleOrder.getId());
        List<SaleOrderDetail> lstSaleOrderDetail = saleOrderDetailRepository.findSaleOrderDetail(id, null);
        return createPrintSaleOrderDTO(shopId, customer, saleOrder, lstSaleOrderDetail, lstSaleOrderDiscount);
    }

    @Override
    public Page<SaleOrderDTO> getAllBillOfSaleList(RedInvoiceFilter redInvoiceFilter, Long shopId, Pageable pageable) {
        List<Long> customerIds = null;
        if(redInvoiceFilter.getSearchKeywords() != null) {
            customerIds = customerClient.getIdCustomerBySearchKeyWordsV1(redInvoiceFilter.getSearchKeywords().trim()).getData();
            if(customerIds == null || customerIds.isEmpty()) customerIds = Arrays.asList(-1L);
        }
        LocalDateTime fromDate = redInvoiceFilter.getFromDate();
        LocalDateTime toDate = redInvoiceFilter.getToDate();
        if(fromDate == null) fromDate = DateUtils.getFirstDayOfCurrentMonth();
        if(toDate == null) toDate = LocalDateTime.now();
        if(redInvoiceFilter.getOrderNumber() != null) redInvoiceFilter.setOrderNumber(redInvoiceFilter.getOrderNumber().trim().toUpperCase());
        Page<SaleOrder> saleOrders = repository.getAllBillOfSaleList(shopId,redInvoiceFilter.getOrderNumber(), customerIds, fromDate, toDate, pageable);

        List<CustomerDTO> customers = customerClient.getCustomerInfoV1(new ArrayList<>(), null, saleOrders.stream().map(item -> item.getCustomerId())
                .distinct().collect(Collectors.toList()));

        return saleOrders.map(so->{
            SaleOrderDTO saleOrder =  modelMapper.map(so,SaleOrderDTO.class);
            saleOrder.setSaleOrderID(so.getId());
            if(customers != null){
                for (CustomerDTO customer : customers){
                    if(customer.getId().equals(so.getCustomerId())){
                        saleOrder.setCustomerNumber(customer.getCustomerCode());
                        saleOrder.setCustomerName(customer.getFullName());
                        break;
                    }
                }
            }
            if (so.getAutoPromotion() == null)
                so.setAutoPromotion((double) 0);
            if (so.getZmPromotion() == null)
                so.setZmPromotion((double) 0);
            if (so.getTotalVoucher() == null)
                so.setTotalPromotion((double) 0);
            if (so.getDiscountCodeAmount() == null)
                so.setDiscountCodeAmount((double) 0);
            if (so.getTotalPromotionVat() != null)
                saleOrder.setTotalPromotion(so.getTotalPromotionVat());
//            saleOrder.setTotalPromotion(Math.round(so.getAutoPromotion() + so.getZmPromotion() + so.getTotalVoucher() + so.getDiscountCodeAmount())); //tiền giảm giá

            if (so.getCustomerPurchase() == null)
                so.setCustomerPurchase((double) 0);
            if (so.getMemberCardAmount() != null)
                saleOrder.setCustomerPurchase(Math.round(so.getMemberCardAmount()));//tiền tích lũy
            if (so.getTotal() == null)
                so.setTotal((double) 0);
            saleOrder.setTotal(Math.round(so.getTotal()));//tiền phải trả
            return saleOrder;
        });
    }

    @Override
    public SaleOrderDTO getLastSaleOrderByCustomerId(Long customerId) {
        List<SaleOrder> saleOrders = repository.getLastSaleOrderByCustomerId(customerId);
        if (saleOrders == null || saleOrders.isEmpty())
            throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST_IN_SALE_ORDER);
        SaleOrderDTO saleOrderDTO = modelMapper.map(saleOrders.get(0), SaleOrderDTO.class);
        return saleOrderDTO;
    }

    @Override
    public Double getTotalBillForTheMonthByCustomerId(Long customerId, LocalDate lastOrderDate) {
        if(lastOrderDate == null){
            return 0D;
        }
        else{
            LocalDate firstMonth = lastOrderDate.withDayOfMonth(1);
            Double total = saleOrderRepository.getTotalBillForTheMonthByCustomerId(customerId, firstMonth, lastOrderDate);
            if(total == null){
                return 0D;
            }
            else
                return total;
        }
    }
}
