package vn.viettel.saleservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.*;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.saleservice.repository.*;
import vn.viettel.saleservice.service.SaleService;
import vn.viettel.saleservice.service.dto.SaleOrderDetailDto;
import vn.viettel.saleservice.service.dto.SaleOrderRequest;
import vn.viettel.saleservice.service.feign.CustomerClient;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class SaleServiceImpl extends BaseServiceImpl<SaleOrder, SaleOrderRepository> implements SaleService {
    @Autowired
    SaleOrderRepository orderRepo;

    @Autowired
    SaleOrderDetailRepository detailRepo;

    @Autowired
    ReceiptOnlineRepository receiptOnlRepo;

    @Autowired
    ReceiptTypeRepository typeRepo;

    @Autowired
    ProductRepository proRepo;

    @Autowired
    StockTotalRepository stockRepo;

    @Autowired
    CustomerClient customerClient;

    @Override
    public Response<SaleOrder> createSaleOrder(SaleOrderRequest request, long userId) {
        Response<SaleOrder> response = new Response<>();
        SaleOrder saleOrder = new SaleOrder();

        Date date = new Date();
        LocalDateTime time = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        if (request == null) {
            response.setFailure(ResponseMessage.INVALID_BODY);
            return response;
        }
        // set data for sale order
        Customer customer;
        try {
            // get customer by call api from customer service
            customer = customerClient.findById(request.getCusId());
        } catch (Exception e) {
            response.setFailure(ResponseMessage.CUSTOMER_NOT_EXIST);
            return response;
        }

        if (customer == null) {
            response.setFailure(ResponseMessage.CUSTOMER_NOT_EXIST);
            return response;
        }
        saleOrder.setCusId(customer.getId());

        try {
            // get receipt type by id
            ReceiptType type = typeRepo.findById(request.getReceiptTypeId()).get();
            saleOrder.setReceiptTypeId(type.getId());

            // if order type is offline -> not allow to add receipt online number
            if (request.getSaleOrderTypeId() != 1) {
                // get receipt online by id
                ReceiptOnline receiptOnline = receiptOnlRepo.findById(request.getReceiptOnlineId()).get();
                saleOrder.setReceiptOnlineId(receiptOnline.getId());
            } else
                saleOrder.setReceiptOnlineId(0);
            saleOrder.setSaleOrderTypeId(request.getSaleOrderTypeId());

        } catch (Exception e) {
            response.setFailure(ResponseMessage.DATA_NOT_FOUND);
            return response;
        }
        // set data
        saleOrder.setPaymentMethod(request.getPaymentMethod());
        saleOrder.setNote(request.getNote());
        saleOrder.setRedReceiptNote(request.getRedReceiptNote());
        saleOrder.setRedReceiptExport(request.isRedReceiptExport());
        saleOrder.setCreatedAt(time);
        saleOrder.setCreatedBy(userId);
        // save sale order to get sale order id
        try {
            orderRepo.save(saleOrder);
        } catch (Exception e) {
            System.out.println("ErrOr: " + e);
            response.setFailure(ResponseMessage.CREATE_FAILED);
            return response;
        }

        int totalPayment = 0;
        for (SaleOrderDetailDto detail : request.getProducts()) {
            SaleOrderDetail orderDetail = new SaleOrderDetail();
            StockTotal stock;
            try {
                stock = stockRepo.findStockTotalByProductIdAndWareHouseId(
                        detail.getProductId(), request.getWareHouseId());
                if (detail.getQuantity() > stock.getAvailableQuantity()) {
                    response.setFailure(ResponseMessage.PRODUCT_OUT_OF_STOCK);
                    return response;
                }
                // minus quantity in stock when sale order success
                stock.setQuantity(stock.getQuantity() - detail.getQuantity());
                stock.setAvailableQuantity(stock.getAvailableQuantity() - detail.getQuantity());
            } catch (Exception e) {
                response.setFailure(ResponseMessage.STOCK_NOT_FOUND);
                return response;
            }

            try {
                Product product = proRepo.findById(detail.getProductId()).get();

                /* table PRODUCT_PRICES still not create
                   table PRODUCTS still not have field PRODUCT_PRICE_ID
                   100000 is sample data
                 */
                // totalPayment += product.getPrice()*(detail.getQuantity());
                totalPayment += 100000*(detail.getQuantity());
                saleOrder.setTotalPayment(totalPayment);
            } catch (Exception e) {
                response.setFailure(ResponseMessage.PRODUCT_NOT_FOUND);
                return response;
            }
            // set data for sale order detail
            orderDetail.setSaleOrderId(saleOrder.getId());
            orderDetail.setProductId(detail.getProductId());
            orderDetail.setQuantity(detail.getQuantity());
            orderDetail.setCreatedAt(time);
            orderDetail.setCreatedBy(userId);
            try {
                detailRepo.save(orderDetail);
                saleOrder.setSaleOrderDetailId(orderDetail.getId());
            } catch (Exception e) {
                // if create order detail fail -> return back product quantity in stock and delete sale order
                orderRepo.delete(saleOrder);
                stock.setQuantity(stock.getQuantity() + detail.getQuantity());
                stock.setAvailableQuantity(stock.getAvailableQuantity() + detail.getQuantity());
                response.setFailure(ResponseMessage.CREATE_FAILED);
                return response;
            }
            stockRepo.save(stock);
        }
        orderRepo.save(saleOrder);
        response.setData(saleOrder);
        return response;
    }

}
