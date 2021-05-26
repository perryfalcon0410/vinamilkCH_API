package vn.viettel.sale.service.impl;

import org.apache.commons.lang.StringUtils;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.UserDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.util.VNCharacterUtils;
import vn.viettel.sale.entities.*;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.messaging.*;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.OrderReturnService;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.service.feign.ApparamClient;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.service.feign.ShopClient;
import vn.viettel.sale.service.feign.UserClient;
import vn.viettel.sale.specification.SaleOderSpecification;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
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

    @Override
    public CoverResponse<Page<OrderReturnDTO>, SaleOrderTotalResponse> getAllOrderReturn(SaleOrderFilter saleOrderFilter, Pageable pageable, Long id) {
        Page<SaleOrder> findAll;
        if(saleOrderFilter.getSearchKeyword() == null){
            findAll = repository.findAll(SaleOderSpecification.hasFromDateToDate(saleOrderFilter.getFromDate(), saleOrderFilter.getToDate())
                    .and(SaleOderSpecification.hasOrderNumber(saleOrderFilter.getOrderNumber()))
                    .and(SaleOderSpecification.type(2)), pageable);
        }else {
            List<Long> customerIds = customerClient.getIdCustomerBySearchKeyWordsV1(saleOrderFilter.getSearchKeyword()).getData();
            if(customerIds.size() == 0) {
                throw new ValidateException(ResponseMessage.ORDER_FOR_RETURN_NOT_FOUND);
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
        dto.setOrderNumberRef(saleOrder.getOrderNumber());
        dto.setUserName(user.getFirstName()+" "+user.getLastName());
        dto.setCustomerNumber(customer.getCustomerCode());
        dto.setCustomerName(customer.getFirstName()+" "+customer.getLastName());
        dto.setAmount(orderReturn.getAmount() * (-1));
        dto.setTotal(orderReturn.getTotal() * (-1));
        dto.setDateReturn(orderReturn.getOrderDate());
        return dto;
    }

    @Override
    public OrderReturnDetailDTO getOrderReturnDetail(long orderReturnId) {
        OrderReturnDetailDTO orderReturnDetailDTO = new OrderReturnDetailDTO();
        orderReturnDetailDTO.setInfos(getInfos(orderReturnId));
        orderReturnDetailDTO.setProductReturn(getProductReturn(orderReturnId));
        orderReturnDetailDTO.setPromotionReturn(getPromotionReturn(orderReturnId));
        return orderReturnDetailDTO;
    }

    public InfosReturnDetailDTO getInfos(long orderReturnId){
        InfosReturnDetailDTO infosReturnDetailDTO = new InfosReturnDetailDTO();
        SaleOrder orderReturn = repository.findById(orderReturnId).get();
        infosReturnDetailDTO.setOrderDate(orderReturn.getOrderDate()); //order date
        CustomerDTO customer =
                customerClient.getCustomerByIdV1(orderReturn.getCustomerId()).getData();
        infosReturnDetailDTO.setCustomerName(customer.getFirstName()+" "+customer.getLastName());
        ApParamDTO apParamDTO = new ApParamDTO();
        if(orderReturn.getReasonId()!= null) {
             apParamDTO = apparamClient.getApParamByCodeV1(orderReturn.getReasonId()).getData();
        }
        infosReturnDetailDTO.setReason(apParamDTO.getApParamName());
        infosReturnDetailDTO.setReasonDesc(orderReturn.getReasonDesc());
        infosReturnDetailDTO.setReturnDate(orderReturn.getCreatedAt()); //order return
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
            productReturnDTO.setQuantity(productReturn.getQuantity() * (-1));
            productReturnDTO.setPricePerUnit(productReturn.getPrice());
            productReturnDTO.setTotalPrice(productReturn.getAmount() * (-1));
            if(productReturn.getAutoPromotion() == null && productReturn.getZmPromotion() == null){
                productReturnDTO.setDiscount(0F);
            }
            else if(productReturn.getAutoPromotion() == null || productReturn.getZmPromotion() == null) {
                if(productReturn.getAutoPromotion() == null)
                    productReturnDTO.setDiscount(productReturn.getZmPromotion());
                if(productReturn.getZmPromotion() == null)
                    productReturnDTO.setDiscount(productReturn.getAutoPromotion());
            }else {
                float discount = productReturn.getAutoPromotion() + productReturn.getZmPromotion();
                productReturnDTO.setDiscount(discount);
            }
            productReturnDTO.setPaymentReturn(productReturn.getTotal() * (-1));
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
            promotionReturnDTO.setQuantity(promotionReturn.getQuantity() * (-1));
            promotionReturnDTO.setPricePerUnit(0F);
            promotionReturnDTO.setPaymentReturn(0F);
            promotionReturnsDTOList.add(promotionReturnDTO);
        }
        TotalOrderReturnDetail totalResponse = new TotalOrderReturnDetail();
        promotionReturnsDTOList.forEach(pr -> {
            totalResponse   .addTotalQuantity(pr.getQuantity())
                            .addTotalAmount(0F)
                            .addTotalDiscount(0F)
                            .addAllTotal(pr.getPaymentReturn());
        });
        CoverResponse<List<PromotionReturnDTO>,TotalOrderReturnDetail> coverResponse =
                new CoverResponse<>(promotionReturnsDTOList,totalResponse);
        return coverResponse;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public SaleOrder createOrderReturn(OrderReturnRequest request, Long id, String userName) {
        if (request == null)
            throw new ValidateException(ResponseMessage.REQUEST_BODY_NOT_BE_NULL);
        SaleOrder saleOrder = repository.getSaleOrderByNumber(request.getOrderNumber());
        if(saleOrder == null)
            throw new ValidateException(ResponseMessage.ORDER_RETURN_DOES_NOT_EXISTS);
        Date orderDate = EndDay(saleOrder.getOrderDate());
        Date returnDate = EndDay(request.getDateReturn());
        double diff = returnDate.getTime() - orderDate.getTime();
        double diffDays = diff / (24 * 60 * 60 * 1000);
        int dayReturn = Integer.parseInt(shopClient.dayReturn(id).getData());
        SaleOrder newOrderReturn = new SaleOrder();
        if(diffDays <= dayReturn) {
            Calendar cal = dateToCalendar(request.getDateReturn());
            long day = cal.get(Calendar.DATE);
            long month = cal.get(Calendar.MONTH) + 1;
            String  year = Integer.toString(cal.get(Calendar.YEAR)).substring(2);
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
            Timestamp dateReturn = new Timestamp(request.getDateReturn().getTime());
            newOrderReturn.setOrderDate(request.getDateReturn());
            newOrderReturn.setCreatedAt(dateReturn);
            repository.save(newOrderReturn); //save new orderReturn

            //new orderReturn detail
            List<SaleOrderDetail> saleOrderDetails =
                    saleOrderDetailRepository.getBySaleOrderId(saleOrder.getId());
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
            List<SaleOrderDetail> saleOrderPromotions =
                    saleOrderDetailRepository.getSaleOrderDetailPromotion(saleOrder.getId());
            for(SaleOrderDetail promotionDetail:saleOrderPromotions) {
                SaleOrder orderReturn = repository.getSaleOrderByNumber(newOrderReturn.getOrderNumber());
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                NewOrderReturnDetailDTO newOrderReturnDetailDTO =
                        modelMapper.map(promotionDetail, NewOrderReturnDetailDTO.class);
                SaleOrderDetail promotionReturn =
                        modelMapper.map(newOrderReturnDetailDTO, SaleOrderDetail.class);
                promotionReturn.setSaleOrderId(orderReturn.getId());
                promotionReturn.setPrice(0F);
                promotionReturn.setAmount(0F);
                promotionReturn.setTotal(0F);
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
        long DAY_IN_MS = 1000 * 60 * 60 * 24;
        if (filter.getFromDate() == null || filter.getToDate() == null) {
            Date now = EndDay(new Date());
            Date ago = new Date(now.getTime() - (2 * DAY_IN_MS));
            filter.setFromDate(ago);
            filter.setToDate(now);
        }
        String orderNumber = StringUtils.defaultIfBlank(filter.getOrderNumber(), StringUtils.EMPTY);
        String keyProduct = StringUtils.defaultIfBlank(filter.getProduct(), StringUtils.EMPTY);
        String nameLowerCase = VNCharacterUtils.removeAccent(filter.getProduct()).toUpperCase(Locale.ROOT);
        String checkLowerCaseNull = StringUtils.defaultIfBlank(nameLowerCase, StringUtils.EMPTY);
        Date tsToDate = EndDay(filter.getToDate());
        Date tsFromDate = EndDay(filter.getFromDate());
        double diff = tsToDate.getTime() - tsFromDate.getTime();
        double diffDays = diff / (24 * 60 * 60 * 1000);
        int dayReturn = Integer.parseInt(shopClient.dayReturn(id).getData());
        List<Long> customerIds = customerClient.getIdCustomerBySearchKeyWordsV1(filter.getSearchKeyword()).getData();
        List<SaleOrder> saleOrders;
        Date thisFromDate, thisToDate;
        long ago = tsFromDate.getTime();
        if(diffDays <= dayReturn) {
             thisFromDate = tsFromDate;
        }else {
            do{
                ago = ago + (1 * DAY_IN_MS);
                diff = tsToDate.getTime() - ago;
                diffDays = diff / (24 * 60 * 60 * 1000);
            }while (diffDays > dayReturn);
            thisFromDate = new Date(ago);
        }
        thisToDate = tsToDate;
        if(filter.getSearchKeyword() == null || filter.getSearchKeyword().equals("")) {
            List<Long> idr = repository.getFromSaleId();
            saleOrders =
                    repository.getListSaleOrder(keyProduct, checkLowerCaseNull, orderNumber, customerIds, thisFromDate, thisToDate, idr, id);
            if(saleOrders.size() == 0) throw new ValidateException(ResponseMessage.ORDER_FOR_RETURN_NOT_FOUND);
        }else {
            if(customerIds.size() == 0) {
                throw new ValidateException(ResponseMessage.ORDER_FOR_RETURN_NOT_FOUND);
            }else {
                List<Long> idr = repository.getFromSaleId();
                saleOrders =
                        repository.getListSaleOrder(keyProduct, checkLowerCaseNull, orderNumber, customerIds, tsFromDate, tsToDate, idr, id);
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

    public String createOrderReturnNumber(Long shopId, Long day, Long month, String year) {
        ShopDTO shop = shopClient.getByIdV1(shopId).getData();
        String shopCode = shop.getShopCode();
        Date now = new Date();
        int STT = repository.countOrderReturn() + 1;
        return  "SAL." +  shopCode + "." + year + month + day + Integer.toString(STT + 10000).substring(1);
    }
    private Calendar dateToCalendar(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;

    }

    public Timestamp EndDay(Timestamp date) {
        LocalDateTime localDateTimeMax = LocalDateTime.of(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalTime.MAX);
        Timestamp endDay = Timestamp.valueOf(localDateTimeMax);
        return endDay;
    }

    public Date EndDay(Date date) {
        LocalDateTime localDateTimeMax = LocalDateTime.of(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalTime.MAX);
        Date endDay = Date.from(localDateTimeMax.atZone(ZoneId.systemDefault()).toInstant());
        return endDay;
    }
}
