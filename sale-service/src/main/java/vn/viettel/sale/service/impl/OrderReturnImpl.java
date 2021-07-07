package vn.viettel.sale.service.impl;

import org.apache.commons.lang.StringUtils;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.messaging.CustomerRequest;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.UserDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.util.VNCharacterUtils;
import vn.viettel.sale.entities.*;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.messaging.*;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.OrderReturnService;
import vn.viettel.sale.service.SaleService;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.service.feign.*;
import vn.viettel.sale.specification.SaleOderSpecification;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.*;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderReturnImpl extends BaseServiceImpl<SaleOrder, SaleOrderRepository> implements OrderReturnService {
    @Autowired
    SaleOrderDetailRepository saleOrderDetailRepository;
    @Autowired
    UserClient userClient;
    @Autowired
    CustomerClient customerClient;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ShopClient shopClient;
    @Autowired
    ApparamClient apparamClient;
    @Autowired
    StockTotalRepository stockTotalRepository;
    @Autowired
    ComboProductDetailRepository comboDetailRepository;
    @Autowired
    PromotionClient promotionClient;
    @Autowired
    SaleService saleService;
    @Autowired
    SaleOrderDiscountRepository saleDiscount;

    @Override
    public CoverResponse<Page<OrderReturnDTO>, SaleOrderTotalResponse> getAllOrderReturn(SaleOrderFilter saleOrderFilter, Pageable pageable, Long shopId) {
        List<Long> customerIds = customerClient.getIdCustomerByV1(saleOrderFilter.getSearchKeyword(), saleOrderFilter.getCustomerPhone()).getData();
        if(customerIds.size() == 0) {
            return new CoverResponse<>(new PageImpl<>(new ArrayList<>()), new SaleOrderTotalResponse());
        }
        if(saleOrderFilter.getOrderNumber() != null) saleOrderFilter.setOrderNumber(saleOrderFilter.getOrderNumber().trim().toUpperCase());
        Page<SaleOrder> findAll = repository.getSaleOrderReturn(shopId,saleOrderFilter.getOrderNumber(),
                customerIds, saleOrderFilter.getFromDate(), saleOrderFilter.getToDate(), pageable);
        SaleOrderTotalResponse totalResponse = repository.getSumSaleOrderReturn(shopId,saleOrderFilter.getOrderNumber(),
                customerIds, saleOrderFilter.getFromDate(), saleOrderFilter.getToDate());
        List<UserDTO> users = userClient.getUserByIdsV1(findAll.getContent().stream().map(item -> item.getSalemanId()).distinct()
        .filter(Objects::nonNull).collect(Collectors.toList()));
        List<CustomerDTO> customers = customerClient.getCustomerInfoV1(null, customerIds);
        List<SaleOrder> saleOrders = repository.findAllById(findAll.getContent().stream().map(item -> item.getFromSaleOrderId()).collect(Collectors.toList()));
        Page<OrderReturnDTO> orderReturnDTOS = findAll.map(item ->mapOrderReturnDTO(item, users, customers, saleOrders));
        CoverResponse coverResponse = new CoverResponse(orderReturnDTOS, totalResponse);
        return coverResponse;
    }

    private OrderReturnDTO mapOrderReturnDTO(SaleOrder orderReturn, List<UserDTO> users, List<CustomerDTO> customers, List<SaleOrder> saleOrders) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OrderReturnDTO dto = modelMapper.map(orderReturn, OrderReturnDTO.class);
        if(saleOrders != null){
            for(SaleOrder saleOrder : saleOrders){
                if(saleOrder.getId().equals(orderReturn.getFromSaleOrderId())){
                    dto.setOrderNumberRef(saleOrder.getOrderNumber());
                    break;
                }
            }
        }
        if(users != null){
            for(UserDTO user : users){
                if(user.getId().equals(orderReturn.getSalemanId())){
                    dto.setUserName(user.getFullName());
                    break;
                }
            }
        }
        if(customers != null){
            for(CustomerDTO customer : customers){
                if(customer.getId().equals(orderReturn.getCustomerId())){
                    dto.setCustomerNumber(customer.getCustomerCode());
                    dto.setCustomerName(customer.getFullName());
                    break;
                }
            }
        }
        dto.setAmount(orderReturn.getAmount() * (-1));
        dto.setTotal(orderReturn.getTotal() * (-1));
        dto.setDateReturn(orderReturn.getOrderDate());
        return dto;
    }

    @Override
    public OrderReturnDetailDTO getOrderReturnDetail(Long orderReturnId) {
        OrderReturnDetailDTO orderReturnDetailDTO = new OrderReturnDetailDTO();
        orderReturnDetailDTO.setInfos(getInfos(orderReturnId));
        orderReturnDetailDTO.setProductReturn(getProductReturn(orderReturnId));
        orderReturnDetailDTO.setPromotionReturn(getPromotionReturn(orderReturnId));
        return orderReturnDetailDTO;
    }

    private InfosReturnDetailDTO getInfos(long orderReturnId){
        InfosReturnDetailDTO infosReturnDetailDTO = new InfosReturnDetailDTO();
        SaleOrder orderReturn = repository.findById(orderReturnId).get();
        if (orderReturn.getFromSaleOrderId() != null) {
            SaleOrder saleOrder = repository.findById(orderReturn.getFromSaleOrderId()).get();
            infosReturnDetailDTO.setOrderDate(saleOrder.getOrderDate()); //order date
        }
        CustomerDTO customer =
                customerClient.getCustomerByIdV1(orderReturn.getCustomerId()).getData();
        infosReturnDetailDTO.setCustomerName(customer.getFirstName()+" "+customer.getLastName());
        ApParamDTO apParamDTO = new ApParamDTO();
        if(orderReturn.getReasonId()!= null) {
             apParamDTO = apparamClient.getApParamByCodeV1(orderReturn.getReasonId()).getData();
        }
        infosReturnDetailDTO.setReason(apParamDTO.getApParamName());
        infosReturnDetailDTO.setReasonDesc(orderReturn.getReasonDesc());
        infosReturnDetailDTO.setReturnDate(orderReturn.getOrderDate()); //order return
        UserDTO user = userClient.getUserByIdV1(orderReturn.getSalemanId());
        infosReturnDetailDTO.setUserName(user.getFirstName()+" "+user.getLastName());
        return  infosReturnDetailDTO;
    }

    private CoverResponse<List<ProductReturnDTO>,TotalOrderReturnDetail> getProductReturn(long orderReturnId) {
        List<SaleOrderDetail> productReturns = saleOrderDetailRepository.findSaleOrderDetail(orderReturnId, false);
        if(productReturns.size() == 0) return null;
        List<ProductReturnDTO> productReturnDTOList = new ArrayList<>();
        List<Product> products  = productRepository.getProducts(productReturns.stream().map(item -> item.getProductId()).distinct().collect(Collectors.toList()), null);
        TotalOrderReturnDetail totalResponse = new TotalOrderReturnDetail();
        for (SaleOrderDetail productReturn:productReturns ) {
            ProductReturnDTO productReturnDTO = new ProductReturnDTO();
            if(products != null){
                for(Product product : products){
                    if(product.getId().equals(productReturn.getProductId())){
                        productReturnDTO.setProductCode(product.getProductCode());
                        productReturnDTO.setProductName(product.getProductName());
                        productReturnDTO.setUnit(product.getUom1());
                        break;
                    }
                }
            }
            productReturnDTO.setPricePerUnit(productReturn.getPrice());
            if(productReturn.getAutoPromotion() == null && productReturn.getZmPromotion() == null){
                productReturnDTO.setDiscount(0D);
            }
            else if(productReturn.getAutoPromotion() == null || productReturn.getZmPromotion() == null) {
                if(productReturn.getAutoPromotion() == null)
                    productReturnDTO.setDiscount(productReturn.getZmPromotion());
                if(productReturn.getZmPromotion() == null)
                    productReturnDTO.setDiscount(productReturn.getAutoPromotion());
            }else {
                double discount = productReturn.getAutoPromotion() + productReturn.getZmPromotion();
                productReturnDTO.setDiscount(discount);
            }
            if(productReturn.getQuantity() < 0) {
                productReturnDTO.setTotalPrice(productReturn.getAmount() * (-1));
                productReturnDTO.setQuantity(productReturn.getQuantity() * (-1));
                productReturnDTO.setPaymentReturn(productReturn.getTotal() * (-1));
            } else {
                productReturnDTO.setTotalPrice(productReturn.getAmount());
                productReturnDTO.setQuantity(productReturn.getQuantity());
                productReturnDTO.setPaymentReturn(productReturn.getTotal());
            }
            productReturnDTOList.add(productReturnDTO);
            totalResponse.setTotalQuantity(totalResponse.getTotalQuantity() + productReturnDTO.getQuantity());
            totalResponse.setTotalAmount(totalResponse.getTotalAmount() + productReturnDTO.getTotalPrice());
            totalResponse.setTotalDiscount(totalResponse.getTotalDiscount() + productReturnDTO.getDiscount());
            totalResponse.setAllTotal(totalResponse.getAllTotal() + productReturnDTO.getPaymentReturn());
        }

        CoverResponse<List<ProductReturnDTO>,TotalOrderReturnDetail> coverResponse =
                new CoverResponse<>(productReturnDTOList,totalResponse);
        return coverResponse;
    }

    private CoverResponse<List<PromotionReturnDTO>,TotalOrderReturnDetail> getPromotionReturn(long orderReturnId) {
        List<SaleOrderDetail> promotionReturns = saleOrderDetailRepository.findSaleOrderDetail(orderReturnId, true);
        List<PromotionReturnDTO> promotionReturnsDTOList = new ArrayList<>();
        TotalOrderReturnDetail totalResponse = new TotalOrderReturnDetail();
        if(!promotionReturns.isEmpty()) {
            List<Product> products  = productRepository.getProducts(promotionReturns.stream().map(item -> item.getProductId()).distinct().collect(Collectors.toList()), null);
            for (SaleOrderDetail promotionReturn:promotionReturns) {
                PromotionReturnDTO promotionReturnDTO = new PromotionReturnDTO();
                if(products != null){
                    for(Product product : products){
                        if(product.getId().equals(promotionReturn.getProductId())){
                            promotionReturnDTO.setProductCode(product.getProductCode());
                            promotionReturnDTO.setProductName(product.getProductName());
                            promotionReturnDTO.setUnit(product.getUom1());
                            break;
                        }
                    }
                }
                if(promotionReturn.getQuantity() < 0){
                    promotionReturnDTO.setQuantity(promotionReturn.getQuantity() * (-1));
                }else {
                    promotionReturnDTO.setQuantity(promotionReturn.getQuantity());
                }
                promotionReturnDTO.setPricePerUnit(0D);
                promotionReturnDTO.setPaymentReturn(0D);
                promotionReturnsDTOList.add(promotionReturnDTO);
                totalResponse.setTotalQuantity(totalResponse.getTotalQuantity() + promotionReturnDTO.getQuantity());
                totalResponse.setAllTotal(totalResponse.getAllTotal() + promotionReturnDTO.getPaymentReturn());
            }
        }
        CoverResponse<List<PromotionReturnDTO>,TotalOrderReturnDetail> coverResponse =
                new CoverResponse<>(promotionReturnsDTOList,totalResponse);
        return coverResponse;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public SaleOrder createOrderReturn(OrderReturnRequest request, Long shopId, String userName) {
        if (request == null)
            throw new ValidateException(ResponseMessage.REQUEST_BODY_NOT_BE_NULL);
        SaleOrder saleOrder = repository.getSaleOrderByNumber(request.getOrderNumber());
        if(saleOrder == null)
            throw new ValidateException(ResponseMessage.ORDER_RETURN_DOES_NOT_EXISTS);
        CustomerDTO customer = customerClient.getCustomerByIdV1(saleOrder.getCustomerId()).getData();
        if(customer == null) throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
        Integer check = repository.checkIsReturn(saleOrder.getId());
        if(check != null && check > 0)
                throw new ValidateException(ResponseMessage.SALE_ORDER_HAS_ALREADY_RETURNED);
        List<SaleOrderDetail> saleOrderPromotions =
                saleOrderDetailRepository.findSaleOrderDetail(saleOrder.getId(), true);
        for(SaleOrderDetail promotionDetail:saleOrderPromotions) {
            if (promotionClient.isReturn(promotionDetail.getPromotionCode()) == true)
                throw new ValidateException(ResponseMessage.SALE_ORDER_HAVE_PRODUCT_CANNOT_RETURN);
        }
        LocalDateTime orderDate = DateUtils.convertToDate(saleOrder.getOrderDate());
        LocalDateTime returnDate = DateUtils.convertToDate(new Date());
        Duration dur = Duration.between(orderDate, returnDate);
        double diff = dur.toMillis();
//        double diff = returnDate.getTime() - orderDate.getTime();
        double diffDays = diff / (24 * 60 * 60 * 1000);
        int dayReturn = Integer.parseInt(shopClient.dayReturn(shopId).getData());
        SaleOrder newOrderReturn = new SaleOrder();
        if(diffDays <= dayReturn) {
            int day = returnDate.getDayOfMonth();
            int month = returnDate.getMonthValue();
            String  year = Integer.toString(returnDate.getYear()).substring(2);
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            NewOrderReturnDTO newOrderReturnDTO = modelMapper.map(saleOrder, NewOrderReturnDTO.class);
            newOrderReturn =  modelMapper.map(newOrderReturnDTO, SaleOrder.class);
            String orderNumber = createOrderReturnNumber(saleOrder.getShopId(), day, month, year);
            newOrderReturn.setOrderNumber(orderNumber); // important
            newOrderReturn.setType(2);
            newOrderReturn.setTotalCustomerPurchase(saleOrder.getTotalCustomerPurchase());
            newOrderReturn.setCustomerPurchase(saleOrder.getCustomerPurchase());
            newOrderReturn.setTotalPromotion(saleOrder.getTotalPromotion());
            newOrderReturn.setFromSaleOrderId(saleOrder.getId());
            newOrderReturn.setReasonId(request.getReasonId());
            newOrderReturn.setReasonDesc(request.getReasonDescription());
            newOrderReturn.setAmount(saleOrder.getAmount() * (-1));
            newOrderReturn.setTotal(saleOrder.getTotal() * (-1));
            newOrderReturn.setCreatedAt(LocalDateTime.now());
            newOrderReturn.setOrderDate(newOrderReturn.getCreatedAt());
//            newOrderReturn.setOrderDate(request.getDateReturn());
            repository.save(newOrderReturn); //save new orderReturn
            saleOrder.setIsReturn(false);
            repository.save(saleOrder);

            //new orderReturn detail
            List<SaleOrderDetail> saleOrderDetails =
                    saleOrderDetailRepository.findSaleOrderDetail(saleOrder.getId(), false);
//            if(saleOrderDetails.size() <= 0) throw new ValidateException(ResponseMessage.SALE_ORDER_DOES_NOT_HAVE_PRODUCT);
            SaleOrder orderReturn = repository.getOrderReturnByNumber(newOrderReturn.getOrderNumber());
            if(saleOrderDetails != null)
            for(SaleOrderDetail saleOrderDetail:saleOrderDetails) {
                saleOrderDetail.setSaleOrderId(orderReturn.getId());
                saleOrderDetail.setAutoPromotionVat(saleOrderDetail.getAutoPromotionVat());
                saleOrderDetail.setOrderDate(LocalDateTime.now());
                saleOrderDetail.setQuantity(saleOrderDetail.getQuantity() * (-1));
                saleOrderDetail.setAmount((saleOrderDetail.getAmount() * (-1)));
                saleOrderDetail.setTotal((saleOrderDetail.getTotal() * (-1)));
                saleOrderDetailRepository.save(saleOrderDetail); //save new orderReturn detail
            }

            //new orderReturn promotion
            for(SaleOrderDetail promotionDetail:saleOrderPromotions) {
                promotionDetail.setSaleOrderId(orderReturn.getId());
                promotionDetail.setPrice(0D);
                promotionDetail.setAmount(0D);
                promotionDetail.setTotal(0D);
                promotionDetail.setQuantity(promotionDetail.getQuantity() * (-1));
                promotionDetail.setPromotionType(promotionDetail.getPromotionType());
                saleOrderDetailRepository.save(promotionDetail);
            }

            //new orderReturn discount
            List<SaleOrderDiscount> orderReturnDiscount = saleDiscount.findAllBySaleOrderId(saleOrder.getId());
            if(orderReturnDiscount.size() > 0) {
                for(SaleOrderDiscount discount:orderReturnDiscount){
                    discount.setOrderDate(newOrderReturn.getCreatedAt());
                    discount.setSaleOrderId(newOrderReturn.getId());
                    saleDiscount.save(discount);
                }
            }
            updateReturn(newOrderReturn.getId(), newOrderReturn.getWareHouseTypeId(),shopId);
            if(saleOrder.getCustomerPurchase() != null)
                saleService.updateCustomer(newOrderReturn, customer, true);
            if(saleOrder.getMemberCardAmount() != null)
                saleService.updateAccumulatedAmount(-saleOrder.getMemberCardAmount(), customer.getId());
        }else {
            throw new ValidateException(ResponseMessage.ORDER_EXPIRED_FOR_RETURN);
        }
        return newOrderReturn;
    }

    public CoverResponse<List<SaleOrderDTO>,TotalOrderChoose> getSaleOrderForReturn(SaleOrderChosenFilter filter, Long shopId) {
        String orderNumber = StringUtils.defaultIfBlank(filter.getOrderNumber(), StringUtils.EMPTY);
        String upperCaseON = VNCharacterUtils.removeAccent(orderNumber.toUpperCase(Locale.ROOT));
        String productCode = StringUtils.defaultIfBlank(filter.getProduct(), StringUtils.EMPTY);
        String upperCasePC= VNCharacterUtils.removeAccent(productCode.toUpperCase(Locale.ROOT));
        String nameLowerCase = VNCharacterUtils.removeAccent(filter.getProduct()).toUpperCase(Locale.ROOT);
        String checkLowerCaseNull = StringUtils.defaultIfBlank(nameLowerCase, StringUtils.EMPTY);
        long DAY_IN_MS = 1000 * 60 * 60 * 24;
        String stringDayReturn = shopClient.dayReturn(shopId).getData();
        if(stringDayReturn == null) throw new ValidateException(ResponseMessage.SHOP_DOES_HAVE_DAY_RETURN);
        int dayReturn = Integer.parseInt(shopClient.dayReturn(shopId).getData());
        List<Long> customerIds = null;
        customerIds = customerClient.getIdCustomerBySearchKeyWordsV1(filter.getSearchKeyword()).getData();
        if (filter.getFromDate() == null && filter.getToDate() == null) {
            filter.setFromDate(DateUtils.getFirstDayOfCurrentMonth());
            filter.setToDate(DateUtils.convertDateToLocalDateTime(new Date()));
        }
        if(filter.getFromDate() != null && filter.getToDate() == null) {
            filter.setToDate(LocalDateTime.now());
        }
        if(filter.getFromDate() != null && filter.getToDate() != null) {
            LocalDateTime tsToDate = DateUtils.convertToDate(filter.getToDate());
            LocalDateTime tsFromDate = DateUtils.convertFromDate(filter.getFromDate());
//            double diff = tsToDate.getTime() - tsFromDate.getTime();
            Duration dur = Duration.between(tsFromDate, tsToDate);
            double diff = dur.toMillis();
            double diffDays = diff / DAY_IN_MS;
            long ago = tsFromDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            if (diffDays > dayReturn) {
                do {
                    ago = ago + (1 * DAY_IN_MS);
                    diff = tsToDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - ago;
                    diffDays = diff / DAY_IN_MS;
                } while (diffDays > dayReturn);
                LocalDateTime convert = LocalDateTime.ofInstant(Instant.ofEpochMilli(ago),
                        TimeZone.getDefault().toZoneId());
                filter.setFromDate(convert);
                filter.setToDate(tsToDate);
            }
        }
        if(filter.getFromDate() == null && filter.getToDate() != null) {
//            Date ago = new Date(filter.getToDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - (2 * DAY_IN_MS));
            filter.setFromDate(filter.getToDate().minus((dayReturn * DAY_IN_MS), ChronoField.MILLI_OF_DAY.getBaseUnit()));
        }
        String keyUpper = "";
        if (filter.getProduct() != null){
            keyUpper = VNCharacterUtils.removeAccent(filter.getProduct().trim()).toUpperCase(Locale.ROOT);
        }
        if(customerIds.size() == 0) {
            customerIds = null;
        }
        List<SaleOrder> saleOrders = repository.getSaleOrderForReturn(shopId,upperCaseON, customerIds, keyUpper, filter.getFromDate(), filter.getToDate(), PageRequest.of(0, 5000)).getContent();
        if(saleOrders.size() == 0) throw new ValidateException(ResponseMessage.ORDER_FOR_RETURN_NOT_FOUND);

        List<SaleOrderDTO> list = new ArrayList<>();
        SaleOrderTotalResponse totalResponse = new SaleOrderTotalResponse();
        List<UserDTO> users = userClient.getUserByIdsV1(saleOrders.stream().map(item -> item.getSalemanId()).distinct().filter(Objects::nonNull).collect(Collectors.toList()));
        List<CustomerDTO> customers = customerClient.getCustomerInfoV1(1, saleOrders.stream().map(item -> item.getCustomerId()).distinct().filter(Objects::nonNull).collect(Collectors.toList()));
        for(SaleOrder saleOrder:saleOrders) {
            SaleOrderDTO listForChoose = mapSaleOrderDTO(saleOrder, users, customers);
            list.add(listForChoose);
            totalResponse.setTotalAmount(totalResponse.getTotalAmount() + saleOrder.getAmount());
            totalResponse.setAllTotal(totalResponse.getAllTotal() + saleOrder.getTotal());
        }
        CoverResponse coverResponse = new CoverResponse(list, totalResponse);
        return coverResponse;

    }

    private SaleOrderDTO mapSaleOrderDTO(SaleOrder saleOrder, List<UserDTO> users, List<CustomerDTO> customers) {
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

    public OrderReturnDetailDTO getSaleOrderChosen(long id) {
        OrderReturnDetailDTO orderReturnDetailDTO = new OrderReturnDetailDTO();
        orderReturnDetailDTO.setInfos(getInfos(id));
        orderReturnDetailDTO.setProductReturn(getProductReturn(id));
        orderReturnDetailDTO.setPromotionReturn(getPromotionReturn(id));
        List<ApParamDTO> apParamDTOList = apparamClient.getApParamByTypeV1("SALEMT_MASTER_PAY_ITEM").getData();
        List<ReasonReturnDTO> reasons = new ArrayList<>();
        for(ApParamDTO ap:apParamDTOList) {
            ReasonReturnDTO reasonReturnDTO = new ReasonReturnDTO();
            reasonReturnDTO.setApCode(ap.getApParamCode());
            reasonReturnDTO.setApName(ap.getApParamName());
            reasonReturnDTO.setValue(ap.getValue());
            reasons.add(reasonReturnDTO);
        }
        orderReturnDetailDTO.setReasonReturn(reasons);
        return orderReturnDetailDTO;
    }

    private void updateReturn(long id, long wareHouse, long shopId){
        // todo lock table StockTotal
        List<SaleOrderDetail> odReturns = saleOrderDetailRepository.findSaleOrderDetail(id, false);

        for(SaleOrderDetail sod:odReturns) {
            StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeIdAndShopId(sod.getProductId(), wareHouse,shopId);
            stockIn(stockTotal, sod.getQuantity());
        }
        List<SaleOrderDetail> promotionReturns = saleOrderDetailRepository.findSaleOrderDetail(id, true);
        for(SaleOrderDetail prd:promotionReturns) {
            StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeIdAndShopId(prd.getProductId(), wareHouse,shopId);
            stockIn(stockTotal, prd.getQuantity());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void stockIn(StockTotal stockTotal, int quantity) {
        stockTotal.setQuantity(stockTotal.getQuantity() + (quantity * (-1)));
        stockTotalRepository.save(stockTotal);
    }

    private String createOrderReturnNumber(Long shopId, int day, int month, String year) {
        ShopDTO shop = shopClient.getByIdV1(shopId).getData();
        String shopCode = shop.getShopCode();
        LocalDateTime now = DateUtils.convertDateToLocalDateTime(new Date());
        LocalDateTime start =  DateUtils.convertFromDate(now);
        LocalDateTime end =  DateUtils.convertToDate(now);
        int STT = repository.countSaleOrder(start,end,shopId) + 1;
        return  "SAL." +  shopCode + "." + year + Integer.toString(month + 100).substring(1)  + Integer.toString(day + 100).substring(1) + Integer.toString(STT + 10000).substring(1);
    }
}
