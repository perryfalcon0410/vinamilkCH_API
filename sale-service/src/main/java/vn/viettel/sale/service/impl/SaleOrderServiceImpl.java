package vn.viettel.sale.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.authorization.User;
import vn.viettel.core.db.entity.common.Customer;
import vn.viettel.core.db.entity.common.Price;
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
    ProductRepository productRepository;
    @Autowired
    CustomerClient customerClient;
    @Autowired
    SaleOrderDetailRepository saleOrderDetailRepository;
    @Autowired
    UserClient userClient;
//    @Autowired
//    PaymentRepository paymentRepository;
    @Autowired
ProductPriceRepository productPriceRepository;



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
            orderDetail.setOrderDetail(getDetail(request.getSoId(),request.getPayId()));
        } catch (Exception e) {
            //response.setFailure(ResponseMessage.SALE_ORDER_DETAIL_DOES_NOT_EXISTS);
            return response;
        }

        try {
            orderDetail.setPayment(getPayment(request.getSoId(),request.getPayId()));
        } catch (Exception e) {
            //response.setFailure(ResponseMessage.PAYMENT_DOES_NOT_EXISTS);
            return response;
        }

        try {
            orderDetail.setDiscount(getDiscount(request.getSoId(),request.getDisId()));
        }catch (Exception e) {
            //response.setFailure(ResponseMessage.DISCOUNT_DOSE_NOT_EXISTS);
            return response;
        }
        orderDetail.setPromotion(getPromotion(request.getSoId(),request.getCusId()));
        response.setData(orderDetail);
        return response;
    }

    public List<OrderDetailDTO> getDetail(long soId, long payId) {
        List<SaleOrderDetail> saleOrderDetails = saleOrderDetailRepository.getBySaleOrderId(soId);
        List<OrderDetailDTO> saleOrderDetailList = new ArrayList<>();
        OrderDetailDTO sodr = new OrderDetailDTO();
        for (SaleOrderDetail sod: saleOrderDetails) {
            Product product = productRepository.findById(sod.getProductId()).get();
            Price price = productPriceRepository.findByProductId(sod.getProductId());
            Payment payment = paymentRepository.findById(payId).get();


            sodr.setProductId(sod.getProductId());

            sodr.setProductCode(product.getProductCode());
            sodr.setProductName(product.getProductName());
            sodr.setUnit(product.getUnit());
            sodr.setQuantity(sod.getQuantity());
            sodr.setPrice(proPrice.getPrice());

            float totalPrice = sod.getQuantity() * price.getPrice();
            sodr.setTotalPrice(totalPrice);

            sodr.setDiscount(payment.getDiscount());

            sodr.setPayment(totalPrice - payment.getDiscount());
            sodr.setNote(sod.getNote());
            saleOrderDetailList.add(sodr);
        }
        return saleOrderDetailList;
    }

    public PaymentDTO getPayment(long soId, long payId) {
        List<SaleOrderDetail> saleOrderDetails = saleOrderDetailRepository.getBySaleOrderId(soId);
        PaymentDTO response = new PaymentDTO();
        for(SaleOrderDetail sod: saleOrderDetails) {
            User user = userClient.getUserById(sod.getCreatedBy());
            Payment payment = paymentRepository.findById(payId).get();
            response.setChange(payment.getChangeMoney());
            response.setNeedPayment(payment.getNeededPayment());
            response.setCurrency(payment.getCurrency());
            response.setPaid(payment.getCustomerRealPayment());
            response.setCreatedBy(user.getFirstName() + " " + user.getLastName());
        }
        return response;
    }

    public List<PromotionDTO> getPromotion(long soId, long cusId) {
        List<SaleOrderDetail> saleOrderDetails = saleOrderDetailRepository.getBySaleOrderId(soId);
        Response<CustomerResponse> cusResponse = customerClient.getById(cusId);
        List<PromotionDTO> listPromotion = new ArrayList<>();
        for(SaleOrderDetail sod: saleOrderDetails) {
            CustomerResponse cus = cusResponse.getData();

            Product pro = productRepository.findById(sod.getProductId()).get();
            PromotionDTO promotion = new PromotionDTO();
            promotion.setProductCode(pro.getProductCode());
            promotion.setProductName(pro.getProductName());

            String cusName = cus.getFirstName() +" "+ cus.getLastName();
            promotion.setCustomerName(cusName);

            promotion.setQuantityPromotion(2);
            listPromotion.add(promotion);
        }
        return listPromotion;
    }

    public List<DiscountDTO> getDiscount(long soId, long disId) {
        List<SaleOrderDetail> saleOrderDetails = saleOrderDetailRepository.getBySaleOrderId(soId);
        List<DiscountDTO> discountDTOList = new ArrayList<>();
        for(SaleOrderDetail sod: saleOrderDetails) {
            User user = userClient.getUserById(sod.getCreatedBy());
            DiscountDTO discount = new DiscountDTO();
            discount.setDiscountName("Tết Nguyên Đán");
            discount.setDiscountType("Khuyến mãi giảm tiền");
            discount.setDiscount(100000);
            discount.setDisPercent(50);
            discount.setCreatedBy(user.getFirstName()+" "+user.getLastName());
            discount.setConfirmedBy(user.getFirstName()+" "+user.getLastName());
            discountDTOList.add(discount);
        }
        return discountDTOList;
    }
}
