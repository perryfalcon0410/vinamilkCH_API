package vn.viettel.sale.service.impl;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.authorization.User;
import vn.viettel.core.db.entity.common.Customer;
import vn.viettel.core.db.entity.common.Price;
import vn.viettel.core.db.entity.common.Shop;
import vn.viettel.core.db.entity.sale.SaleOrder;
import vn.viettel.core.db.entity.sale.SaleOrderDetail;
import vn.viettel.core.db.entity.sale.SaleOrderType;
import vn.viettel.core.db.entity.stock.StockTotal;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.SaleService;
import vn.viettel.sale.service.dto.OrderDetailDTO;
import vn.viettel.sale.service.dto.SaleOrderRequest;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.service.feign.UserClient;

import java.sql.Timestamp;
import java.util.Date;

@Service
public class SaleServiceImpl extends BaseServiceImpl<SaleOrder, SaleOrderRepository> implements SaleService {
    @Autowired
    SaleOrderDetailRepository detailRepo;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    StockTotalRepository stockTotalRepository;
    @Autowired
    ShopRepository shopRepository;
    @Autowired
    ProductPriceRepository priceRepository;
    @Autowired
    CustomerClient customerClient;
    @Autowired
    UserClient userClient;
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
        Customer customer = customerClient.getCustomerById(request.getCusId());
        if (customer == null)
            throw new ValidateException(ResponseMessage.CUSTOMER_NOT_EXIST);

        // check entity exist
        if (!shopRepository.existsByIdAndDeletedAtIsNull(request.getShopId()))
            throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
        if (SaleOrderType.getValueOf(request.getSaleOrderType()) == null)
            throw new ValidateException(ResponseMessage.SALE_ORDER_TYPE_NOT_EXIST);
//        if (request.getReceiptOnlineId() != null) {
//            if (!receiptOnlRepo.existsByIdAndDeletedAtIsNull(request.getReceiptOnlineId()))
//                throw new ValidateException(ResponseMessage.RECEIPT_ONLINE_NOT_EXIST);
//        }
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        SaleOrder saleOrder = modelMapper.map(request, SaleOrder.class);

        User user = userClient.getUserById(userId);
        if (user == null)
            throw new ValidateException(ResponseMessage.USER_DOES_NOT_EXISTS);
        saleOrder.setCreateUser(user.getUserAccount());
        saleOrder.setOrderNumber("UNKNOWN FORMAT");
        // save sale order to get sale order id
        try {
            repository.save(saleOrder);
        } catch (Exception e) {
            return response.withError(ResponseMessage.CREATE_FAILED);
        }
        /*
        redInvoiceId,
         */
        float totalPayment = 0;
        StockTotal stockTotal = stockTotalRepository.findById(new Long(1)).get();
        if (stockTotal == null)
            throw new ValidateException(ResponseMessage.STOCK_NOT_FOUND);

        for (OrderDetailDTO detail : request.getProducts()) {
            SaleOrderDetail orderDetail = modelMapper.map(detail, SaleOrderDetail.class);
            setSaleOrderDetail(orderDetail, saleOrder.getId(), user.getUserAccount());
            // minus quantity in stock when sale order success
            stockOut(stockTotal, detail.getQuantity());

            if (!productRepository.existsByIdAndDeletedAtIsNull(detail.getProductId()))
                throw new ValidateException(ResponseMessage.PRODUCT_NOT_FOUND);
            Price productPrice = priceRepository.findByProductId(detail.getProductId());

            totalPayment += productPrice.getPrice() * (detail.getQuantity());
            saleOrder.setTotalPaid(totalPayment);

            try {
                detailRepo.save(orderDetail);
            } catch (Exception e) {
                // if create order detail fail -> return back product quantity in stock and delete sale order
                repository.delete(saleOrder);
                createFailRollBack(stockTotal, detail.getQuantity());
                return response.withError(ResponseMessage.CREATE_FAILED);
            }
            stockTotalRepository.save(stockTotal);
        }
        repository.save(saleOrder);
        return response.withData(saleOrder);
    }

    public void stockOut(StockTotal stockTotal, int quantity) {
        stockTotal.setQuantity(stockTotal.getQuantity() - quantity);
    }

    public void createFailRollBack(StockTotal stockTotal, int quantity) {
        stockTotal.setQuantity(stockTotal.getQuantity() + quantity);
    }

    public void setSaleOrderDetail(SaleOrderDetail orderDetail, Long saleOrderId, String username) {
        orderDetail.setSaleOrderId(saleOrderId);
        orderDetail.setCreatedAt(time);
        orderDetail.setCreateUser(username);
    }

    @Override
    public Response<Shop> getShopById(long id) {
        Response<Shop> response = new Response<>();
        try {
            response.setData(shopRepository.findById(id).get());
        } catch (Exception e) {
            response.setFailure(ResponseMessage.SHOP_NOT_FOUND);
        }
        return response;
    }
}

