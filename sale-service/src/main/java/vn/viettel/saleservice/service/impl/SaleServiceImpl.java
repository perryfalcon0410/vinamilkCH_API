package vn.viettel.saleservice.service.impl;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.*;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.saleservice.repository.*;
import vn.viettel.saleservice.service.SaleService;
import vn.viettel.saleservice.service.dto.OrderDetailDTO;
import vn.viettel.saleservice.service.dto.SaleOrderRequest;
import vn.viettel.saleservice.service.feign.CustomerClient;

import java.sql.Timestamp;
import java.util.Date;

@Service
public class SaleServiceImpl extends BaseServiceImpl<SaleOrder, SaleOrderRepository> implements SaleService {
    @Autowired
    SaleOrderDetailRepository detailRepo;
    @Autowired
    ReceiptOnlineRepository receiptOnlRepo;
    @Autowired
    ProductRepository proRepo;
    @Autowired
    StockTotalRepository stockRepo;
    @Autowired
    ShopRepository shopRepo;
    @Autowired
    ProductPriceRepository priceRepo;
    @Autowired
    CustomerClient customerClient;
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    SaleOrderTypeRepository saleOrderTypeRepository;
    @Autowired
    WareHouseRepository wareHouseRepository;
    @Autowired
    ModelMapper modelMapper;

    private Date date = new Date();
    private Timestamp time = new Timestamp(date.getTime());

    @Override
    public Response<SaleOrder> createSaleOrder(SaleOrderRequest request, long userId) {
        Response<SaleOrder> response = new Response<>();

        if (request == null) {
            response.setFailure(ResponseMessage.INVALID_BODY);
            return response;
        }
        Customer customer = customerClient.findById(request.getCusId());
        if (customer == null)
            throw new ValidateException(ResponseMessage.CUSTOMER_NOT_EXIST);

        // check entity exist
        if (!shopRepo.existsByIdAndDeletedAtIsNull(request.getShopId()))
            throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
        if (!saleOrderTypeRepository.existsByIdAndDeletedAtIsNull(request.getSaleOrderTypeId()))
            throw new ValidateException(ResponseMessage.SALE_ORDER_TYPE_NOT_EXIST);
        if (!wareHouseRepository.existsByIdAndDeletedAtIsNull(request.getWareHouseId()))
            throw new ValidateException(ResponseMessage.WARE_HOUSE_NOT_EXIST);
        if (request.getReceiptOnlineId() != null) {
            if (!receiptOnlRepo.existsByIdAndDeletedAtIsNull(request.getReceiptOnlineId()))
                throw new ValidateException(ResponseMessage.RECEIPT_ONLINE_NOT_EXIST);
        }
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        SaleOrder saleOrder = modelMapper.map(request, SaleOrder.class);

        saleOrder.setCreatedBy(userId);
        saleOrder.setCode("UNKNOWN FORMAT");
        // save sale order to get sale order id
        try {
            repository.save(saleOrder);
        } catch (Exception e) {
            return response.withError(ResponseMessage.CREATE_FAILED);
        }
        /*
        redInvoiceId,
         */
        int totalPayment = 0;
        StockTotal stockTotal = stockRepo.findByWareHouseId(request.getWareHouseId());
        if (stockTotal == null)
            throw new ValidateException(ResponseMessage.STOCK_NOT_FOUND);

        for (OrderDetailDTO detail : request.getProducts()) {
            SaleOrderDetail orderDetail = modelMapper.map(detail, SaleOrderDetail.class);
            setSaleOrderDetail(orderDetail, saleOrder.getId(), userId);
            // minus quantity in stock when sale order success
            stockOut(stockTotal, detail.getQuantity());

            if (!proRepo.existsByIdAndDeletedAtIsNull(detail.getProductId()))
                throw new ValidateException(ResponseMessage.PRODUCT_NOT_FOUND);
            ProductPrice productPrice = priceRepo.findByProductId(detail.getProductId());

            totalPayment += productPrice.getPrice() * (detail.getQuantity());
            saleOrder.setTotalPayment(totalPayment);

            try {
                detailRepo.save(orderDetail);
                saleOrder.setSaleOrderDetailId(orderDetail.getId());

                Payment payment = createPayment(saleOrder, request.getCustomerRealPay());
                if (payment == null)
                    return response.withError(ResponseMessage.PAYMENT_FAIL);
                saleOrder.setPaymentId(payment.getId());
            } catch (Exception e) {
                // if create order detail fail -> return back product quantity in stock and delete sale order
                repository.delete(saleOrder);
                createFailRollBack(stockTotal, detail.getQuantity());
                return response.withError(ResponseMessage.CREATE_FAILED);
            }
            stockRepo.save(stockTotal);
        }
        repository.save(saleOrder);
        return response.withData(saleOrder);
    }

    public void stockOut(StockTotal stockTotal, int quantity) {
        stockTotal.setQuantity(stockTotal.getQuantity() - quantity);
        stockTotal.setAvailableQuantity(stockTotal.getAvailableQuantity() - quantity);
    }

    public void createFailRollBack(StockTotal stockTotal, int quantity) {
        stockTotal.setQuantity(stockTotal.getQuantity() + quantity);
        stockTotal.setAvailableQuantity(stockTotal.getAvailableQuantity() + quantity);
    }

    public void setSaleOrderDetail(SaleOrderDetail orderDetail, Long saleOrderId, Long userId) {
        orderDetail.setSaleOrderId(saleOrderId);
        orderDetail.setDiscount(0);
        orderDetail.setCreatedAt(time);
        orderDetail.setCreatedBy(userId);
    }

    public Payment createPayment(SaleOrder saleOrder, int cusRealPay) {
        // calculate discount
        int discount = 0; // add formula later

        Payment payment = new Payment();
        payment.setSaleOrderId(saleOrder.getId());
        payment.setTotalPayment(saleOrder.getTotalPayment());
        payment.setNeededPayment(saleOrder.getTotalPayment() - discount);
        payment.setCustomerRealPayment(cusRealPay);
        payment.setChangeMoney(payment.getCustomerRealPayment() - payment.getNeededPayment());

        try {
            paymentRepository.save(payment);
            saleOrder.setPaymentId(payment.getId());
            return payment;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Response<Shop> getShopById(long id) {
        Response<Shop> response = new Response<>();
        try {
            response.setData(shopRepo.findById(id).get());
        } catch (Exception e) {
            response.setFailure(ResponseMessage.SHOP_NOT_FOUND);
        }
        return response;
    }
}
