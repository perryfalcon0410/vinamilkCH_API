package vn.viettel.saleservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.Company;
import vn.viettel.core.db.entity.Customer;
import vn.viettel.core.db.entity.Product;
import vn.viettel.core.db.entity.SaleOrder;
import vn.viettel.core.messaging.Response;

import vn.viettel.saleservice.repository.CompanyRepository;
import vn.viettel.saleservice.repository.ProductRepository;
import vn.viettel.saleservice.repository.SaleOrderRepository;
import vn.viettel.saleservice.service.SaleOrderService;
import vn.viettel.saleservice.service.dto.CustomerResponse;
import vn.viettel.saleservice.service.dto.SaleOrderDTO;
import vn.viettel.saleservice.service.feign.CustomerClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Bat buoc phai co @Service
@Service
public class SaleOrderServiceImpl implements SaleOrderService {
    @Autowired
    SaleOrderRepository saleOrderRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CustomerClient customerClient;
    @Autowired
    CompanyRepository companyRepository;
//    @Autowired
//    SaleOrderDetailRepository saleOrderDetailRepository;

//    @Override
//    public Response<List<SaleOrderDTO>> getAllSaleOrder() {
//        //List<SaleOrder> saleOrders = saleOrderRepository.findAll();
//        List<SaleOrderDTO> saleOrdersList = new ArrayList<>();
//            SaleOrderDTO saleOrder = new SaleOrderDTO();
//            saleOrder.setId(1);
//            saleOrder.setOrderNumber("HD001");
//            saleOrder.setCusNumber("CUS.CH40235.001");
//            saleOrder.setCusName("Phan Bảo Châu");
//            saleOrder.setCreatedAt(LocalDateTime.now());
//            saleOrder.setTotal(16800);
//            saleOrder.setDiscount(0);
//            saleOrder.setAccumulation(0);
//            saleOrder.setPaid(16800);
//            saleOrder.setNote("");
//            saleOrder.setRedReceipt(false);
//            saleOrder.setComName("Công ty TNHH Tekc");
//            saleOrder.setTaxCode("1000023687");
//            saleOrder.setAddress("Sô 10, đường Tân Trào, Phường Tân Phú, Quận 7");
//            //saleOrder.setDescription("");
//            saleOrdersList.add(saleOrder);
//        Response<List<SaleOrderDTO>> response = new Response<>();
//        response.setData(saleOrdersList);
//        return response;
//    }

    @Override
    public Response<List<SaleOrderDTO>> getAllSaleOrder() {
        String cusName, cusCode, comName, comAdrs, taxCode;

        List<SaleOrderDTO> saleOrdersList = new ArrayList<>();
        List<SaleOrder> saleOrders = saleOrderRepository.findAll();
        for(SaleOrder so: saleOrders) {
            Response<CustomerResponse> cusResponse = customerClient.getById(so.getCusId());
            CustomerResponse cus = cusResponse.getData();
            cusName = cus.getFirstName() +" "+ cus.getLastName();
            cusCode = cus.getCusCode();
            taxCode = cus.getTaxCode();
            comName = cus.getCompany();
            comAdrs = cus.getCompanyAddress();

            SaleOrderDTO saleOrder = new SaleOrderDTO();
            saleOrder.setId(so.getId());
            saleOrder.setOrderNumber(so.getCode());
            saleOrder.setCusId(so.getCusId());
            saleOrder.setCusNumber(cusCode);
            saleOrder.setCusName(cusName);
            saleOrder.setCreatedAt(so.getCreatedAt());
            //chưa set
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

    public Response<CustomerResponse> getCus(long id){
        Response<CustomerResponse> response = customerClient.getById(id);
        return response;
    }

//    public long totalPayment(long id) {
//        Product product = productRepository.findById(id);
//        long total = product.get
//    }
//    public Response<List<SaleOrderDetailDTO>> getAllSaleOrderDetail() {
//        List<SaleOrderDetail> saleOrderDetails = saleOrderDetailRepository.findAll();
//        // sysout o day, roi chay api tren postman, roi vo console de coi ketqua:))
//        System.out.println("TEST: " + saleOrderDetails.get(0).getAmount());
//        List<SaleOrderDetailDTO> saleOrdersDetailList = new ArrayList<>();
//        for (SaleOrderDetail sod : saleOrderDetails) {
//            SaleOrderDetailDTO saleOrderDetail = new SaleOrderDetailDTO();
//            saleOrderDetail.setId(sod.getId());
//            saleOrderDetail.setSaleOrderDetailId(sod.getSaleOrderDetailId());
//            saleOrderDetail.setSaleOrderId(sod.getSaleOrderId());
//            saleOrderDetail.setOrderDate(sod.getOrderDate());
//            saleOrderDetail.setShopId(sod.getShopId());
//            saleOrderDetail.setStaffId(sod.getStaffId());
//            saleOrderDetail.setProductId(sod.getProductId());
//            saleOrderDetail.setConvfact(sod.getConvfact());
//            saleOrderDetail.setCatId(sod.getCatId());
//            saleOrderDetail.setQuantity(sod.getQuantity());
//            saleOrderDetail.setQuantityRetail(sod.getQuantityRetail());
//            saleOrderDetail.setQuantityPackage(sod.getQuantityPackage());
//            saleOrderDetail.setIsFreeItem(sod.getIsFreeItem());
//            saleOrderDetail.setDiscountPercent(sod.getDiscountPercent());
//            saleOrderDetail.setDiscountAmount(sod.getDiscountAmount());
//            saleOrderDetail.setAmount(sod.getAmount());
//            saleOrderDetail.setPriceId(sod.getPriceId());
//            saleOrderDetail.setPrice(sod.getPrice());
//            saleOrderDetail.setPriceNotVat(sod.getPriceNotVat());
//            saleOrderDetail.setVat(sod.getVat());
//            saleOrderDetail.setTotalWeight(sod.getTotalWeight());
//            saleOrderDetail.setProgrameTypeCode(sod.getProgrameTypeCode());
//            saleOrdersDetailList.add(saleOrderDetail);
//        }
//        Response<List<SaleOrderDetailDTO>> response = new Response<>();
//        response.setData(saleOrdersDetailList);
//        return response;
//    }
}
