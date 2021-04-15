package vn.viettel.sale.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.authorization.User;
import vn.viettel.core.db.entity.common.Product;
import vn.viettel.core.db.entity.common.Shop;
import vn.viettel.core.db.entity.sale.SaleOrder;
import vn.viettel.core.db.entity.sale.SaleOrderDetail;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.messaging.OrderReturnRequest;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.repository.ProductRepository;
import vn.viettel.sale.repository.SaleOrderDetailRepository;
import vn.viettel.sale.repository.SaleOrderRepository;
import vn.viettel.sale.service.OrderReturnService;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.service.feign.ShopClient;
import vn.viettel.sale.service.feign.UserClient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

    @Override
    public Response<Page<OrderReturnDTO>> getAllOrderReturn(Pageable pageable) {
        Response<Page<OrderReturnDTO>> response = new Response<>();
        List<OrderReturnDTO> orderReturnDTOList = new ArrayList<>();
        List<SaleOrder> orderReturnList = repository.getListOrderReturn();
        for (SaleOrder orderReturn:orderReturnList) {
            SaleOrder saleOrder = new SaleOrder();
            if (repository.findById(orderReturn.getFromSaleOrderId()).isPresent())
                saleOrder = repository.findById(orderReturn.getFromSaleOrderId()).get();
            User user = userClient.getUserById(orderReturn.getSalemanId());
            CustomerFeignDTO customer = customerClient.getCustomerFeignById(orderReturn.getCustomerId()).getData();

            OrderReturnDTO orderReturnDTO = new OrderReturnDTO();
            orderReturnDTO.setId(orderReturn.getId());
            orderReturnDTO.setOrderReturnNumber(orderReturn.getOrderNumber());
            orderReturnDTO.setOrderNumber(saleOrder.getOrderNumber());
            orderReturnDTO.setUserName(user.getFirstName()+" "+user.getLastName());
            orderReturnDTO.setCustomerNumber(customer.getCustomerCode());
            orderReturnDTO.setCustomerName(customer.getFirstName()+" "+customer.getLastName());
            orderReturnDTO.setDateReturn(orderReturn.getCreatedAt());
            orderReturnDTOList.add(orderReturnDTO);
        }
        Page<OrderReturnDTO> orderReturnDTOResponse = new PageImpl<>(orderReturnDTOList);
        response.setData(orderReturnDTOResponse);
        return response;
    }

    @Override
    public Response<OrderReturnDetailDTO> getOrderReturnDetail(long orderReturnId) {
        Response<OrderReturnDetailDTO> response = new Response<>();
        SaleOrder orderReturn = repository.findById(orderReturnId).get();
        OrderReturnDetailDTO orderReturnDetailDTO = new OrderReturnDetailDTO();
        orderReturnDetailDTO.setOrderDate(orderReturn.getOrderDate()); //order date
        CustomerFeignDTO customer =
                customerClient.getCustomerFeignById(orderReturn.getCustomerId()).getData();
        orderReturnDetailDTO.setCustomerName(customer.getFirstName()+" "+customer.getLastName());
        orderReturnDetailDTO.setReasonId(orderReturn.getReasonId());
        orderReturnDetailDTO.setReasonDesc(orderReturn.getReasonDesc());
        orderReturnDetailDTO.setReturnDate(orderReturn.getCreatedAt()); //order return
        User user = userClient.getUserById(orderReturn.getSalemanId());
        orderReturnDetailDTO.setUserName(user.getFirstName()+" "+user.getLastName());
        orderReturnDetailDTO.setProductReturn(getProductReturn(orderReturnId));
        orderReturnDetailDTO.setPromotionReturn(getPromotionReturn(orderReturnId));
        response.setData(orderReturnDetailDTO);
        return response;
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
    public String createOrderReturnNumber(Long shopId, Long day, Long month, String year) {
        Shop shop = shopClient.getById(shopId).getData();
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
