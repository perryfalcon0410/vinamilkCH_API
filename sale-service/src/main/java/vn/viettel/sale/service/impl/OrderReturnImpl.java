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
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.UserDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.util.VNCharacterUtils;
import vn.viettel.sale.entities.Product;
import vn.viettel.sale.entities.SaleOrder;
import vn.viettel.sale.entities.SaleOrderDetail;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.messaging.OrderReturnRequest;
import vn.viettel.sale.messaging.OrderReturnTotalResponse;
import vn.viettel.sale.messaging.SaleOrderChosenFilter;
import vn.viettel.sale.messaging.SaleOrderFilter;
import vn.viettel.sale.repository.ProductRepository;
import vn.viettel.sale.repository.SaleOrderDetailRepository;
import vn.viettel.sale.repository.SaleOrderRepository;
import vn.viettel.sale.service.OrderReturnService;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.service.feign.ApparamClient;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.service.feign.ShopClient;
import vn.viettel.sale.service.feign.UserClient;
import vn.viettel.sale.specification.SaleOderSpecification;

import java.sql.Timestamp;
import java.time.LocalDate;
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

    @Override
    public Response<CoverResponse<Page<OrderReturnDTO>, OrderReturnTotalResponse>> getAllOrderReturn(SaleOrderFilter saleOrderFilter, Pageable pageable) {
        float totalAmount = 0F, totalPayment = 0F;;
        List<OrderReturnDTO> orderReturnDTOList = new ArrayList<>();
        List<SaleOrder> findAll = new ArrayList<>();
        if(saleOrderFilter.getSearchKeyword().equals(null)){
            findAll = repository.findAll(SaleOderSpecification.hasFromDateToDate(saleOrderFilter.getFromDate(), saleOrderFilter.getToDate())
                    .and(SaleOderSpecification.hasOrderNumber(saleOrderFilter.getOrderNumber()))
                    .and(SaleOderSpecification.type(2)));
        }else {
            List<Long> customerIds = customerClient.getIdCustomerBySearchKeyWordsV1(saleOrderFilter.getSearchKeyword()).getData();
            if(customerIds.size() == 0) {
                findAll = repository.findAll(Specification.where(SaleOderSpecification.type(-1)));
            }else {
                findAll = repository.findAll(Specification.where(SaleOderSpecification.hasNameOrPhone(customerIds))
                        .and(SaleOderSpecification.hasFromDateToDate(saleOrderFilter.getFromDate(), saleOrderFilter.getToDate()))
                        .and(SaleOderSpecification.hasOrderNumber(saleOrderFilter.getOrderNumber()))
                        .and(SaleOderSpecification.type(2)));
            }
        }
        for (SaleOrder orderReturn: findAll) {
            SaleOrder saleOrder = new SaleOrder();
            if (repository.findById(orderReturn.getFromSaleOrderId()).isPresent())
                saleOrder = repository.findById(orderReturn.getFromSaleOrderId()).get();
            UserDTO user = userClient.getUserByIdV1(orderReturn.getSalemanId());
            CustomerDTO customer = customerClient.getCustomerByIdV1(orderReturn.getCustomerId()).getData();
            OrderReturnDTO orderReturnDTO = new OrderReturnDTO();
            orderReturnDTO.setId(orderReturn.getId());
            orderReturnDTO.setOrderReturnNumber(orderReturn.getOrderNumber());
            orderReturnDTO.setOrderNumber(saleOrder.getOrderNumber());
            orderReturnDTO.setOrderDate(saleOrder.getOrderDate());
            orderReturnDTO.setUserName(user.getFirstName()+" "+user.getLastName());
            orderReturnDTO.setCustomerNumber(customer.getCustomerCode());
            orderReturnDTO.setCustomerName(customer.getFirstName()+" "+customer.getLastName());
            orderReturnDTO.setDateReturn(orderReturn.getCreatedAt());
            orderReturnDTO.setAmount(orderReturn.getAmount());
            orderReturnDTO.setDiscount(orderReturn.getTotalPromotion());
            orderReturnDTO.setTotal(orderReturn.getTotal());
            totalAmount =  totalAmount + orderReturn.getAmount();
            totalPayment = totalPayment + orderReturn.getTotal();
            orderReturnDTOList.add(orderReturnDTO);
        }
        OrderReturnTotalResponse totalResponse = new OrderReturnTotalResponse(totalAmount, totalPayment);
        Page<OrderReturnDTO> orderReturnResponse = new PageImpl<>(orderReturnDTOList);
        CoverResponse<Page<OrderReturnDTO>, OrderReturnTotalResponse> response =
                new CoverResponse(orderReturnResponse, totalResponse);
        return new Response<CoverResponse<Page<OrderReturnDTO>, OrderReturnTotalResponse>>()
                .withData(response);
    }

    @Override
    public Response<OrderReturnDetailDTO> getOrderReturnDetail(long orderReturnId) {
        Response<OrderReturnDetailDTO> response = new Response<>();
        OrderReturnDetailDTO orderReturnDetailDTO = new OrderReturnDetailDTO();
        orderReturnDetailDTO.setInfos(getInfos(orderReturnId));
        orderReturnDetailDTO.setProductReturn(getProductReturn(orderReturnId));
        orderReturnDetailDTO.setPromotionReturn(getPromotionReturn(orderReturnId));
        response.setData(orderReturnDetailDTO);
        return response;
    }

    public InfosReturnDetailDTO getInfos(long orderReturnId){
        InfosReturnDetailDTO infosReturnDetailDTO = new InfosReturnDetailDTO();
        SaleOrder orderReturn = repository.findById(orderReturnId).get();
        infosReturnDetailDTO.setOrderDate(orderReturn.getOrderDate()); //order date
        CustomerDTO customer =
                customerClient.getCustomerByIdV1(orderReturn.getCustomerId()).getData();
        infosReturnDetailDTO.setCustomerName(customer.getFirstName()+" "+customer.getLastName());
        ApParamDTO apParamDTO = apparamClient.getApParamByCodeV1(orderReturn.getReasonId()).getData();
        infosReturnDetailDTO.setReason(apParamDTO.getApParamName());
        infosReturnDetailDTO.setReasonDesc(orderReturn.getReasonDesc());
        infosReturnDetailDTO.setReturnDate(orderReturn.getCreatedAt()); //order return
        UserDTO user = userClient.getUserByIdV1(orderReturn.getSalemanId());
        infosReturnDetailDTO.setUserName(user.getFirstName()+" "+user.getLastName());
        return  infosReturnDetailDTO;
    }

    public List<ProductReturnDTO> getProductReturn(long orderReturnId) {
        List<SaleOrderDetail> productReturns = saleOrderDetailRepository.getBySaleOrderId(orderReturnId);
        List<ProductReturnDTO> productReturnDTOList = new ArrayList<>();
        for (SaleOrderDetail productReturn:productReturns ) {
            Product product = productRepository.findById(productReturn.getProductId()).get();
            ProductReturnDTO productReturnDTO = new ProductReturnDTO();
            productReturnDTO.setProductCode(product.getProductCode());
            productReturnDTO.setProductName(product.getProductName());
            productReturnDTO.setUnit(product.getUom1());
            productReturnDTO.setQuantity(productReturn.getQuantity());
            productReturnDTO.setPricePerUnit(productReturn.getPrice());
            productReturnDTO.setTotalPrice(productReturn.getAmount());
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
            productReturnDTO.setPaymentReturn(productReturn.getTotal());
            productReturnDTOList.add(productReturnDTO);
        }
        return productReturnDTOList;
    }

    public List<PromotionReturnDTO> getPromotionReturn(long orderReturnId) {
        List<SaleOrderDetail> promotionReturns = saleOrderDetailRepository.getSaleOrderDetailPromotion(orderReturnId);
        List<PromotionReturnDTO> promotionReturnsDTOList = new ArrayList<>();
        for (SaleOrderDetail promotionReturn:promotionReturns) {
            Product product = productRepository.findById(promotionReturn.getProductId()).get();
            PromotionReturnDTO promotionReturnDTO = new PromotionReturnDTO();
            promotionReturnDTO.setProductCode(product.getProductCode());
            promotionReturnDTO.setProductName(product.getProductName());
            promotionReturnDTO.setUnit(product.getUom1());
            promotionReturnDTO.setQuantity(promotionReturn.getQuantity());
            promotionReturnDTO.setPricePerUnit(0);
            promotionReturnDTO.setPaymentReturn(0);
            promotionReturnsDTOList.add(promotionReturnDTO);
        }
        return promotionReturnsDTOList;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Response<SaleOrder> createOrderReturn(OrderReturnRequest request) {
        Response<SaleOrder> response = new Response<>();
        if (request == null)
            throw new ValidateException(ResponseMessage.REQUEST_BODY_NOT_BE_NULL);
        SaleOrder saleOrder = repository.getSaleOrderByNumber(request.getOrderNumber());
        if(saleOrder == null)
            throw new ValidateException(ResponseMessage.ORDER_RETURN_DOES_NOT_EXISTS);
        Date date = new Date();
        double diff = date.getTime() - saleOrder.getOrderDate().getTime();
        double diffDays = diff / (24 * 60 * 60 * 1000);
        SaleOrder newOrderReturn = new SaleOrder();
        if(diffDays <= 2) {
            Calendar cal = dateToCalendar(request.getDateReturn());
            long day = cal.get(Calendar.DATE);
            long month = cal.get(Calendar.MONTH) + 1;
            String  year = Integer.toString(cal.get(Calendar.YEAR)).substring(2);
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            NewOrderReturnDTO newOrderReturnDTO = modelMapper.map(saleOrder, NewOrderReturnDTO.class);
            newOrderReturn =  modelMapper.map(newOrderReturnDTO, SaleOrder.class);
            String orderNumber = createOrderReturnNumber(saleOrder.getShopId(), day, month, year);
            newOrderReturn.setOrderNumber(orderNumber); // important
            newOrderReturn.setFromSaleOrderId(saleOrder.getId());
            newOrderReturn.setCreatedAt(request.getDateReturn());
            newOrderReturn.setCreateUser(request.getCreateUser());
            newOrderReturn.setType(2);
            newOrderReturn.setReasonId(request.getReasonId());
            newOrderReturn.setReasonDesc(request.getReasonDescription());
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
                orderDetailReturn.setCreatedAt(orderReturn.getCreatedAt());
                orderDetailReturn.setCreateUser(orderReturn.getCreateUser());
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
                promotionReturn.setCreatedAt(orderReturn.getCreatedAt());
                promotionReturn.setCreateUser(orderReturn.getCreateUser());
                promotionReturn.setPrice(0F);
                promotionReturn.setAmount(0F);
                promotionReturn.setTotal(0F);
                saleOrderDetailRepository.save(promotionReturn);
            }
        }else {
            response.setFailure(ResponseMessage.ORDER_EXPIRED_FOR_RETURN);
            return response;
        }
        return response.withData(newOrderReturn);
    }

    public Response<List<SaleOrderDTO>> getSaleOrderForReturn(SaleOrderChosenFilter filter) {
        if (filter.getFromDate() == null || filter.getToDate() == null) {
            LocalDate initial = LocalDate.now();
            filter.setFromDate(Date.from(initial.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
            filter.setToDate(new Date());
        }
        String orderNumber = StringUtils.defaultIfBlank(filter.getOrderNumber(), StringUtils.EMPTY);
        String keyProduct = StringUtils.defaultIfBlank(filter.getProduct(), StringUtils.EMPTY);
        String nameLowerCase = VNCharacterUtils.removeAccent(filter.getProduct()).toUpperCase(Locale.ROOT);
        String checkLowerCaseNull = StringUtils.defaultIfBlank(nameLowerCase, StringUtils.EMPTY);
        Timestamp tsFromDate = new Timestamp(filter.getFromDate().getTime());
        LocalDateTime localDateTime = LocalDateTime.of(filter.getToDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalTime.MAX);
        Timestamp tsToDate = Timestamp.valueOf(localDateTime);
        List<Long> customerIds = customerClient.getIdCustomerBySearchKeyWordsV1(filter.getSearchKeyword()).getData();
        List<SaleOrder> saleOrders = new ArrayList<>();
        if(filter.getSearchKeyword() == null || filter.getSearchKeyword().equals("")) {
            saleOrders =
                    repository.getListSaleOrder(keyProduct, checkLowerCaseNull, orderNumber, customerIds, tsFromDate, tsToDate);
        }else {
            if(customerIds.size() == 0) {
                saleOrders = repository.findAll(Specification.where(SaleOderSpecification.type(-1)));
            }else {
                saleOrders =
                        repository.getListSaleOrder(keyProduct, checkLowerCaseNull, orderNumber, customerIds, tsFromDate, tsToDate);
            }
        }
        List<SaleOrderDTO> choose = new ArrayList<>();
        for(SaleOrder so:saleOrders) {
            UserDTO user = userClient.getUserByIdV1(so.getSalemanId());
            CustomerDTO customer = customerClient.getCustomerByIdV1(so.getCustomerId()).getData();
            SaleOrderDTO saleOrderDTO = new SaleOrderDTO();
            saleOrderDTO.setOrderNumber(so.getOrderNumber());
            saleOrderDTO.setOrderDate(so.getOrderDate());
            saleOrderDTO.setSalesManName(user.getFirstName()+" "+user.getLastName());
            saleOrderDTO.setCustomerName(customer.getFirstName()+" "+customer.getLastName());
            saleOrderDTO.setTotal(so.getTotal());
            choose.add(saleOrderDTO);
        }
        Response<List<SaleOrderDTO>> response = new Response<>();
        response.setData(choose);
        return response;
    }

    public Response<OrderReturnDetailDTO> getSaleOrderChosen(long id) {
        Response<OrderReturnDetailDTO> response = new Response<>();
        OrderReturnDetailDTO orderReturnDetailDTO = new OrderReturnDetailDTO();
        orderReturnDetailDTO.setInfos(getInfos(id));
        orderReturnDetailDTO.setProductReturn(getProductReturn(id));
        orderReturnDetailDTO.setPromotionReturn(getPromotionReturn(id));
        List<ApParamDTO> apParamDTOList = apparamClient.getApParamsV1().getData();
        List<String> reasons = new ArrayList<>();
        for(ApParamDTO ap:apParamDTOList) {
            String reasonName = ap.getApParamName();
            reasons.add(reasonName);
        }
        orderReturnDetailDTO.setReasonReturn(reasons);
        response.setData(orderReturnDetailDTO);
        return response;
    }

    public void updateReturn(long id){

    }

    public String createOrderReturnNumber(Long shopId, Long day, Long month, String year) {
        ShopDTO shop = shopClient.getByIdV1(shopId).getData();
        String shopCode = shop.getShopCode();
        int STT = repository.countOrderReturn() + 1;
        return  "SAL." +  shopCode + "." + year + month + day + Integer.toString(STT + 10000).substring(1);
    }
    private Calendar dateToCalendar(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;

    }
}
