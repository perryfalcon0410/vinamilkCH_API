package vn.viettel.sale.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.authorization.User;
import vn.viettel.core.db.entity.common.Customer;
import vn.viettel.core.db.entity.common.Product;
import vn.viettel.core.db.entity.common.Shop;
import vn.viettel.core.db.entity.sale.SaleOrder;
import vn.viettel.core.db.entity.sale.SaleOrderComboDetail;
import vn.viettel.core.db.entity.sale.SaleOrderDetail;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.controller.PromotionReturnDTO;
import vn.viettel.sale.repository.ProductRepository;
import vn.viettel.sale.repository.SaleOrderDetailRepository;
import vn.viettel.sale.repository.SaleOrderRepository;
import vn.viettel.sale.repository.ShopRepository;
import vn.viettel.sale.service.OrderReturnService;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.service.feign.UserClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    ShopRepository shopRepository;

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
            Customer customer = customerClient.getCustomerById(orderReturn.getCustomerId()).getData();

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
        Customer customer = customerClient.getCustomerById(orderReturn.getCustomerId()).getData();
        orderReturnDetailDTO.setCustomerName(customer.getFirstName()+" "+customer.getLastName());
        orderReturnDetailDTO.setReason(orderReturn.getNote());//???
        orderReturnDetailDTO.setReturnDate(orderReturn.getCreatedAt()); //order return
        User user = userClient.getUserById(orderReturn.getSalemanId());
        orderReturnDetailDTO.setUserName(user.getFirstName()+" "+user.getLastName());
        orderReturnDetailDTO.setNote(orderReturn.getNote());
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
            float totalPrice = productReturn.getQuantity() * productReturn.getPrice();
            productReturnDTO.setTotalPrice(totalPrice);
            float discount = productReturn.getAutoPromotion() + productReturn.getZmPromotion();
            productReturnDTO.setDiscount(discount);
            productReturnDTO.setPaymentReturn(totalPrice - discount);
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
    public Response<SaleOrder> createOrderReturn(OrderReturnRequest request) {
        Response<SaleOrder> response = new Response<>();
        if (request == null)
            throw new ValidateException(ResponseMessage.REQUEST_BODY_NOT_BE_NULL);
        SaleOrder saleOrder = repository.getSaleOrderByNumber(request.getOrderNumber());
        if(saleOrder == null)
            throw new ValidateException(ResponseMessage.ORDER_RETURN_DOES_NOT_EXISTS);
        Date date = new Date();
        long diff = date.getTime() - saleOrder.getOrderDate().getTime();
        long diffDays = diff / (24 * 60 * 60 * 1000);
        if(diffDays <= 2) {
            long day = request.getDateReturn().getDate();
            long month = request.getDateReturn().getMonth();
            String  year = Integer.toString(request.getDateReturn().getYear()).substring(2);
            SaleOrder orderReturn = orderReturn = modelMapper.map(saleOrder, SaleOrder.class);
            String orderNumber = createOrderReturnNumber(saleOrder.getShopId(), day, month, year);
            orderReturn.setOrderNumber(orderNumber); // important
            orderReturn.setFromSaleOrderId(saleOrder.getId());
            orderReturn.setCreatedAt(request.getDateReturn());
            orderReturn.setCreateUser(request.getCreateUser());
            //missing reasonId, reasonDesc;
            repository.save(orderReturn); //save new orderReturn

            List<SaleOrderDetail> saleOrderDetail = saleOrderDetailRepository.getBySaleOrderId(saleOrder.getId());
        }
        return null;
    }
    public String createOrderReturnNumber(Long shopId, Long day, Long month, String year) {
        Shop shop = shopRepository.findById(shopId).get();
        String shopCode = shop.getShopCode();
        int STT = repository.countOrderReturn() + 1;
        return  "SAL." +  shopCode + "." + year + month + day + Integer.toString(STT + 10000).substring(1);
    }
}
