package vn.viettel.sale.service.impl;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.authorization.User;
import vn.viettel.core.db.entity.common.*;
import vn.viettel.core.db.entity.sale.SaleOrder;
import vn.viettel.core.db.entity.sale.SaleOrderComboDetail;
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
import java.util.List;

@Service
public class SaleServiceImpl extends BaseServiceImpl<SaleOrder, SaleOrderRepository> implements SaleService {
    @Autowired
    SaleOrderDetailRepository detailRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    StockTotalRepository stockTotalRepository;
    @Autowired
    ShopRepository shopRepository;
    @Autowired
    ProductPriceRepository priceRepository;
    @Autowired
    ComboProductRepository comboProductRepository;
    @Autowired
    ComboDetailRepository comboDetailRepository;
    @Autowired
    SaleOrderComboDetailRepository orderComboDetailRepository;
    //    @Autowired
//    ReceiptOnlineRepository receiptOnlineRepository;
    @Autowired
    CustomerClient customerClient;
    @Autowired
    UserClient userClient;
    @Autowired
    ModelMapper modelMapper;

    private final float VAT = (float) 0.1;
    private Date date = new Date();
    private Timestamp time = new Timestamp(date.getTime());

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Response<SaleOrder> createSaleOrder(SaleOrderRequest request, long userId) {
        Response<SaleOrder> response = new Response<>();

        if (request == null)
            throw new ValidateException(ResponseMessage.REQUEST_BODY_NOT_BE_NULL);

        Customer customer = customerClient.getCustomerById(request.getCustomerId()).getData();
        if (customer == null)
            throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
        User user = userClient.getUserById(userId);
        if (user == null)
            throw new ValidateException(ResponseMessage.USER_DOES_NOT_EXISTS);

        // check entity exist
        if (!shopRepository.existsByIdAndDeletedAtIsNull(request.getShopId()))
            throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
        if (SaleOrderType.getValueOf(request.getOrderType()) == null)
            throw new ValidateException(ResponseMessage.SALE_ORDER_TYPE_NOT_EXIST);
        if (request.getFromSaleOrderId() != null) {
            if (!repository.existsByIdAndDeletedAtIsNull(request.getFromSaleOrderId()))
                throw new ValidateException(ResponseMessage.SALE_ORDER_TYPE_NOT_EXIST);
        }
//        if (receiptOnlineRepository.findByOnlineNumber(request.getOnlineNumber()))
//            throw new ValidateException(ResponseMessage.RECEIPT_ONLINE_NOT_EXIST);

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        SaleOrder saleOrder = modelMapper.map(request, SaleOrder.class);

        saleOrder.setCreateUser(user.getUserAccount());
        saleOrder.setOrderNumber("UNKNOWN FORMAT");

        // save sale order to get sale order id
        try {
            repository.save(saleOrder);
        } catch (Exception e) {
            return response.withError(ResponseMessage.CREATE_FAILED);
        }

        float totalPayment = 0;
        float totalPromotion = 0; // needed calculate
        float amount = 0; // ?

        for (OrderDetailDTO detail : request.getProducts()) {
            if (!productRepository.existsByIdAndDeletedAtIsNull(detail.getProductId()))
                throw new ValidateException(ResponseMessage.PRODUCT_NOT_FOUND);
            Product product = productRepository.findByIdAndDeletedAtIsNull(detail.getProductId());

            Price productPrice = priceRepository.findByProductId(detail.getProductId());
            if (productPrice == null)
                throw new ValidateException(ResponseMessage.NO_PRICE_APPLIED);

            if (product.getIsComno() != null && product.getIsComno() == true) {
                ComboProduct combo = comboProductRepository.findByIdAndDeletedAtIsNull(product.getComboProductId());
                stockOutCombo(request.getWareHouseTypeId(), combo);

                SaleOrderComboDetail orderComboDetail = modelMapper.map(detail, SaleOrderComboDetail.class);
                orderComboDetail.setComboProductId(combo.getId());
                orderComboDetail.setComboQuantity(combo.getNumProduct());
                setComboDetailCreatedInfo(orderComboDetail, saleOrder.getId(), user.getUserAccount(), productPrice.getPrice());
                try {
                    orderComboDetailRepository.save(orderComboDetail);
                } catch (Exception e) {
                    return response.withError(ResponseMessage.CREATE_FAILED);
                }
            } else {
                StockTotal stockTotal = getStockTotal(detail.getProductId(), request.getWareHouseTypeId());
                stockOut(stockTotal, detail.getQuantity());

                SaleOrderDetail orderDetail = modelMapper.map(detail, SaleOrderDetail.class);

                setDetailCreatedInfo(orderDetail, saleOrder.getId(), user.getUserAccount(),
                        productPrice.getPrice(), request.getShopId());
                try {
                    detailRepository.save(orderDetail);
                } catch (Exception e) {
                    return response.withError(ResponseMessage.CREATE_FAILED);
                }
            }
            totalPayment += productPrice.getPrice() * (detail.getQuantity()) - totalPromotion; // minus discount, vat and promotion later
        }
        saleOrder.setTotalPromotion(totalPromotion); // total money discount
        saleOrder.setTotal(totalPayment); // total payment of the bill
        saleOrder.setBalance(request.getTotalPaid() - (totalPayment + totalPromotion)); // change money
        setSaleOrderCreatedInfo(saleOrder, user.getUserAccount());

        repository.save(saleOrder);
        return response.withData(saleOrder);
    }

