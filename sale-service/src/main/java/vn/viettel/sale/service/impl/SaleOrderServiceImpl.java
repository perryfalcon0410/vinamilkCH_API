package vn.viettel.sale.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.core.db.entity.SaleOrder;
import vn.viettel.core.messaging.Response;

import vn.viettel.sale.repository.ProductRepository;
import vn.viettel.sale.repository.SaleOrderRepository;
import vn.viettel.sale.service.SaleOrderService;
import vn.viettel.sale.service.dto.CustomerDTO;
import vn.viettel.sale.service.dto.SaleOrderDTO;
import vn.viettel.sale.service.feign.CustomerClient;

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


    @Override
    public Response<List<SaleOrderDTO>> getAllSaleOrder() {
        String cusName, cusCode, comName, comAdrs, taxCode;

        List<SaleOrderDTO> saleOrdersList = new ArrayList<>();
        List<SaleOrder> saleOrders = saleOrderRepository.findAll();
        for(SaleOrder so: saleOrders) {
            Response<CustomerDTO> cusResponse = customerClient.getCustomerById(so.getCusId());
            CustomerDTO cus = cusResponse.getData();
            cusName = cus.getFirstName() +" "+ cus.getLastName();
            cusCode = cus.getCustomerCode();
            taxCode = cus.getTaxCode();
            comName = cus.getCompanyName();
            comAdrs = cus.getCompanyAddress();

            SaleOrderDTO saleOrder = new SaleOrderDTO();
            saleOrder.setId(so.getId());
            saleOrder.setOrderNumber(so.getCode());
            saleOrder.setCusId(so.getCusId());
            saleOrder.setCusNumber(cusCode);
            saleOrder.setCusName(cusName);
            saleOrder.setCreatedAt(so.getCreatedAt());
            //
            saleOrder.setTotal(16800);
            saleOrder.setDiscount(0);
            saleOrder.setAccumulation(0);
            saleOrder.setPaid(16800);
            //
            saleOrder.setNote(so.getNote());
            saleOrder.setRedReceipt(so.isRedReceiptExport());
            saleOrder.setComName(comName);
            saleOrder.setTaxCode(taxCode);
            saleOrder.setAddress(comAdrs);
            saleOrdersList.add(saleOrder);
            saleOrder.setNoteRed(so.getRedReceiptNote());
        }
        Response<List<SaleOrderDTO>> response = new Response<>();
        response.setData(saleOrdersList);
        return response;
    }

    public Response<List<SaleOrder>> getSaleOrders(){
        List<SaleOrder> saleOrders = saleOrderRepository.findAll();

        Response<List<SaleOrder>> response = new Response<>();
        response.setData(saleOrders);
        return response;
    }

    public Response<CustomerDTO> getCustomer(Long id){
        return customerClient.getCustomerById(id);
    }

}
