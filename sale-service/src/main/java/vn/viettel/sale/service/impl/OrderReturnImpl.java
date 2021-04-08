package vn.viettel.sale.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.db.entity.authorization.User;
import vn.viettel.core.db.entity.common.Product;
import vn.viettel.core.db.entity.sale.SaleOrder;
import vn.viettel.core.db.entity.sale.SaleOrderDetail;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.controller.PromotionReturnDTO;
import vn.viettel.sale.repository.ProductRepository;
import vn.viettel.sale.repository.SaleOrderDetailRepository;
import vn.viettel.sale.repository.SaleOrderRepository;
import vn.viettel.sale.service.OrderReturnService;
import vn.viettel.sale.service.dto.CustomerDTO;
import vn.viettel.sale.service.dto.OrderReturnDTO;
import vn.viettel.sale.service.dto.OrderReturnDetailDTO;
import vn.viettel.sale.service.dto.ProductReturnDTO;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.service.feign.UserClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderReturnImpl implements OrderReturnService {

    @Autowired
    SaleOrderRepository saleOrderRepository;
    @Autowired
    SaleOrderDetailRepository saleOrderDetailRepository;
    @Autowired
    UserClient userClient;
    @Autowired
    CustomerClient customerClient;
    @Autowired
    ProductRepository productRepository;

    public Response<Page<OrderReturnDTO>> getAllOrderReturn(Pageable pageable) {
        Response<Page<OrderReturnDTO>> response = new Response<>();
        List<OrderReturnDTO> orderReturnDTOList = new ArrayList<>();
        List<SaleOrder> orderReturnList = saleOrderRepository.getListOrderReturn();
        for (SaleOrder orderReturn:orderReturnList) {
            SaleOrder saleOrder = saleOrderRepository.findById(orderReturn.getFromSaleOrderId()).get();
            User user = userClient.getUserById(orderReturn.getSalemanId());
            CustomerDTO customer = customerClient.getCustomerById(orderReturn.getCustomerId()).getData();

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

    public Response<OrderReturnDetailDTO> getOrderReturnDetail(long orderReturnId) {
        Response<OrderReturnDetailDTO> response = new Response<>();
        SaleOrder orderReturn = saleOrderRepository.findById(orderReturnId).get();
        OrderReturnDetailDTO orderReturnDetailDTO = new OrderReturnDetailDTO();
        orderReturnDetailDTO.setOrderDate(orderReturn.getOrderDate()); //order date
        CustomerDTO customer = customerClient.getCustomerById(orderReturn.getCustomerId()).getData();
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
}