    @Transactional(rollbackFor = Exception.class)
    public void stockOut(StockTotal stockTotal, int quantity) {
        stockTotal.setQuantity(stockTotal.getQuantity() - quantity);
        stockTotalRepository.save(stockTotal);
    }

    @Transactional(rollbackFor = Exception.class)
    public void stockOutCombo(Long wareHouseTypeId, ComboProduct combo) {
        List<ComboProductDetail> comboDetails = comboDetailRepository.findByComboProductId(combo.getId());

        for (ComboProductDetail detail : comboDetails) {
            StockTotal stockTotal = getStockTotal(detail.getProductId(), wareHouseTypeId);
            stockOut(stockTotal, (int) (combo.getNumProduct() * detail.getFactor()));
            stockTotalRepository.save(stockTotal);
        }
    }

    public StockTotal getStockTotal(Long productId, Long wareHouseTypeId) {
        StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(productId, wareHouseTypeId);
        if (stockTotal == null)
            throw new ValidateException(ResponseMessage.STOCK_NOT_FOUND);
        return stockTotal;
    }

    public void setDetailCreatedInfo(SaleOrderDetail orderDetail, Long saleOrderId, String username, float price, Long shopId) {
        orderDetail.setOrderDate(time);
        orderDetail.setCreatedAt(time);
        orderDetail.setCreateUser(username);
        orderDetail.setCreatedAt(time);
        orderDetail.setSaleOrderId(saleOrderId);
        orderDetail.setPrice(price);
        orderDetail.setPriceNotVat(price - price * VAT);
        orderDetail.setShopId(shopId);
    }

    public void setComboDetailCreatedInfo(SaleOrderComboDetail orderComboDetail, Long saleOrderId, String username, float price) {
        orderComboDetail.setOrderDate(time);
        orderComboDetail.setCreatedAt(time);
        orderComboDetail.setCreateUser(username);
        orderComboDetail.setCreatedAt(time);
        orderComboDetail.setSaleOrderId(saleOrderId);
        orderComboDetail.setPrice(price);
        orderComboDetail.setPriceNotVat(price - price * VAT);
    }

    public void setSaleOrderCreatedInfo(SaleOrder saleOrder, String username) {
        saleOrder.setOrderDate(time);
        saleOrder.setCreatedAt(time);
        saleOrder.setCreateUser(username);
    }

    public float getTotalDiscount() {
        return 0;
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

