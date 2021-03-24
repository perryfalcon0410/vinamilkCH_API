package vn.viettel.sale.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.authorization.User;
import vn.viettel.core.db.entity.common.Customer;
import vn.viettel.core.db.entity.sale.SaleOrder;
import vn.viettel.core.messaging.Response;

import vn.viettel.sale.repository.ProductRepository;
import vn.viettel.sale.repository.SaleOrderDetailRepository;
import vn.viettel.sale.repository.SaleOrderRepository;
import vn.viettel.sale.service.SaleOrderService;
import vn.viettel.sale.service.dto.CustomerDTO;
import vn.viettel.sale.service.dto.SaleOrderDTO;
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
//    @Autowired
//    ProductPriceRepository productPriceRepository;



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
            User user = new User();
            try {
                user = userClient.getUserByUserName(so.getCreateUser());
            }catch (Exception e) {
                response.setFailure(ResponseMessage.USER_DOES_NOT_EXISTS);
                return response;
            }
            saleOrder.setCreatedBy(user.getId()); //userId;
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
}
