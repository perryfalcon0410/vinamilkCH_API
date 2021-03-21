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
import java.util.Optional;

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
        SaleOrder saleOrder;

        if (request == null) {
            response.setFailure(ResponseMessage.INVALID_BODY);
            return response;
        }
        // get entity by id
        Customer customer = customerClient.findById(request.getCusId());
        Optional<Shop> shop = shopRepo.findById(request.getShopId());
        Optional<SaleOrderType> saleOrderType = saleOrderTypeRepository.findById(request.getSaleOrderTypeId());
        Optional<WareHouse> wareHouse = wareHouseRepository.findById(request.getWareHouseId());
        // check entity exist
        if (customer == null)
            throw new ValidateException(ResponseMessage.CUSTOMER_NOT_EXIST);
        if (shop == null)
            throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
        if (saleOrderType == null)
            throw new ValidateException(ResponseMessage.SALE_ORDER_TYPE_NOT_EXIST);
        if (wareHouse == null)
            throw new ValidateException(ResponseMessage.WARE_HOUSE_NOT_EXIST);

        if (request.getReceiptOnlineId() != null) {
            Optional<ReceiptOnline> receiptOnline = receiptOnlRepo.findById(request.getReceiptOnlineId());
            if (receiptOnline == null)
                throw new ValidateException(ResponseMessage.RECEIPT_ONLINE_NOT_EXIST);
        }

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        saleOrder = modelMapper.map(request, SaleOrder.class);

        saleOrder.setCreatedBy(userId);
        saleOrder.setCode("UNKNOWN FORMAT");

        /*
        code - done
        saleOrderDetailId - done
        redInvoiceId,
        totalPayment,
        paymentId,
         */

        // save sale order to get sale order id
        try {
            repository.save(saleOrder);
        } catch (Exception e) {
            System.out.println("ErrOr: " + e);
            response.setFailure(ResponseMessage.CREATE_FAILED);
            return response;
        }

        int totalPayment = 0;
        for (OrderDetailDTO detail : request.getProducts()) {
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
                ProductPrice productPrice = priceRepo.findById(product.getProductPriceId()).get();

                totalPayment += productPrice.getPrice()*(detail.getQuantity());
                saleOrder.setTotalPayment(totalPayment);
            } catch (Exception e) {
                response.setFailure(ResponseMessage.PRODUCT_NOT_FOUND);
                return response;
            }
            // set data for sale order detail
            orderDetail.setSaleOrderId(saleOrder.getId());
            orderDetail.setProductId(detail.getProductId());
            orderDetail.setQuantity(detail.getQuantity());
            orderDetail.setNote(detail.getNote());
            orderDetail.setCreatedAt(time);
            orderDetail.setCreatedBy(userId);
            try {
                detailRepo.save(orderDetail);
                saleOrder.setSaleOrderDetailId(orderDetail.getId());

                Payment payment = createPayment(saleOrder, request.getCustomerRealPay());
                if (payment == null) {
                    response.setFailure(ResponseMessage.PAYMENT_FAIL);
                    return response;
                }
            } catch (Exception e) {
                // if create order detail fail -> return back product quantity in stock and delete sale order
                repository.delete(saleOrder);
                stock.setQuantity(stock.getQuantity() + detail.getQuantity());
                stock.setAvailableQuantity(stock.getAvailableQuantity() + detail.getQuantity());
                response.setFailure(ResponseMessage.CREATE_FAILED);
                return response;
            }
            stockRepo.save(stock);
        }
        repository.save(saleOrder);
        response.setData(saleOrder);
        return response;
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
