package vn.viettel.sale.service.impl;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.authorization.User;
import vn.viettel.core.db.entity.common.*;
import vn.viettel.core.db.entity.promotion.*;
import vn.viettel.core.db.entity.sale.SaleOrder;
import vn.viettel.core.db.entity.sale.SaleOrderComboDetail;
import vn.viettel.core.db.entity.sale.SaleOrderDetail;
import vn.viettel.core.db.entity.sale.SaleOrderType;
import vn.viettel.core.db.entity.stock.StockTotal;
import vn.viettel.core.db.entity.voucher.Voucher;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.SaleService;
import vn.viettel.sale.service.dto.OrderDetailDTO;
import vn.viettel.sale.service.dto.PromotionShopMapDTO;
import vn.viettel.sale.service.dto.SaleOrderRequest;
import vn.viettel.sale.service.dto.ZmFreeItemDTO;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.service.feign.PromotionClient;
import vn.viettel.sale.service.feign.UserClient;

import java.sql.Timestamp;
import java.util.ArrayList;
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
    @Autowired
    SaleOrderDetailRepository saleOrderDetailRepository;
    //    @Autowired
//    ReceiptOnlineRepository receiptOnlineRepository;
    @Autowired
    CustomerClient customerClient;
    @Autowired
    UserClient userClient;
    @Autowired
    PromotionClient promotionClient;
    @Autowired
    ModelMapper modelMapper;


    private final float VAT = (float) 0.1;
    private final Date date = new Date();
    private final Timestamp time = new Timestamp(date.getTime());

    private float totalPromotion = 0;
    private boolean isProductRejected = false;

    private List<PromotionShopMapDTO> promotionShopMapList = new ArrayList<>();

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

        float totalPromotion = 0; // needed calculate
        float autoPromotion = 0;
        float zmPromotion = 0;
        float amount = 0;
        float voucherDiscount = 0;

        Voucher voucher = null;
        if (request.getVoucherId() != null)
            voucher = promotionClient.getVouchers(request.getVoucherId()).getData();
        if (voucher != null) {
            setVoucherInUsed(voucher, saleOrder.getId());
            voucherDiscount = voucher.getPrice();
            saleOrder.setTotalVoucher(voucher.getPrice());
            saleOrder.setDiscountCodeAmount(voucher.getPrice());
        }

        for (OrderDetailDTO detail : request.getProducts()) {
            if (!productRepository.existsByIdAndDeletedAtIsNull(detail.getProductId()))
                throw new ValidateException(ResponseMessage.PRODUCT_NOT_FOUND);
            Product product = productRepository.findByIdAndDeletedAtIsNull(detail.getProductId());

            Price productPrice = priceRepository.findByProductId(detail.getProductId());
            if (productPrice == null)
                throw new ValidateException(ResponseMessage.NO_PRICE_APPLIED);

            // get list available promotion program id
            List<Long> promotionProgramIds = getListPromotionProgramId(request.getShopId());
            // get auto promotion
            getPromotion(detail, productPrice.getPrice(), promotionProgramIds, request.getShopId(), saleOrder.getId());

            if (product.getIsComno() != null && product.getIsComno()) {
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
                if (stockTotal.getQuantity() < detail.getQuantity())
                    throw new ValidateException(ResponseMessage.PRODUCT_OUT_OF_STOCK);
                stockOut(stockTotal, detail.getQuantity());

                SaleOrderDetail orderDetail = modelMapper.map(detail, SaleOrderDetail.class);

                setDetailCreatedInfo(orderDetail, saleOrder.getId(), user.getUserAccount(),
                        productPrice.getPrice(), detail.getQuantity(), request.getShopId());
                try {
                    detailRepository.save(orderDetail);
                } catch (Exception e) {
                    return response.withError(ResponseMessage.CREATE_FAILED);
                }
            }
            if (detail.getZmPromotion() != null)
                zmPromotion += detail.getZmPromotion();

            amount += productPrice.getPrice() * detail.getQuantity();
        }
        // if no product is rejected -> accept auto promotion
        if (!this.isProductRejected) {
            autoPromotion += this.totalPromotion;
            // set change for promotion shop map
            setChangePromotionShopMap();
        }
        // else set auto promotion for detail order to 0
        else
            setSaleOrderDetailRejected(saleOrder.getId());

        totalPromotion += voucherDiscount;
        totalPromotion += autoPromotion;
        totalPromotion += zmPromotion;

        setSaleOrderCreatedInfo(saleOrder, user.getUserAccount(), request.getTotalPaid(),
                totalPromotion, amount, autoPromotion, zmPromotion);

        repository.save(saleOrder);
        setZmPromotionFreeItemToSaleOrder(request.getFreeItemList(), saleOrder.getId(), saleOrder.getShopId());

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
            int quantity = (int) (detail.getFactor() * 1);
            if (stockTotal.getQuantity() < quantity)
                throw new ValidateException(ResponseMessage.PRODUCT_OUT_OF_STOCK);
            stockOut(stockTotal, quantity);
            stockTotalRepository.save(stockTotal);
        }
    }

    public StockTotal getStockTotal(Long productId, Long wareHouseTypeId) {
        StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(productId, wareHouseTypeId);
        if (stockTotal == null)
            throw new ValidateException(ResponseMessage.STOCK_NOT_FOUND);
        return stockTotal;
    }

    public void setDetailCreatedInfo(SaleOrderDetail orderDetail, Long saleOrderId, String username,
                                     float price, int quantity, Long shopId) {
        orderDetail.setOrderDate(time);
        orderDetail.setCreatedAt(time);
        orderDetail.setCreateUser(username);
        orderDetail.setCreatedAt(time);
        orderDetail.setSaleOrderId(saleOrderId);
        orderDetail.setPrice(price);
        orderDetail.setAmount(quantity * price);
        orderDetail.setTotal(quantity * price - this.totalPromotion);
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

    public void setSaleOrderCreatedInfo(SaleOrder saleOrder, String username, float totalPaid,
                                        float totalPromotion, float amount,
                                        float autoPromotion, float zmPromotion) {
        saleOrder.setOrderDate(time);
        saleOrder.setCreatedAt(time);
        saleOrder.setCreateUser(username);

        saleOrder.setAmount(amount);
        saleOrder.setTotalPromotion(totalPromotion); // total money discount
        saleOrder.setTotal(amount - totalPromotion); // total payment of the bill
        saleOrder.setBalance(totalPaid - (amount - totalPromotion)); // change money
        saleOrder.setAutoPromotion(autoPromotion);
        saleOrder.setZmPromotion(zmPromotion);
    }

    // call api from promotion service to set and save
    public void setVoucherInUsed(Voucher voucher, Long saleOrderId) {
        voucher.setIsUsed(true);
        voucher.setSaleOrderId(saleOrderId);
        voucher.setOrderDate(time);

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

    public boolean isCustomerMatchProgram(Long shopId, Customer customer, Long orderType) {
        List<PromotionCustATTR> promotionCusATTRS = promotionClient.getGroupCustomerMatchProgram(shopId).getData();
        if (promotionCusATTRS == null)
            return false;
        for (PromotionCustATTR cusATTR : promotionCusATTRS) {
            int type = cusATTR.getObjectType();
            switch (type) {
                case 2:
                    Customer cus = customerClient.getByIdAndType(customer.getId(), cusATTR.getObjectId()).getData();
                    if (cus != null)
                        return true;
                    break;
                case 20:
                    if (cusATTR.getObjectId() == orderType)
                        return true;
                    break;
            }
        }
        return false;
    }

    public void getPromotion(OrderDetailDTO detail, float price,
                             List<Long> promotionProgramIds,
                             Long shopId, Long saleOrderId) {
        List<PromotionProgramDetail> programDetails = promotionClient.getPromotionDetailByPromotionId(shopId).getData();
        List<PromotionProgramProduct> rejectedProducts = promotionClient.getRejectProduct(promotionProgramIds).getData();
//        Price price = priceRepository.findByProductId(detail.getProductId());

        if (programDetails.size() > 0)
            // for each product in bill
            if (rejectedProducts.size() > 0)
                // for each rejected item -> if 1 product is in rejected list -> no promotion for the bil
                for (PromotionProgramProduct product : rejectedProducts) {
                    if (detail.getProductId() == product.getProductId())
                        this.isProductRejected = true;
                }
        // for each promotion program detail -> if product is in promotion list and match condition -> discount
        for (PromotionProgramDetail promotionProgram : programDetails) {
            if (detail.getProductId() == promotionProgram.getProductId()) {
                // if sale quantity or sale amount match promotion requirement
                if ((promotionProgram.getSaleQty() != null && detail.getQuantity() >= promotionProgram.getSaleQty())
                        || (promotionProgram.getSaleAmt() != null && detail.getQuantity() * price >= promotionProgram.getSaleAmt())) {

                    // get sale order detail to set data
                    SaleOrderDetail saleOrderDetail = saleOrderDetailRepository
                            .findByProductIdAndSaleOrderId(detail.getProductId(), saleOrderId);
                    // get promotion shop map to change data
                    PromotionShopMap promotionShopMap = promotionClient.getPromotionShopMap(
                            promotionProgram.getPromotionProgramId(), shopId).getData();

                    // discount amount
                    if (promotionProgram.getDiscAmt() != null)
                        this.totalPromotion += promotionProgram.getDiscAmt();
                    // discount percent
                    if (promotionProgram.getDisPer() != null)
                        this.totalPromotion += (detail.getQuantity() * price) * promotionProgram.getDisPer();
                    // give free item
                    if (promotionProgram.getFreeProductId() != null) {
                        for (int i = 0; i < promotionProgram.getFreeQty(); i++) {
                            setAutoPromotionFreeItemToSaleOrder(saleOrderId, shopId, promotionProgram);
                        }
                    }

                    if (saleOrderDetail != null)
                        setSaleOrderPromotion(saleOrderDetail, promotionProgram.getDiscAmt(),
                                detail.getZmPromotion(), promotionProgram);

                    // add to list promotion shop map for change data
                    PromotionShopMapDTO promotionShopMapDTO = new PromotionShopMapDTO(promotionShopMap,
                            this.totalPromotion, promotionProgram.getFreeQty());
                    if (!promotionShopMapList.contains(promotionShopMapDTO))
                        promotionShopMapList.add(promotionShopMapDTO);
                }
            }
        }
    }

    @Override
    public Response<List<ZmFreeItemDTO>> getFreeItems(List<OrderDetailDTO> productList) {
        List<PromotionSaleProduct> totalListSaleProduct = new ArrayList<>();
        List<ZmFreeItemDTO> freeItemList = new ArrayList<>();

        for (OrderDetailDTO product : productList) {
            List<PromotionSaleProduct> saleProductList = promotionClient.getZmPromotion(product.getProductId()).getData();
            if (saleProductList != null)
                for (PromotionSaleProduct saleProduct : saleProductList) {
                    if (product.getQuantity() >= saleProduct.getQuantity())
                        totalListSaleProduct.add(saleProduct);
                }
        }
        for (PromotionSaleProduct saleProduct : totalListSaleProduct) {
            List<PromotionProductOpen> productOpenList = promotionClient.getFreeItem(saleProduct.getPromotionProgramId()).getData();
            freeItemList.addAll(convertProductOpenToFreeItemDTO(productOpenList));
        }
        return new Response<List<ZmFreeItemDTO>>().withData(freeItemList);
    }

    public List<ZmFreeItemDTO> convertProductOpenToFreeItemDTO(List<PromotionProductOpen> productOpens) {
        List<ZmFreeItemDTO> response = new ArrayList<>();

        for (PromotionProductOpen productOpen : productOpens) {
            PromotionProgram promotionProgram = getPromotionProgramById(productOpen.getPromotionProgramId());
            Product product = productRepository.findByIdAndDeletedAtIsNull(productOpen.getProductId());

            ZmFreeItemDTO freeItem = modelMapper.map(promotionProgram, ZmFreeItemDTO.class);
            freeItem.setPromotionId(promotionProgram.getId());
            freeItem.setProductId(product.getId());
            freeItem.setProductCode(product.getProductCode());
            freeItem.setProductName(product.getProductName());
            freeItem.setPromotionId(product.getId());
            freeItem.setPromotionQuantity(productOpen.getQuantity().intValue());

            response.add(freeItem);
        }
        return response;
    }

    public void setAutoPromotionFreeItemToSaleOrder(Long saleOrderId, Long shopId, PromotionProgramDetail programDetail) {
        SaleOrderDetail orderDetail = new SaleOrderDetail();

        orderDetail.setSaleOrderId(saleOrderId);
        orderDetail.setIsFreeItem(true);
        orderDetail.setProductId(programDetail.getProductId());
        orderDetail.setQuantity(programDetail.getFreeQty());
        orderDetail.setOrderDate(time);
        orderDetail.setShopId(shopId);

        saleOrderDetailRepository.save(orderDetail);
    }

    public void setZmPromotionFreeItemToSaleOrder(List<ZmFreeItemDTO> freeItemList, Long saleOrderId, Long shopId) {
        for (ZmFreeItemDTO freeItem : freeItemList) {
            SaleOrderDetail orderDetail = new SaleOrderDetail();

            orderDetail.setSaleOrderId(saleOrderId);
            orderDetail.setIsFreeItem(true);
            orderDetail.setProductId(freeItem.getProductId());
            orderDetail.setQuantity(freeItem.getPromotionQuantity());
            orderDetail.setOrderDate(time);
            orderDetail.setShopId(shopId);

            saleOrderDetailRepository.save(orderDetail);
        }
    }

    public PromotionProgram getPromotionProgramById(Long id) {
        return promotionClient.getById(id).getData() == null ? null : promotionClient.getById(id).getData();
    }

    public List<Long> getListPromotionProgramId(Long shopId) {
        List<Long> ids = new ArrayList<>();

        List<PromotionCustATTR> programList = promotionClient.getGroupCustomerMatchProgram(shopId).getData();
        if (programList.size() > 0)
            for (PromotionCustATTR program : programList)
                ids.add(program.getPromotionProgramId());
        return ids;
    }

    public void setSaleOrderPromotion(SaleOrderDetail saleOrderDetail, float autoPromotion, float zmPromotion,
                                      PromotionProgramDetail promotionProgram) {
        PromotionProgram promotion = getPromotionProgramById(promotionProgram.getPromotionProgramId());

        saleOrderDetail.setAutoPromotion(autoPromotion);
        saleOrderDetail.setAutoPromotionVat(1F);
        saleOrderDetail.setAutoPromotionNotVat(1F);

        saleOrderDetail.setPromotionCode(promotion.getPromotionProgramCode());
        saleOrderDetail.setPromotionName(promotion.getPromotionProgramName());

        saleOrderDetail.setZmPromotion(zmPromotion);
        saleOrderDetail.setZmPromotionVat(1F);
        saleOrderDetail.setZmPromotionNotVat(1F);

        saleOrderDetailRepository.save(saleOrderDetail);
    }

    public void setSaleOrderDetailRejected(Long saleOrderId) {
        List<SaleOrderDetail> saleOrderDetails = saleOrderDetailRepository.getBySaleOrderId(saleOrderId);
        for (SaleOrderDetail detail : saleOrderDetails) {
            detail.setAutoPromotion((float) 0);
            detail.setAutoPromotionVat((float) 0);
            detail.setAutoPromotionNotVat((float) 0);
            detail.setTotal(detail.getAmount());

            detail.setPromotionCode(null);
            detail.setPromotionName(null);

            saleOrderDetailRepository.save(detail);
        }
    }

//    public List<ZmFreeItemDTO> getPromotionDiscount(List<PromotionProgramDiscount> programDiscounts) {
//        List<ZmFreeItemDTO> znPromotionList = new ArrayList<>();
//        for (PromotionProgramDiscount discount : programDiscounts) {
//            ZmFreeItemDTO zmFreeItemDTO = new ZmFreeItemDTO();
//            if (discount.getType() == 2) {
//                zmFreeItemDTO.setDiscountCode(discount.getDiscountCode());
//                zmFreeItemDTO.setDiscountAmount(discount.getDiscountAmount());
//            }
//        }
//        return null;
//    }

    public void setChangePromotionShopMap() {
        for (PromotionShopMapDTO promotionShopMapDTO : this.promotionShopMapList) {
            int quantity = promotionShopMapDTO.getQuantity() == null ? 0 : promotionShopMapDTO.getQuantity();

            promotionClient.saveChangePromotionShopMap(promotionShopMapDTO.getPromotionShopMap(),
                    promotionShopMapDTO.getAmount(), quantity);
        }
    }
}

