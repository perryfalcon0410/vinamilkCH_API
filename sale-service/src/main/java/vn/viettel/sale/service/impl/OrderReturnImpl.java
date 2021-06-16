package vn.viettel.sale.service.impl;

import org.apache.commons.lang.StringUtils;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.core.dto.common.ApParamDTO;
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
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.service.feign.*;
import vn.viettel.sale.specification.SaleOderSpecification;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.*;

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

    @Override
    public CoverResponse<Page<OrderReturnDTO>, SaleOrderTotalResponse> getAllOrderReturn(SaleOrderFilter saleOrderFilter, Pageable pageable, Long id) {
        Page<SaleOrder> findAll;
        if(saleOrderFilter.getSearchKeyword() == null){
            findAll = repository.findAll(SaleOderSpecification.hasFromDateToDate(saleOrderFilter.getFromDate(), saleOrderFilter.getToDate())
                    .and(SaleOderSpecification.hasOrderNumber(saleOrderFilter.getOrderNumber()))
                    .and(SaleOderSpecification.type(2)), pageable);
        }else {
            List<Long> customerIds = customerClient.getIdCustomerByV1(saleOrderFilter.getSearchKeyword(), saleOrderFilter.getCustomerPhone()).getData();
            if(customerIds.size() == 0) {
                return new CoverResponse<>(new PageImpl<>(new ArrayList<>()), new SaleOrderTotalResponse());
            }else {
                findAll = repository.findAll(Specification.where(SaleOderSpecification.hasNameOrPhone(customerIds))
                        .and(SaleOderSpecification.hasFromDateToDate(saleOrderFilter.getFromDate(), saleOrderFilter.getToDate()))
                        .and(SaleOderSpecification.hasOrderNumber(saleOrderFilter.getOrderNumber()))
                        .and(SaleOderSpecification.hasShopId(id))
                        .and(SaleOderSpecification.type(2)), pageable);
            }
        }
        Page<OrderReturnDTO> orderReturnDTOS = findAll.map(this::mapOrderReturnDTO);
        SaleOrderTotalResponse totalResponse = new SaleOrderTotalResponse();
        findAll.forEach(so -> {
            totalResponse.addTotalAmount(so.getAmount() * -1).addAllTotal(so.getTotal() * -1);
        });
        CoverResponse coverResponse = new CoverResponse(orderReturnDTOS, totalResponse);
        return coverResponse;
    }

    private OrderReturnDTO mapOrderReturnDTO(SaleOrder orderReturn) {
        SaleOrder saleOrder = new SaleOrder();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OrderReturnDTO dto = modelMapper.map(orderReturn, OrderReturnDTO.class);
        if (repository.findById(orderReturn.getFromSaleOrderId()).isPresent())
            saleOrder = repository.findById(orderReturn.getFromSaleOrderId()).get();
        UserDTO user = userClient.getUserByIdV1(orderReturn.getSalemanId());
        CustomerDTO customer = customerClient.getCustomerByIdV1(orderReturn.getCustomerId()).getData();
        if(customer == null) throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
        dto.setOrderNumberRef(saleOrder.getOrderNumber());
        dto.setUserName(user.getFirstName()+" "+user.getLastName());
        dto.setCustomerNumber(customer.getCustomerCode());
        dto.setCustomerName(customer.getLastName() +" "+customer.getFirstName());
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

    public InfosReturnDetailDTO getInfos(long orderReturnId){
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

    public CoverResponse<List<ProductReturnDTO>,TotalOrderReturnDetail> getProductReturn(long orderReturnId) {
        List<SaleOrderDetail> productReturns = saleOrderDetailRepository.getBySaleOrderId(orderReturnId);
        List<ProductReturnDTO> productReturnDTOList = new ArrayList<>();
        for (SaleOrderDetail productReturn:productReturns ) {
            Product product = productRepository.getById(productReturn.getProductId());
            ProductReturnDTO productReturnDTO = new ProductReturnDTO();
            productReturnDTO.setProductCode(product.getProductCode());
            productReturnDTO.setProductName(product.getProductName());
            productReturnDTO.setUnit(product.getUom1());
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
        }
        TotalOrderReturnDetail totalResponse = new TotalOrderReturnDetail();
        productReturnDTOList.forEach(pr -> {
            totalResponse   .addTotalQuantity(pr.getQuantity())
                            .addTotalAmount(pr.getTotalPrice())
                            .addTotalDiscount(pr.getDiscount())
                            .addAllTotal(pr.getPaymentReturn());
        });
        CoverResponse<List<ProductReturnDTO>,TotalOrderReturnDetail> coverResponse =
                new CoverResponse<>(productReturnDTOList,totalResponse);
        return coverResponse;
    }

    public CoverResponse<List<PromotionReturnDTO>,TotalOrderReturnDetail> getPromotionReturn(long orderReturnId) {
        List<SaleOrderDetail> promotionReturns = saleOrderDetailRepository.getSaleOrderDetailPromotion(orderReturnId);
        List<PromotionReturnDTO> promotionReturnsDTOList = new ArrayList<>();
        for (SaleOrderDetail promotionReturn:promotionReturns) {
            Product product = productRepository.findById(promotionReturn.getProductId()).get();
            PromotionReturnDTO promotionReturnDTO = new PromotionReturnDTO();
            promotionReturnDTO.setProductCode(product.getProductCode());
            promotionReturnDTO.setProductName(product.getProductName());
            promotionReturnDTO.setUnit(product.getUom1());
            if(promotionReturn.getQuantity() < 0){
                promotionReturnDTO.setQuantity(promotionReturn.getQuantity() * (-1));
            }else {
                promotionReturnDTO.setQuantity(promotionReturn.getQuantity());
            }
            promotionReturnDTO.setPricePerUnit(0D);
            promotionReturnDTO.setPaymentReturn(0D);
            promotionReturnsDTOList.add(promotionReturnDTO);
        }
        TotalOrderReturnDetail totalResponse = new TotalOrderReturnDetail();
        promotionReturnsDTOList.forEach(pr -> {
            totalResponse   .addTotalQuantity(pr.getQuantity())
                            .addTotalAmount(0D)
                            .addTotalDiscount(0D)
                            .addAllTotal(pr.getPaymentReturn());
        });
        CoverResponse<List<PromotionReturnDTO>,TotalOrderReturnDetail> coverResponse =
                new CoverResponse<>(promotionReturnsDTOList,totalResponse);
        return coverResponse;
    }

//    @Transactional(rollbackFor = Exception.class)
    @Override
    public SaleOrder createOrderReturn(OrderReturnRequest request, Long id, String userName) {
        if (request == null)
            throw new ValidateException(ResponseMessage.REQUEST_BODY_NOT_BE_NULL);
        SaleOrder saleOrder = repository.getSaleOrderByNumber(request.getOrderNumber());
        if(saleOrder == null)
            throw new ValidateException(ResponseMessage.ORDER_RETURN_DOES_NOT_EXISTS);
        CustomerDTO customer = customerClient.getCustomerByIdV1(saleOrder.getCustomerId()).getData();
        if(customer == null) throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
        List<Long> sorId = repository.getFromSaleId();
        SaleOrder check = repository.checkIsReturn(saleOrder.getId(), sorId);
        if(check != null)
                throw new ValidateException(ResponseMessage.SALE_ORDER_HAS_ALREADY_RETURNED);
        List<SaleOrderDetail> saleOrderPromotions =
                saleOrderDetailRepository.getSaleOrderDetailPromotion(saleOrder.getId());
        for(SaleOrderDetail promotionDetail:saleOrderPromotions) {
            if (promotionClient.isReturn(promotionDetail.getPromotionCode()) == true)
                throw new ValidateException(ResponseMessage.SALE_ORDER_HAVE_PRODUCT_CANNOT_RETURN);
        }
        LocalDateTime orderDate = DateUtils.convertToDate(saleOrder.getOrderDate());
        LocalDateTime returnDate = DateUtils.convertToDate(request.getDateReturn());
        Duration dur = Duration.between(orderDate, returnDate);
        double diff = dur.toMillis();
//        double diff = returnDate.getTime() - orderDate.getTime();
        double diffDays = diff / (24 * 60 * 60 * 1000);
        int dayReturn = Integer.parseInt(shopClient.dayReturn(id).getData());
        SaleOrder newOrderReturn = new SaleOrder();
        if(diffDays <= dayReturn) {
            int day = request.getDateReturn().getDayOfMonth();
            int month = request.getDateReturn().getMonthValue();
            String  year = Integer.toString(request.getDateReturn().getYear()).substring(2);
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            NewOrderReturnDTO newOrderReturnDTO = modelMapper.map(saleOrder, NewOrderReturnDTO.class);
            newOrderReturn =  modelMapper.map(newOrderReturnDTO, SaleOrder.class);
            String orderNumber = createOrderReturnNumber(saleOrder.getShopId(), day, month, year);
            newOrderReturn.setOrderNumber(orderNumber); // important
            newOrderReturn.setType(2);
            newOrderReturn.setFromSaleOrderId(saleOrder.getId());
            newOrderReturn.setReasonId(request.getReasonId());
            newOrderReturn.setReasonDesc(request.getReasonDescription());
            newOrderReturn.setAmount(saleOrder.getAmount() * (-1));
            newOrderReturn.setTotal(saleOrder.getTotal() * (-1));
            newOrderReturn.setOrderDate(request.getDateReturn());
            repository.save(newOrderReturn); //save new orderReturn

            //new orderReturn detail
            List<SaleOrderDetail> saleOrderDetails =
                    saleOrderDetailRepository.getBySaleOrderId(saleOrder.getId());
            if(saleOrderDetails.size() <= 0) throw new ValidateException(ResponseMessage.SALE_ORDER_DOES_NOT_HAVE_PRODUCT);
            for(SaleOrderDetail saleOrderDetail:saleOrderDetails) {
                SaleOrder orderReturn = repository.getSaleOrderByNumber(newOrderReturn.getOrderNumber());
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                NewOrderReturnDetailDTO newOrderReturnDetailDTO =
                        modelMapper.map(saleOrderDetail, NewOrderReturnDetailDTO.class);
                SaleOrderDetail orderDetailReturn =
                        modelMapper.map(newOrderReturnDetailDTO, SaleOrderDetail.class);
                orderDetailReturn.setSaleOrderId(orderReturn.getId());
                orderDetailReturn.setQuantity(saleOrderDetail.getQuantity() * (-1));
                orderDetailReturn.setAmount((saleOrderDetail.getAmount() * (-1)));
                orderDetailReturn.setTotal((saleOrderDetail.getTotal() * (-1)));
                saleOrderDetailRepository.save(orderDetailReturn); //save new orderReturn detail
            }

            //new orderReturn promotion
            for(SaleOrderDetail promotionDetail:saleOrderPromotions) {
                SaleOrder orderReturn = repository.getSaleOrderByNumber(newOrderReturn.getOrderNumber());
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                NewOrderReturnDetailDTO newOrderReturnDetailDTO =
                        modelMapper.map(promotionDetail, NewOrderReturnDetailDTO.class);
                SaleOrderDetail promotionReturn =
                        modelMapper.map(newOrderReturnDetailDTO, SaleOrderDetail.class);
                promotionReturn.setSaleOrderId(orderReturn.getId());
                promotionReturn.setPrice(0D);
                promotionReturn.setAmount(0D);
                promotionReturn.setTotal(0D);
                promotionReturn.setQuantity(promotionDetail.getQuantity() * (-1));
                saleOrderDetailRepository.save(promotionReturn);
            }

            updateReturn(newOrderReturn.getId(), newOrderReturn.getWareHouseTypeId());
        }else {
            throw new ValidateException(ResponseMessage.ORDER_EXPIRED_FOR_RETURN);
        }
        return newOrderReturn;
    }

    public CoverResponse<List<SaleOrderDTO>,TotalOrderChoose> getSaleOrderForReturn(SaleOrderChosenFilter filter, Long id) {
        String orderNumber = StringUtils.defaultIfBlank(filter.getOrderNumber(), StringUtils.EMPTY);
        String upperCaseON = VNCharacterUtils.removeAccent(orderNumber.toUpperCase(Locale.ROOT));
        String productCode = StringUtils.defaultIfBlank(filter.getProduct(), StringUtils.EMPTY);
        String upperCasePC= VNCharacterUtils.removeAccent(productCode.toUpperCase(Locale.ROOT));
        String nameLowerCase = VNCharacterUtils.removeAccent(filter.getProduct()).toUpperCase(Locale.ROOT);
        String checkLowerCaseNull = StringUtils.defaultIfBlank(nameLowerCase, StringUtils.EMPTY);
        long DAY_IN_MS = 1000 * 60 * 60 * 24;
        List<SaleOrder> saleOrders; List<Long> customerIds = null;
        customerIds = customerClient.getIdCustomerBySearchKeyWordsV1(filter.getSearchKeyword()).getData();
        if (filter.getFromDate() == null && filter.getToDate() == null) {
            filter.setFromDate(DateUtils.getFirstDayOfCurrentMonth());
            filter.setToDate(DateUtils.getLastDayOfCurrentMonth());
        }else if(filter.getFromDate() != null && filter.getToDate() != null){
            LocalDateTime tsToDate = DateUtils.convertToDate(filter.getToDate());
            LocalDateTime tsFromDate = DateUtils.convertFromDate(filter.getFromDate());
//            double diff = tsToDate.getTime() - tsFromDate.getTime();
            Duration dur = Duration.between(tsFromDate, tsToDate);
            double diff = dur.toMillis();
            double diffDays = diff / (24 * 60 * 60 * 1000);
            int dayReturn = Integer.parseInt(shopClient.dayReturn(id).getData());
            long ago = tsFromDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            if(diffDays <= dayReturn) {
                filter.setFromDate(tsFromDate);
            }else {
                do{
                    ago = ago + (1 * DAY_IN_MS);
                    diff = tsToDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - ago;
                    diffDays = diff / (24 * 60 * 60 * 1000);
                }while (diffDays > dayReturn);
                filter.setFromDate(tsFromDate);
            }
            filter.setToDate(tsToDate);
        }else if(filter.getFromDate() != null && filter.getToDate() == null) {
            filter.setToDate(LocalDateTime.now());
        }else if(filter.getFromDate() == null && filter.getToDate() != null) {
//            Date ago = new Date(filter.getToDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - (2 * DAY_IN_MS));
            filter.setFromDate(filter.getToDate().plus((2 * DAY_IN_MS), ChronoField.MILLI_OF_DAY.getBaseUnit()));
        }
        if(filter.getSearchKeyword() == null || filter.getSearchKeyword().equals("")) {
            List<Long> idr = repository.getFromSaleId();
            saleOrders =
                    repository.getListSaleOrder(upperCasePC, checkLowerCaseNull, upperCaseON.trim(), customerIds, filter.getFromDate(), filter.getToDate(), idr, id);
            if(saleOrders.size() == 0) throw new ValidateException(ResponseMessage.ORDER_FOR_RETURN_NOT_FOUND);
        }else {
            if(customerIds.size() == 0) {
                throw new ValidateException(ResponseMessage.ORDER_FOR_RETURN_NOT_FOUND);
            }else {
                List<Long> idr = repository.getFromSaleId();
                saleOrders =
                        repository.getListSaleOrder(upperCasePC, checkLowerCaseNull, upperCaseON.trim(), customerIds, filter.getFromDate(), filter.getToDate(), idr, id);
                if(saleOrders.size() == 0) throw new ValidateException(ResponseMessage.ORDER_FOR_RETURN_NOT_FOUND);
            }
        }
        List<SaleOrderDTO> list = new ArrayList<>();
        for(SaleOrder saleOrder:saleOrders) {
            SaleOrderDTO listForChoose = mapSaleOrderDTO(saleOrder);
            list.add(listForChoose);
        }
        SaleOrderTotalResponse totalResponse = new SaleOrderTotalResponse();
        saleOrders.forEach(so -> {
            totalResponse.addTotalAmount(so.getAmount()).addAllTotal(so.getTotal());
        });
        CoverResponse coverResponse = new CoverResponse(list, totalResponse);
        return coverResponse;

    }

    private SaleOrderDTO mapSaleOrderDTO(SaleOrder saleOrder) {
        String customerName, saleManName;
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        SaleOrderDTO dto = modelMapper.map(saleOrder, SaleOrderDTO.class);
        UserDTO user = userClient.getUserByIdV1(saleOrder.getSalemanId());
        CustomerDTO customer = customerClient.getCustomerByIdV1(saleOrder.getCustomerId()).getData();
        if(customer == null) throw new ValidateException(ResponseMessage.ORDER_FOR_RETURN_NOT_FOUND);
        customerName = customer.getLastName() +" "+ customer.getFirstName();
        saleManName = user.getLastName() + " " + user.getFirstName();
        dto.setCustomerName(customerName);
        dto.setSalesManName(saleManName);
        return dto;
    }

    public OrderReturnDetailDTO getSaleOrderChosen(long id) {
        OrderReturnDetailDTO orderReturnDetailDTO = new OrderReturnDetailDTO();
        orderReturnDetailDTO.setInfos(getInfos(id));
        orderReturnDetailDTO.setProductReturn(getProductReturn(id));
        orderReturnDetailDTO.setPromotionReturn(getPromotionReturn(id));
        List<ApParamDTO> apParamDTOList = apparamClient.getApParamByTypeV1("RETURN").getData();
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

    public void updateReturn(long id, long wareHouse){
        List<SaleOrderDetail> odReturns = saleOrderDetailRepository.getBySaleOrderId(id);
        for(SaleOrderDetail sod:odReturns) {
            StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(sod.getProductId(), wareHouse);
            stockIn(stockTotal, sod.getQuantity());
        }
        List<SaleOrderDetail> promotionReturns = saleOrderDetailRepository.getSaleOrderDetailPromotion(id);
        for(SaleOrderDetail prd:promotionReturns) {
            StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(prd.getProductId(), wareHouse);
            stockIn(stockTotal, prd.getQuantity());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void stockIn(StockTotal stockTotal, int quantity) {
        stockTotal.setQuantity(stockTotal.getQuantity() + (quantity * (-1)));
        stockTotalRepository.save(stockTotal);
    }

    public String createOrderReturnNumber(Long shopId, int day, int month, String year) {
        ShopDTO shop = shopClient.getByIdV1(shopId).getData();
        String shopCode = shop.getShopCode();
        int STT = repository.countSaleOrder() + 1;
        return  "SAL." +  shopCode + "." + year + Integer.toString(month + 100).substring(1)  + Integer.toString(day + 100).substring(1) + Integer.toString(STT + 10000).substring(1);
    }
    private Calendar dateToCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;

    }
}
