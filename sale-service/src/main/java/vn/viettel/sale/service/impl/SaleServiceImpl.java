package vn.viettel.sale.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.UserDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.promotion.*;
import vn.viettel.core.dto.voucher.VoucherDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.entities.*;
import vn.viettel.sale.messaging.ProductOrderRequest;
import vn.viettel.sale.messaging.SaleOrderRequest;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.SaleService;
import vn.viettel.sale.service.dto.AutoPromotionDTO;
import vn.viettel.sale.service.dto.ShopMapDTO;
import vn.viettel.sale.service.dto.ZmFreeItemDTO;
import vn.viettel.sale.service.feign.*;

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
    ProductPriceRepository priceRepository;
    @Autowired
    ComboProductRepository comboProductRepository;
    @Autowired
    ComboProductDetailRepository comboDetailRepository;
    @Autowired
    SaleOrderComboDetailRepository orderComboDetailRepository;
    @Autowired
    SaleOrderDetailRepository saleOrderDetailRepository;
    @Autowired
    OnlineOrderRepository onlineOrderRepo;
    @Autowired
    OnlineOrderDetailRepository onlineOrderDetailRepo;
    @Autowired
    CustomerClient customerClient;
    @Autowired
    CustomerTypeClient customerTypeClient;
    @Autowired
    UserClient userClient;
    @Autowired
    PromotionClient promotionClient;
    @Autowired
    ShopClient shopClient;

    private static final float VAT = (float) 0.1;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Response<SaleOrder> createSaleOrder(SaleOrderRequest request, long userId, long roleId, long shopId) {
        Response<SaleOrder> response = new Response<>();

        Date date = new Date();
        Timestamp time = new Timestamp(date.getTime());

        CustomerDTO customer = customerClient.getCustomerByIdV1(request.getCustomerId()).getData();
        if (customer == null)
            throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
        UserDTO user = userClient.getUserByIdV1(userId);
        if (user == null)
            throw new ValidateException(ResponseMessage.USER_DOES_NOT_EXISTS);
        ShopDTO shop = shopClient.getByIdV1(shopId).getData();

        // check entity exist
        if (shopClient.getByIdV1(request.getShopId()).getData() == null)
            throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
        if (SaleOrderType.getValueOf(request.getOrderType()) == null)
            throw new ValidateException(ResponseMessage.SALE_ORDER_TYPE_NOT_EXIST);
        if (request.getFromSaleOrderId() != null && !repository.existsById(request.getFromSaleOrderId()))
            throw new ValidateException(ResponseMessage.SALE_ORDER_TYPE_NOT_EXIST);

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        SaleOrder saleOrder = modelMapper.map(request, SaleOrder.class);

        // Online order
        if (request.getOrderOnlineId() != null && !editableOnlineOrder(request, shopId))
            throw new ValidateException(ResponseMessage.EDITABLE_ONLINE_ORDER_NOT_ALLOW);

        if (request.getOrderOnlineId() == null && request.getOnlineNumber() != null
                && !shopClient.isManuallyCreatableOnlineOrderV1(shopId).getData())
            throw new ValidateException(ResponseMessage.MANUALLY_CREATABLE_ONLINE_ORDER_NOT_ALLOW);


        OnlineOrder onlineOrder = new OnlineOrder();
        if (request.getOrderOnlineId() != null) {
            onlineOrder = onlineOrderRepo.findById(request.getOrderOnlineId())
                    .orElseThrow(() -> new ValidateException(ResponseMessage.ORDER_ONLINE_NOT_FOUND));
            if (onlineOrder.getSynStatus() == 1)
                throw new ValidateException(ResponseMessage.SALE_ORDER_ALREADY_CREATED);
            saleOrder.setOrderNumber(onlineOrder.getOrderNumber());
        }

        saleOrder.setOrderDate(time);
        saleOrder.setCreatedAt(time);
        // save sale order to get sale order id
        repository.save(saleOrder);
        if(onlineOrder != null) {
            onlineOrder.setSynStatus(1);
            onlineOrder.setSaleOrderId(saleOrder.getId());
            onlineOrder.setOrderDate(saleOrder.getOrderDate());
            onlineOrderRepo.save(onlineOrder);
        }

        float productDiscount = 0;
        float totalPromotion = 0; // needed calculate
        float autoPromotion = 0;
        float zmPromotion = 0;
        float amount = 0;
        float voucherDiscount = 0;
        boolean isProductRejected = false;

        VoucherDTO voucher = null;
        if (request.getVoucherId() != null)
            voucher = promotionClient.getVouchersV1(request.getVoucherId()).getData();
        if (voucher != null) {
            voucher.setOrderShopCode(shop.getShopCode());
            voucher.setSaleOrderId(saleOrder.getId());
            voucher.setOrderNumber(saleOrder.getOrderNumber());
            setVoucherInUsed(voucher, saleOrder.getId(), user.getUserAccount());
            voucherDiscount = voucher.getPrice();
            saleOrder.setTotalVoucher(voucher.getPrice());
            saleOrder.setDiscountCodeAmount(voucher.getPrice());
        }
        List<ProductOrderRequest> orderDetailDTOList = request.getProducts();

        // get list available promotion program id
        List<Long> promotionProgramIds = getListPromotionProgramId(request.getShopId());
        List<PromotionProgramProductDTO> rejectedProducts = promotionClient.getRejectProductV1(promotionProgramIds).getData();
        // for each product in bill
        if (!rejectedProducts.isEmpty()) {
            // for each rejected item -> if 1 product is in rejected list -> no promotion for the bil
            for (ProductOrderRequest detail : orderDetailDTOList)
                if (rejectedProducts.stream().anyMatch(reject -> reject.getProductId().equals(detail.getProductId())))
                    isProductRejected = true;
        }

        for (ProductOrderRequest detail : orderDetailDTOList) {
            if (!productRepository.existsById(detail.getProductId()))
                throw new ValidateException(ResponseMessage.PRODUCT_NOT_FOUND);
            Product product = productRepository.getById(detail.getProductId());

            Price productPrice = priceRepository.getProductPrice(detail.getProductId(), customer.getCustomerTypeId());
            if (productPrice == null)
                throw new ValidateException(ResponseMessage.NO_PRICE_APPLIED);

            // get auto promotion
            AutoPromotionDTO promotionResult = null;
            if (!isProductRejected) {
                promotionResult = getPromotion(detail, productPrice.getPrice(), request.getShopId());
                productDiscount += promotionResult.getDiscountAmount();
                List<ZmFreeItemDTO> freeItems = promotionResult.getFreeItems();
                for (ZmFreeItemDTO freeProduct : freeItems) {
                    setAutoPromotionFreeItemToSaleOrder(saleOrder.getId(), shopId, freeProduct);
                }
            }

            if (product.getIsCombo() != null && product.getIsCombo()) {
                ComboProduct combo = comboProductRepository.getById(product.getComboProductId());
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
                orderDetail.setAutoPromotion(productDiscount);

                setDetailCreatedInfo(orderDetail, saleOrder.getId(), user.getUserAccount(),
                        productPrice.getPrice(), detail.getQuantity(), request.getShopId());

                detailRepository.save(orderDetail);

                if (!isProductRejected) {
                    for (PromotionProgramDetailDTO promotionProgram : promotionResult.getListPromotion()) {
                        if (promotionProgram.getProductId() == product.getId()) {
                            float discountAmount = promotionProgram.getDiscAmt() == null ? 0 : promotionProgram.getDiscAmt();
                            float zmDiscount = detail.getZmPromotion() == null ? 0 : detail.getZmPromotion();
                            setSaleOrderPromotion(orderDetail, discountAmount, zmDiscount, promotionProgram);
                        }
                    }
                }
            }
            if (detail.getZmPromotion() != null)
                zmPromotion += detail.getZmPromotion();

            amount += productPrice.getPrice() * detail.getQuantity();
        }
        // if no product is rejected -> accept auto promotion
        if (!isProductRejected)
            autoPromotion += productDiscount;
            // else set auto promotion for detail order to 0
        else
            setSaleOrderDetailRejected(saleOrder.getId());

        totalPromotion += voucherDiscount;
        totalPromotion += autoPromotion;
        totalPromotion += zmPromotion;

        setSaleOrderCreatedInfo(saleOrder, user.getUserAccount(), request.getTotalPaid(),
                totalPromotion, amount, autoPromotion, zmPromotion);

        repository.save(saleOrder);
        if (request.getFreeItemList() != null)
            setZmPromotionFreeItemToSaleOrder(request.getFreeItemList(), saleOrder.getId(), saleOrder.getShopId());

        return response.withData(saleOrder);
    }

    private boolean editableOnlineOrder(SaleOrderRequest request, Long shopId) {
        boolean isEditable = shopClient.isEditableOnlineOrderV1(shopId).getData();
        if (!isEditable) {
            List<OnlineOrderDetail> onlineDetails = onlineOrderDetailRepo.findByOnlineOrderId(request.getOrderOnlineId());
            if (onlineDetails.size() == request.getProducts().size()) {
                for (OnlineOrderDetail productOld : onlineDetails) {
                    boolean productExits = false;
                    for (ProductOrderRequest productOrder : request.getProducts()) {
                        if (productOld.getSku().equals(productOrder.getProductCode())) {
                            productExits = true;
                            if (!productOld.getQuantity().equals(productOrder.getQuantity())) return false;
                        }
                    }
                    if (!productExits) return false;
                }
                return true;
            }
            return false;
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public void stockOut(StockTotal stockTotal, int quantity) {
        stockTotal.setQuantity(stockTotal.getQuantity() - quantity);
        stockTotalRepository.save(stockTotal);
    }

    @Transactional(rollbackFor = Exception.class)
    public void stockOutCombo(Long wareHouseTypeId, ComboProduct combo) {
        List<ComboProductDetail> comboDetails = comboDetailRepository.findByComboProductIdAndStatus(combo.getId(), 1);

        for (ComboProductDetail detail : comboDetails) {
            StockTotal stockTotal = getStockTotal(detail.getProductId(), wareHouseTypeId);
            int quantity = detail.getFactor();
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
        Date date = new Date();
        Timestamp time = new Timestamp(date.getTime());

        orderDetail.setOrderDate(time);
        orderDetail.setCreatedAt(time);
        orderDetail.setCreateUser(username);
        orderDetail.setCreatedAt(time);
        orderDetail.setSaleOrderId(saleOrderId);
        orderDetail.setPrice(price);
        orderDetail.setAmount(quantity * price);
        orderDetail.setTotal(quantity * price - orderDetail.getAutoPromotion());
        orderDetail.setPriceNotVat(price - price * VAT);
        orderDetail.setShopId(shopId);
    }

    public void setComboDetailCreatedInfo(SaleOrderComboDetail orderComboDetail, Long saleOrderId, String username, float price) {
        Date date = new Date();
        Timestamp time = new Timestamp(date.getTime());

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
        saleOrder.setCreateUser(username);
        saleOrder.setAmount(amount);
        saleOrder.setTotalPromotion(totalPromotion); // total money discount
        if (amount - totalPromotion < 0)
            saleOrder.setTotal(new Float(0));
        else
            saleOrder.setTotal(amount - totalPromotion);
        // total payment of the bill
        saleOrder.setBalance(totalPaid - (amount - totalPromotion)); // change money
        saleOrder.setAutoPromotion(autoPromotion);
        saleOrder.setZmPromotion(zmPromotion);
    }

    // call api from promotion service to set and save
    public void setVoucherInUsed(VoucherDTO voucher, Long saleOrderId, String userAccount) {
        Date date = new Date();
        Timestamp time = new Timestamp(date.getTime());

        voucher.setIsUsed(true);
        voucher.setSaleOrderId(saleOrderId);
        voucher.setOrderDate(time);
        voucher.setUpdatedAt(time);
        voucher.setUpdateUser(userAccount);

        try {
            promotionClient.updateVoucher(voucher);
        } catch (Exception e) {
            throw new ValidateException(ResponseMessage.UPDATE_VOUCHER_FAIL);
        }
    }

//    public boolean isCustomerMatchProgram(Long shopId, Customer customer, Long orderType) {
//        List<PromotionCustATTR> promotionCusATTRS = promotionClient.getGroupCustomerMatchProgram(shopId).getData();
//        if (promotionCusATTRS == null)
//            return false;
//        for (PromotionCustATTR cusATTR : promotionCusATTRS) {
//            int type = cusATTR.getObjectType();
//            switch (type) {
//                case 2:
//                    Customer cus = customerClient.getByIdAndType(customer.getId(), cusATTR.getObjectId()).getData();
//                    if (cus != null)
//                        return true;
//                    break;
//                case 20:
//                    if (cusATTR.getObjectId() == orderType)
//                        return true;
//                    break;
//            }
//        }
//        return false;
//    }

    public AutoPromotionDTO getPromotion(ProductOrderRequest detail, float price, Long shopId) {
        AutoPromotionDTO result = new AutoPromotionDTO();
        float discount = 0;
        List<PromotionProgramDetailDTO> programDetails = promotionClient.getPromotionDetailByPromotionIdV1(shopId).getData();
        List<ShopMapDTO> promotionShopMapList = new ArrayList<>();

        // for each promotion program detail -> if product is in promotion list and match condition -> discount
        for (PromotionProgramDetailDTO promotionProgram : programDetails) {
            if (detail.getProductId() == promotionProgram.getProductId()) {
                // if sale quantity or sale amount match promotion requirement
                if ((promotionProgram.getSaleQty() != null && detail.getQuantity() >= promotionProgram.getSaleQty())
                        || (promotionProgram.getSaleAmt() != null && detail.getQuantity() * price >= promotionProgram.getSaleAmt())) {

                    PromotionShopMapDTO promotionShopMap = promotionClient.getPromotionShopMapV1(
                            promotionProgram.getPromotionProgramId(), shopId).getData();

                    // discount amount
                    if (promotionProgram.getDiscAmt() != null)
                        discount += promotionProgram.getDiscAmt();
                    // discount percent
                    if (promotionProgram.getDisPer() != null)
                        discount += (detail.getQuantity() * price) * promotionProgram.getDisPer();
                    // give free item
                    if (promotionProgram.getFreeProductId() != null) {
                        PromotionProgramDTO promotionProgramDTO = getPromotionProgramById(promotionProgram.getPromotionProgramId());

                        ZmFreeItemDTO freeItem = new ZmFreeItemDTO();
                        freeItem.setPromotionId(promotionProgram.getPromotionProgramId());
                        freeItem.setProductId(promotionProgram.getProductId());
                        freeItem.setPromotionQuantity(promotionProgram.getFreeQty());
                        freeItem.setPromotionProgramName(promotionProgramDTO.getPromotionProgramName());
                        freeItem.setPromotionProgramCode(promotionProgramDTO.getPromotionProgramCode());
                        freeItem.setIsAuto(true);

                        StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(detail.getProductId(), customerTypeClient.getWarehouseTypeIdByCustomer(shopId).getData());
                        freeItem.setStockQuantity(stockTotal.getQuantity());

                        result.getFreeItems().add(freeItem);
                    }

                    result.getListPromotion().add(promotionProgram);

                    // add to list promotion shop map for change data
                    ShopMapDTO promotionShopMapDTO = new ShopMapDTO(promotionShopMap,
                            discount, promotionProgram.getFreeQty());
                    if (!promotionShopMapList.contains(promotionShopMapDTO))
                        promotionShopMapList.add(promotionShopMapDTO);
                }
            }
        }
        setChangePromotionShopMap(promotionShopMapList);

        result.setDiscountAmount(discount);
        return result;
    }

    @Override
    public Response<List<ZmFreeItemDTO>> getFreeItems(List<ProductOrderRequest> productList, Long shopId, Long saleOrderId) {
        List<PromotionSaleProductDTO> totalListSaleProduct = new ArrayList<>();
        List<ZmFreeItemDTO> freeItemList = new ArrayList<>();

        freeItemList.addAll(convertOrderDetailToFreeItemDTO(productList, shopId));
        for (ProductOrderRequest product : productList) {
            List<PromotionSaleProductDTO> saleProductList = promotionClient.getZmPromotionV1(product.getProductId()).getData();
            if (saleProductList != null)
                for (PromotionSaleProductDTO saleProduct : saleProductList) {
                    if (product.getQuantity() >= saleProduct.getQuantity())
                        totalListSaleProduct.add(saleProduct);
                }
        }
        for (PromotionSaleProductDTO saleProduct : totalListSaleProduct) {
            List<PromotionProductOpenDTO> productOpenList = promotionClient.getFreeItemV1(saleProduct.getPromotionProgramId()).getData();
            freeItemList.addAll(convertProductOpenToFreeItemDTO(productOpenList, shopId));
        }
        return new Response<List<ZmFreeItemDTO>>().withData(freeItemList);
    }

    public List<ZmFreeItemDTO> convertProductOpenToFreeItemDTO(List<PromotionProductOpenDTO> productOpens, Long shopId) {
        List<ZmFreeItemDTO> response = new ArrayList<>();

        for (PromotionProductOpenDTO productOpen : productOpens) {
            PromotionProgramDTO promotionProgram = getPromotionProgramById(productOpen.getPromotionProgramId());
            Product product = productRepository.getById(productOpen.getProductId());

            ZmFreeItemDTO freeItem = modelMapper.map(promotionProgram, ZmFreeItemDTO.class);
            freeItem.setPromotionId(promotionProgram.getId());
            freeItem.setProductId(product.getId());
            freeItem.setProductCode(product.getProductCode());
            freeItem.setProductName(product.getProductName());
            freeItem.setPromotionQuantity(productOpen.getQuantity());
            freeItem.setIsZm(true);
            response.add(freeItem);

            StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(product.getId(), customerTypeClient.getWarehouseTypeIdByCustomer(shopId).getData());
            freeItem.setStockQuantity(stockTotal.getQuantity());
        }
        return response;
    }

    public List<ZmFreeItemDTO> convertOrderDetailToFreeItemDTO(List<ProductOrderRequest> orderDetails, Long shopId) {
        List<ZmFreeItemDTO> response = new ArrayList<>();

        for (ProductOrderRequest product : orderDetails) {
            List<ZmFreeItemDTO> freeItems = getPromotion(product, 0, shopId).getFreeItems();
            if (!freeItems.isEmpty())
                response.addAll(freeItems);
        }
        return response;
    }

    public void setAutoPromotionFreeItemToSaleOrder(Long saleOrderId, Long shopId, ZmFreeItemDTO programDetail) {
        Date date = new Date();
        Timestamp time = new Timestamp(date.getTime());

        SaleOrderDetail orderDetail = new SaleOrderDetail();
        orderDetail.setSaleOrderId(saleOrderId);
        orderDetail.setIsFreeItem(true);
        orderDetail.setProductId(programDetail.getProductId());
        orderDetail.setQuantity(programDetail.getPromotionQuantity());
        orderDetail.setOrderDate(time);
        orderDetail.setShopId(shopId);

        saleOrderDetailRepository.save(orderDetail);
    }

    public void setZmPromotionFreeItemToSaleOrder(List<ZmFreeItemDTO> freeItemList, Long saleOrderId, Long shopId) {
        Date date = new Date();
        Timestamp time = new Timestamp(date.getTime());

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

    public PromotionProgramDTO getPromotionProgramById(Long id) {
        return promotionClient.getByIdV1(id).getData() == null ? null : promotionClient.getByIdV1(id).getData();
    }

    public List<Long> getListPromotionProgramId(Long shopId) {
        List<Long> ids = new ArrayList<>();

        List<PromotionCustATTRDTO> programList = promotionClient.getGroupCustomerMatchProgramV1(shopId).getData();
        if (!programList.isEmpty())
            for (PromotionCustATTRDTO program : programList)
                ids.add(program.getPromotionProgramId());
        return ids;
    }

    public void setSaleOrderPromotion(SaleOrderDetail saleOrderDetail, float autoPromotion, float zmPromotion,
                                      PromotionProgramDetailDTO promotionProgram) {
        PromotionProgramDTO promotion = getPromotionProgramById(promotionProgram.getPromotionProgramId());

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

    public void setChangePromotionShopMap(List<ShopMapDTO> promotionShopMapList) {
        for (ShopMapDTO promotionShopMapDTO : promotionShopMapList) {
            int quantity = promotionShopMapDTO.getQuantity() == null ? 0 : promotionShopMapDTO.getQuantity();

            promotionClient.saveChangePromotionShopMapV1(promotionShopMapDTO.getPromotionShopMap(),
                    promotionShopMapDTO.getAmount(), quantity);
        }
    }
}

