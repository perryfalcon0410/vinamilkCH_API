package vn.viettel.sale.service.impl;

import com.amazonaws.services.dynamodbv2.xspec.L;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
                totalResponse.addTotalAmount(order.getAmount()).addAllTotal(order.getTotal());
            }

        }
        Page<SaleOrderDTO> saleOrderDTOS = findAll.map(this::mapSaleOrderDTO);

        CoverResponse coverResponse = new CoverResponse(saleOrderDTOS, totalResponse);
        return coverResponse;
    }

    private SaleOrderDTO mapSaleOrderDTO(SaleOrder saleOrder) {
        String customerName, customerCode, saleManName;
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        SaleOrderDTO dto = modelMapper.map(saleOrder, SaleOrderDTO.class);
        UserDTO user = userClient.getUserByIdV1(saleOrder.getSalemanId());
        CustomerDTO customer = customerClient.getCustomerByIdV1(saleOrder.getCustomerId()).getData();
        customerName = customer.getLastName() + " " + customer.getFirstName();
        customerCode = customer.getCustomerCode();
        saleManName = user.getLastName() + " " + user.getFirstName();
        dto.setCustomerNumber(customerCode);
        dto.setCustomerName(customerName);
        dto.setSalesManName(saleManName);
        return dto;
    }

    public SaleOrderDetailDTO getSaleOrderDetail(long saleOrderId, String orderNumber) {
        SaleOrderDetailDTO orderDetail = new SaleOrderDetailDTO();
        orderDetail.setOrderDetail(getDetail(saleOrderId));
        orderDetail.setInfos(getInfos(saleOrderId, orderNumber));
        orderDetail.setDiscount(getDiscount(saleOrderId, orderNumber));
        orderDetail.setPromotion(getPromotion(saleOrderId));
        return orderDetail;
    }

    public InfosOrderDetailDTO getInfos(long saleOrderId, String orderNumber) {
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

    public CoverResponse<List<OrderDetailDTO>, OrderDetailTotalResponse> getDetail(long saleOrderId) {
        int totalQuantity = 0;
        double totalAmount = 0, totalDiscount = 0, totalPayment = 0;
        List<SaleOrderDetail> saleOrderDetails = saleOrderDetailRepository.findSaleOrderDetail(saleOrderId, false);
        List<OrderDetailDTO> saleOrderDetailList = new ArrayList<>();
        for (SaleOrderDetail saleOrderDetail : saleOrderDetails) {
            Product product = productRepository.findById(saleOrderDetail.getProductId()).get();
            OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
            orderDetailDTO.setProductId(saleOrderDetail.getProductId());
            orderDetailDTO.setProductCode(product.getProductCode());
            orderDetailDTO.setProductName(product.getProductName());
            orderDetailDTO.setUnit(product.getUom1());
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
                discountDTO.setPromotionName(voucher.getVoucherCode());
                discountDTO.setPromotionType(null);
                discountDTO.setVoucherType(voucher.getVoucherName());
                discountDTO.setDiscountPrice(voucher.getPrice());
                discountDTOList.add(discountDTO);
            }
        }

        List<PromotionProgramDiscountDTO> promotionProgramDiscounts =
                promotionClient.listPromotionProgramDiscountByOrderNumberV1(orderNumber).getData();
        if (promotionProgramDiscounts.size() > 0) {
            for (PromotionProgramDiscountDTO promotionProgramDiscount : promotionProgramDiscounts) {
                DiscountDTO discountDTO = new DiscountDTO();
                discountDTO.setDiscountPrice(promotionProgramDiscount.getDiscountAmount());
                discountDTO.setDiscountPercent(promotionProgramDiscount.getDiscountPercent());
                discountDTO.setVoucherType(null);
                if (promotionProgramDiscount.getType() == 0)
                    discountDTO.setPromotionType("Chiết khấu tiền");
                if (promotionProgramDiscount.getType() == 2)
                    discountDTO.setPromotionType("Mã giảm giá");
                if (promotionProgramDiscount.getType() == 3)
                    discountDTO.setPromotionType("Giảm giá khách hàng");
                PromotionProgramDTO promotionProgram =
                        promotionClient.getByIdV1(promotionProgramDiscount.getId()).getData();
                discountDTO.setPromotionName(promotionProgram.getPromotionProgramName());
                discountDTOList.add(discountDTO);
            }
        }
        return discountDTOList;
    }

    public List<PromotionDTO> getPromotion(long saleOrderId) {
        List<PromotionDTO> promotionDTOList = new ArrayList<>();
        List<SaleOrderDetail> saleOrderPromotions = saleOrderDetailRepository.findSaleOrderDetail(saleOrderId, true);
        for (SaleOrderDetail promotionDetail : saleOrderPromotions) {
            Product product = productRepository.findById(promotionDetail.getProductId()).get();
            PromotionDTO promotionDTO = new PromotionDTO();
            promotionDTO.setProductNumber(product.getProductCode());
            promotionDTO.setProductName(product.getProductName());
            promotionDTO.setQuantity(promotionDetail.getQuantity());
            promotionDTO.setPromotionProgramName(promotionDetail.getPromotionName());
            promotionDTOList.add(promotionDTO);
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
        List<Long> idr = repository.getFromSaleId();
        List<SaleOrder> saleOrders = new ArrayList<>();
        if (redInvoiceFilter.getFromDate() == null && redInvoiceFilter.getToDate() != null) {
            LocalDateTime dateTime = DateUtils.getFirstDayOfCurrentMonth();
            saleOrders = repository.getAllBillOfSaleList(redInvoiceFilter.getOrderNumber(), ids, dateTime, redInvoiceFilter.getToDate(), idr, shopId);
        } else if (redInvoiceFilter.getFromDate() != null && redInvoiceFilter.getToDate() == null) {
            LocalDateTime dateTime = LocalDateTime.now();
            saleOrders = repository.getAllBillOfSaleList(redInvoiceFilter.getOrderNumber(), ids, redInvoiceFilter.getFromDate(), dateTime, idr, shopId);
        } else if (redInvoiceFilter.getFromDate() == null && redInvoiceFilter.getToDate() == null) {
            LocalDateTime fromDate = DateUtils.getFirstDayOfCurrentMonth();
            LocalDateTime toDate = LocalDateTime.now();
            saleOrders = repository.getAllBillOfSaleList(redInvoiceFilter.getOrderNumber(), ids, fromDate, toDate, idr, shopId);
        } else {
            saleOrders = repository.getAllBillOfSaleList(redInvoiceFilter.getOrderNumber(), ids, redInvoiceFilter.getFromDate(), redInvoiceFilter.getToDate(), idr, shopId);
        }
        if (saleOrders.size() == 0 || saleOrders.isEmpty()) {
            List<SaleOrder> saleOrderList = new ArrayList<>();
        } else {
            CustomerDTO customer;
            if (saleOrders.isEmpty() || saleOrders.size() == 0) {
                throw new ValidateException(ResponseMessage.SALE_ORDER_NOT_FOUND);
            }
            for (SaleOrder so : saleOrders) {
                customer = customerClient.getCustomerByIdV1(so.getCustomerId()).getData();
                if (customer == null) {
                    throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
                }
                customerName = customer.getLastName() + " " + customer.getFirstName();
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

    public String checkNull(String value) {
        if (value == null) {
            throw new ValidateException(ResponseMessage.TRANS_DATE_MUST_BE_NOT_NULL);
        } else {
            return value;
        }
    }
}
