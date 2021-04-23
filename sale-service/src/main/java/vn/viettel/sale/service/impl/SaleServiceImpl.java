package vn.viettel.sale.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.UserDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.voucher.VoucherDTO;
import vn.viettel.core.dto.promotion.*;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.service.dto.PermissionDTO;
import vn.viettel.sale.entities.*;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.SaleService;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.service.dto.ShopMapDTO;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.service.feign.PromotionClient;
import vn.viettel.sale.service.feign.ShopClient;
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
    ProductPriceRepository priceRepository;
    @Autowired
    ComboProductRepository comboProductRepository;
    @Autowired
    ComboDetailRepository comboDetailRepository;
    @Autowired
    SaleOrderComboDetailRepository orderComboDetailRepository;
    @Autowired
    SaleOrderDetailRepository saleOrderDetailRepository;
    @Autowired
    OnlineOrderRepository orderOnlineRepository;
    @Autowired
    CustomerClient customerClient;
    @Autowired
    UserClient userClient;
    @Autowired
    PromotionClient promotionClient;
    @Autowired
    ShopClient shopClient;

    private static final float VAT = (float) 0.1;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Response<SaleOrder> createSaleOrder(SaleOrderRequest request, long userId, long roleId, long shopId, long formId, long ctrlId) {
        Response<SaleOrder> response = new Response<>();

        Date date = new Date();
        Timestamp time = new Timestamp(date.getTime());

        if (request.getShopId() != shopId)
            return response.withError(ResponseMessage.USER_HAVE_NO_PRIVILEGE_ON_THIS_SHOP);
        List<PermissionDTO> permissionList = userClient.getUserPermission(roleId);
        if (!checkUserPermission(permissionList, formId, ctrlId))
            return response.withError(ResponseMessage.NO_FUNCTIONAL_PERMISSION);

        CustomerDTO customer = customerClient.getCustomerById(request.getCustomerId()).getData();
        if (customer == null)
            throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
        UserDTO user = userClient.getUserById(userId);
        if (user == null)
            throw new ValidateException(ResponseMessage.USER_DOES_NOT_EXISTS);

        // check entity exist
        if (shopClient.getById(request.getShopId()).getData() == null)
            throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
        if (SaleOrderType.getValueOf(request.getOrderType()) == null)
            throw new ValidateException(ResponseMessage.SALE_ORDER_TYPE_NOT_EXIST);
        if (request.getFromSaleOrderId() != null && !repository.existsByIdAndDeletedAtIsNull(request.getFromSaleOrderId()))
            throw new ValidateException(ResponseMessage.SALE_ORDER_TYPE_NOT_EXIST);
        if (request.getOrderOnlineId() != null && !orderOnlineRepository.findById(request.getOrderOnlineId()).isPresent())
            throw new ValidateException(ResponseMessage.ORDER_ONLINE_NOT_FOUND);

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        SaleOrder saleOrder = modelMapper.map(request, SaleOrder.class);

        // Online order
        if (request.getOrderOnlineId() != null) {
            OnlineOrder onlineOrder = orderOnlineRepository.findById(request.getOrderOnlineId())
                    .orElseThrow(() -> new ValidateException(ResponseMessage.ORDER_ONLINE_NOT_FOUND));
            if (onlineOrder.getSynStatus() == 1)
                throw new ValidateException(ResponseMessage.SALE_ORDER_ALREADY_CREATED);
            onlineOrder.setSynStatus(1);
            orderOnlineRepository.save(onlineOrder);

            saleOrder.setOrderNumber(onlineOrder.getOrderNumber());
        }
        saleOrder.setOrderNumber("UNKNOWN FORMAT");
        saleOrder.setOrderDate(time);
        saleOrder.setCreatedAt(time);
        // save sale order to get sale order id
        repository.save(saleOrder);

        float productDiscount = 0;
        float totalPromotion = 0; // needed calculate
        float autoPromotion = 0;
        float zmPromotion = 0;
        float amount = 0;
        float voucherDiscount = 0;
        boolean isProductRejected = false;

        VoucherDTO voucher = null;
        if (request.getVoucherId() != null)
            voucher = promotionClient.getVouchers(request.getVoucherId()).getData();
        if (voucher != null) {
            setVoucherInUsed(voucher, saleOrder.getId());
            voucherDiscount = voucher.getPrice();
            saleOrder.setTotalVoucher(voucher.getPrice());
            saleOrder.setDiscountCodeAmount(voucher.getPrice());
        }
        List<OrderDetailDTO> orderDetailDTOList = request.getProducts();

        // get list available promotion program id
        List<Long> promotionProgramIds = getListPromotionProgramId(request.getShopId());
        List<PromotionProgramProductDTO> rejectedProducts = promotionClient.getRejectProduct(promotionProgramIds).getData();
        // for each product in bill
        if (!rejectedProducts.isEmpty()) {
            // for each rejected item -> if 1 product is in rejected list -> no promotion for the bil
            for (OrderDetailDTO detail : orderDetailDTOList)
                if (rejectedProducts.stream().anyMatch(reject -> reject.getProductId().equals(detail.getProductId())))
                    isProductRejected = true;
        }

        for (OrderDetailDTO detail : orderDetailDTOList) {
            if (!productRepository.existsByIdAndDeletedAtIsNull(detail.getProductId()))
                throw new ValidateException(ResponseMessage.PRODUCT_NOT_FOUND);
            Product product = productRepository.findByIdAndDeletedAtIsNull(detail.getProductId());

            Price productPrice = priceRepository.findByProductId(detail.getProductId());
            if (productPrice == null)
                throw new ValidateException(ResponseMessage.NO_PRICE_APPLIED);

            // get auto promotion
            if (!isProductRejected)
                productDiscount += getPromotion(detail, productPrice.getPrice(), promotionProgramIds, request.getShopId(), saleOrder.getId());

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
                orderDetail.setAutoPromotion(productDiscount);

                setDetailCreatedInfo(orderDetail, saleOrder.getId(), user.getUserAccount(),
                        productPrice.getPrice(), detail.getQuantity(), request.getShopId());

                detailRepository.save(orderDetail);
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
    public void setVoucherInUsed(VoucherDTO voucher, Long saleOrderId) {
        Date date = new Date();
        Timestamp time = new Timestamp(date.getTime());

        voucher.setIsUsed(true);
        voucher.setSaleOrderId(saleOrderId);
        voucher.setOrderDate(time);
    }

    @Override
    public Response<ShopDTO> getShopById(long id) {
        Response<ShopDTO> response = new Response<>();
        try {
            response.setData(shopClient.getById(id).getData());
        } catch (Exception e) {
            response.setFailure(ResponseMessage.SHOP_NOT_FOUND);
        }
        return response;
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

    public Float getPromotion(OrderDetailDTO detail, float price,
                              List<Long> promotionProgramIds,
                              Long shopId, Long saleOrderId) {
        float discount = 0;
        List<PromotionProgramDetailDTO> programDetails = promotionClient.getPromotionDetailByPromotionId(shopId).getData();
        List<ShopMapDTO> promotionShopMapList = new ArrayList<>();

        // for each promotion program detail -> if product is in promotion list and match condition -> discount
        for (PromotionProgramDetailDTO promotionProgram : programDetails) {
            if (detail.getProductId() == promotionProgram.getProductId()) {
                // if sale quantity or sale amount match promotion requirement
                if ((promotionProgram.getSaleQty() != null && detail.getQuantity() >= promotionProgram.getSaleQty())
                        || (promotionProgram.getSaleAmt() != null && detail.getQuantity() * price >= promotionProgram.getSaleAmt())) {

                    // get sale order detail to set data
                    SaleOrderDetail saleOrderDetail = saleOrderDetailRepository
                            .findByProductIdAndSaleOrderId(detail.getProductId(), saleOrderId);
                    // get promotion shop map to change data
                    PromotionShopMapDTO promotionShopMap = promotionClient.getPromotionShopMap(
                            promotionProgram.getPromotionProgramId(), shopId).getData();

                    // discount amount
                    if (promotionProgram.getDiscAmt() != null)
                        discount += promotionProgram.getDiscAmt();
                    // discount percent
                    if (promotionProgram.getDisPer() != null)
                        discount += (detail.getQuantity() * price) * promotionProgram.getDisPer();
                    // give free item
                    if (promotionProgram.getFreeProductId() != null)
                            setAutoPromotionFreeItemToSaleOrder(saleOrderId, shopId, promotionProgram);

                    if (saleOrderDetail != null)
                        setSaleOrderPromotion(saleOrderDetail, promotionProgram.getDiscAmt(),
                                detail.getZmPromotion(), promotionProgram);

                    // add to list promotion shop map for change data
                    ShopMapDTO promotionShopMapDTO = new ShopMapDTO(promotionShopMap,
                            discount, promotionProgram.getFreeQty());
                    if (!promotionShopMapList.contains(promotionShopMapDTO))
                        promotionShopMapList.add(promotionShopMapDTO);
                }
            }
        }

        setChangePromotionShopMap(promotionShopMapList);
        return discount;
    }

    @Override
    public Response<List<ZmFreeItemDTO>> getFreeItems(List<OrderDetailDTO> productList) {
        List<PromotionSaleProductDTO> totalListSaleProduct = new ArrayList<>();
        List<ZmFreeItemDTO> freeItemList = new ArrayList<>();

        for (OrderDetailDTO product : productList) {
            List<PromotionSaleProductDTO> saleProductList = promotionClient.getZmPromotion(product.getProductId()).getData();
            if (saleProductList != null)
                for (PromotionSaleProductDTO saleProduct : saleProductList) {
                    if (product.getQuantity() >= saleProduct.getQuantity())
                        totalListSaleProduct.add(saleProduct);
                }
        }
        for (PromotionSaleProductDTO saleProduct : totalListSaleProduct) {
            List<PromotionProductOpenDTO> productOpenList = promotionClient.getFreeItem(saleProduct.getPromotionProgramId()).getData();
            freeItemList.addAll(convertProductOpenToFreeItemDTO(productOpenList));
        }
        return new Response<List<ZmFreeItemDTO>>().withData(freeItemList);
    }

    public List<ZmFreeItemDTO> convertProductOpenToFreeItemDTO(List<PromotionProductOpenDTO> productOpens) {
        List<ZmFreeItemDTO> response = new ArrayList<>();

        for (PromotionProductOpenDTO productOpen : productOpens) {
            PromotionProgramDTO promotionProgram = getPromotionProgramById(productOpen.getPromotionProgramId());
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

    public void setAutoPromotionFreeItemToSaleOrder(Long saleOrderId, Long shopId, PromotionProgramDetailDTO programDetail) {
        Date date = new Date();
        Timestamp time = new Timestamp(date.getTime());

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
        return promotionClient.getById(id).getData() == null ? null : promotionClient.getById(id).getData();
    }

    public List<Long> getListPromotionProgramId(Long shopId) {
        List<Long> ids = new ArrayList<>();

        List<PromotionCustATTRDTO> programList = promotionClient.getGroupCustomerMatchProgram(shopId).getData();
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

            promotionClient.saveChangePromotionShopMap(promotionShopMapDTO.getPromotionShopMap(),
                    promotionShopMapDTO.getAmount(), quantity);
        }
    }
}

