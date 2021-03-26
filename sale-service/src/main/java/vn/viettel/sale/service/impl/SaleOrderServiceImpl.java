package vn.viettel.sale.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.authorization.User;
import vn.viettel.core.db.entity.common.Customer;
import vn.viettel.core.db.entity.common.Product;
import vn.viettel.core.db.entity.sale.SaleOrder;
import vn.viettel.core.db.entity.sale.SaleOrderDetail;
import vn.viettel.core.messaging.Response;

import vn.viettel.sale.repository.ProductPriceRepository;
import vn.viettel.sale.repository.ProductRepository;
import vn.viettel.sale.repository.SaleOrderDetailRepository;
import vn.viettel.sale.repository.SaleOrderRepository;
import vn.viettel.sale.service.SaleOrderService;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.service.feign.UserClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class SaleOrderServiceImpl implements SaleOrderService {
    @Autowired
    SaleOrderRepository saleOrderRepository;
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



    @Override
    public Response<Page<SaleOrderDTO>> getAllSaleOrder(Pageable pageable) {
        String customerName, customerCode, companyName, companyAddress, taxCode;
        Response<Page<SaleOrderDTO>> response = new Response<>();
        List<SaleOrderDTO> saleOrdersList = new ArrayList<>();
        List<SaleOrder> saleOrders = saleOrderRepository.findAll();
        for(SaleOrder so: saleOrders) {
            Customer customer = new Customer();
            try {
                customer = customerClient.getCustomerById(so.getCustomerId());
            }catch (Exception e) {
                response.setFailure(ResponseMessage.CUSTOMER_NOT_EXIST);
                return response;
            }
            customerName = customer.getFirstName() +" "+ customer.getLastName();
            customerCode = customer.getCustomerCode();
            taxCode = customer.getTaxCode();
            companyName = customer.getWorkingOffice();
            companyAddress = customer.getOfficeAddress();

            SaleOrderDTO saleOrder = new SaleOrderDTO();
            saleOrder.setId(so.getId()); //soId
            saleOrder.setOrderNumber(so.getOrderNumber());
            saleOrder.setCusId(so.getCustomerId()); //cusId;
            saleOrder.setCusNumber(customerCode);
            saleOrder.setCusName(customerName);
            saleOrder.setCreatedAt(so.getCreatedAt());

            saleOrder.setAmount(so.getAmount());
            saleOrder.setDiscount(so.getTotalPromotion());
            saleOrder.setAccumulation(so.getCustomerPurchase());
            saleOrder.setTotal(so.getTotal());

            saleOrder.setNote(so.getNote());
            saleOrder.setRedReceipt(so.getUsedRedInvoice());
            saleOrder.setComName(companyName);
            saleOrder.setTaxCode(taxCode);
            saleOrder.setAddress(companyAddress);
            saleOrder.setNoteRed(so.getRedInvoiceRemark());
            saleOrdersList.add(saleOrder);
        }
        Page<SaleOrderDTO> saleOrderResponse = new PageImpl<>(saleOrdersList);
        response.setData(saleOrderResponse);
        return response;
    }

    public Response<List<SaleOrder>> getSaleOrders(){
        List<SaleOrder> saleOrders = saleOrderRepository.findAll();

        Response<List<SaleOrder>> response = new Response<>();
        response.setData(saleOrders);
        return response;
    }

    @Override
    public Response<Customer> getCustomer(Long id) {
        Customer customer = customerClient.getCustomerById(id);

        Response<Customer> response = new Response<>();
        response.setData(customer);
        return response;
    }

    public Response<SaleOrderDetailDTO> getSaleOrderDetail(GetOrderDetailRequest request) {
        Response<SaleOrderDetailDTO> response = new Response<>();
        SaleOrderDetailDTO orderDetail = new SaleOrderDetailDTO();
        try {
            orderDetail.setOrderDetail(getDetail(request.getSaleOrderId()));
        } catch (Exception e) {
            response.setFailure(ResponseMessage.SALE_ORDER_DETAIL_DOES_NOT_EXISTS);
            return response;
        }
        SaleOrder saleOrder = saleOrderRepository.findById(request.getSaleOrderId()).get();
        orderDetail.setCurrency("VND");
        orderDetail.setAmount(saleOrder.getAmount());//?
        orderDetail.setTotal(saleOrder.getTotal());
        orderDetail.setTotalPaid(saleOrder.getTotalPaid());//?
        orderDetail.setBalance(saleOrder.getBalance());

        User user = new User();
        try {
            user = userClient.getUserById(saleOrder.getSalemanId());
        } catch (Exception e) {
            response.setFailure(ResponseMessage.USER_DOES_NOT_EXISTS);
            return response;
        }
        orderDetail.setSaleMan(user.getFirstName()+ " " +user.getLastName());



//        try {
//            orderDetail.setDiscount(getDiscount(request.getSaleOrderId(),request.getDiscountId()));
//        }catch (Exception e) {
//            //response.setFailure(ResponseMessage.DISCOUNT_DOSE_NOT_EXISTS);
//            return response;
//        }
//        orderDetail.setPromotion(getPromotion(request.getSaleOrderId(),request.getCustomerId()));
//        response.setData(orderDetail);
        return response;
    }

    public List<OrderDetailDTO> getDetail(long saleOrderId) {
        float discount;
        List<SaleOrderDetail> saleOrderDetails = saleOrderDetailRepository.getBySaleOrderId(saleOrderId);
        List<OrderDetailDTO> saleOrderDetailList = new ArrayList<>();
        OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
        for (SaleOrderDetail saleOrderDetail: saleOrderDetails) {
            Product product = productRepository.findById(saleOrderDetail.getProductId()).get();
            orderDetailDTO.setProductId(saleOrderDetail.getProductId());
            orderDetailDTO.setProductCode(product.getProductCode());
            orderDetailDTO.setProductName(product.getProductName());
            orderDetailDTO.setUnit(product.getUom1());
            orderDetailDTO.setQuantity(saleOrderDetail.getQuantity());
            orderDetailDTO.setPrice(saleOrderDetail.getPriceNotVat());
            orderDetailDTO.setTotalPrice(saleOrderDetail.getAmount());

            discount = saleOrderDetail.getAutoPromotionNotVat() + saleOrderDetail.getZmPromotionNotVat();
            orderDetailDTO.setDiscount(discount);

            orderDetailDTO.setTotalPrice(saleOrderDetail.getTotal());
            saleOrderDetailList.add(orderDetailDTO);
        }
        return saleOrderDetailList;
    }

//    public List<PromotionDTO> getPromotion(long saleOrderId, long customerId) {
//        List<SaleOrderDetail> saleOrderDetails = saleOrderDetailRepository.getBySaleOrderId(saleOrderId);
//        Customer customer = customerClient.getCustomerById(customerId);
//        List<PromotionDTO> listPromotion = new ArrayList<>();
//        PromotionDTO promotionDTO = new PromotionDTO();
//        for(SaleOrderDetail saleOrderDetail: saleOrderDetails) {
//            promotionDTO.
//            listPromotion.add(promotion);
//        }
//        return listPromotion;
//    }
//
//    public List<DiscountDTO> getDiscount(long soId, long disId) {
//        List<SaleOrderDetail> saleOrderDetails = saleOrderDetailRepository.getBySaleOrderId(soId);
//        List<DiscountDTO> discountDTOList = new ArrayList<>();
//        for(SaleOrderDetail sod: saleOrderDetails) {
//            User user = userClient.getUserById(sod.getCreatedBy());
//            DiscountDTO discount = new DiscountDTO();
//            discount.setDiscountName("Tết Nguyên Đán");
//            discount.setDiscountType("Khuyến mãi giảm tiền");
//            discount.setDiscount(100000);
//            discount.setDisPercent(50);
//            discount.setCreatedBy(user.getFirstName()+" "+user.getLastName());
//            discount.setConfirmedBy(user.getFirstName()+" "+user.getLastName());
//            discountDTOList.add(discount);
//        }
//        return discountDTOList;
//    }
}
