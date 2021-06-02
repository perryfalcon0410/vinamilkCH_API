package vn.viettel.sale.service.impl;

import org.apache.commons.lang3.EnumUtils;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.core.dto.ShopDTO;
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
import vn.viettel.sale.service.OnlineOrderService;
import vn.viettel.sale.service.SaleService;
import vn.viettel.sale.service.dto.CoverOrderDetailDTO;
import vn.viettel.sale.service.dto.OrderDetailShopMapDTO;
import vn.viettel.sale.service.dto.ZmFreeItemDTO;
import vn.viettel.sale.service.enums.ProgramApplyDiscountPriceType;
import vn.viettel.sale.service.enums.PromotionDiscountOverTotalBill;
import vn.viettel.sale.service.enums.PromotionGroupProducts;
import vn.viettel.sale.service.enums.PromotionSetProducts;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.service.feign.CustomerTypeClient;
import vn.viettel.sale.service.feign.PromotionClient;
import vn.viettel.sale.service.feign.ShopClient;

import java.sql.Timestamp;
import java.util.*;

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
    PromotionClient promotionClient;
    @Autowired
    ShopClient shopClient;
    @Autowired
    OnlineOrderService onlineOrderService;

    private static final double VAT = 0.1;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Response<SaleOrder> createSaleOrder(SaleOrderRequest request, long userId, long roleId, long shopId) {
        Response<SaleOrder> response = new Response<>();

        Date date = new Date();
        Timestamp time = new Timestamp(date.getTime());

        CustomerDTO customer = customerClient.getCustomerByIdV1(request.getCustomerId()).getData();
        if (customer == null)
            throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
        ShopDTO shop = shopClient.getByIdV1(shopId).getData();

        // check entity exist
        if (shopClient.getByIdV1(shopId).getData() == null)
            throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
        if (SaleOrderType.getValueOf(request.getOrderType()) == null)
            throw new ValidateException(ResponseMessage.SALE_ORDER_TYPE_NOT_EXIST);

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        SaleOrder saleOrder = modelMapper.map(request, SaleOrder.class);

        // Online order
        OnlineOrder onlineOrder = null;
        if (request.getOrderOnlineId() != null || request.getOnlineNumber() != null )
            onlineOrder = this.checkOnlineOrder(saleOrder, request, shopId);

        saleOrder.setOrderDate(time);
        // save sale order to get sale order id
        repository.save(saleOrder);
        if (onlineOrder != null) {
            onlineOrder.setSynStatus(1);
            onlineOrder.setSaleOrderId(saleOrder.getId());
            onlineOrder.setOrderDate(saleOrder.getOrderDate());
            onlineOrderRepo.save(onlineOrder);
        }

        double totalPromotion = 0; // needed calculate
        double zmPromotion = 0;
        double voucherDiscount = 0;

        VoucherDTO voucher = null;
        if (request.getVoucherId() != null)
            voucher = promotionClient.getVouchersV1(request.getVoucherId()).getData();
        if (voucher != null) {
            voucher.setOrderShopCode(shop.getShopCode());
            voucher.setSaleOrderId(saleOrder.getId());
            voucher.setOrderNumber(saleOrder.getOrderNumber());
            setVoucherInUsed(voucher, saleOrder.getId());
            voucherDiscount = voucher.getPrice();
            saleOrder.setTotalVoucher(voucher.getPrice());
            saleOrder.setDiscountCodeAmount(voucher.getPrice());
        }
        List<ProductOrderRequest> orderDetailDTOList = request.getProducts();

        Long warehouseTypeId = customerTypeClient.getWarehouseTypeIdByCustomer(shopId).getData();
        // list promotion program detail
        List<PromotionProgramDetailDTO> programDetails = promotionClient.getPromotionDetailByPromotionIdV1(shopId).getData();

        CoverOrderDetailDTO coverOrderDetailDTO = createSaleOrderDetail(saleOrder, orderDetailDTOList, programDetails, shopId, customer.getCustomerTypeId(), warehouseTypeId);

        // for list promotion
        for (OrderDetailShopMapDTO promotion : coverOrderDetailDTO.getListPromotions()) {
            // get promotion shop map
            PromotionShopMapDTO promotionShopMap = promotionClient.getPromotionShopMapV1(promotion.getPromotionProgramId(), shopId).getData();
            /* 2 Set dùng để add những đơn hàng chi tiết đủ điều kiện KM
            -> nếu toàn bộ đơn hàng đủ điều kiện hưởng KM -> for từng orderDetail trong set và lưu xuống DB
            else -> for từng orderDetail trong set -> set KM về 0 và lưu xuống DB
             */
            Set<SaleOrderDetail> orderDetailPromotion = new HashSet<>();
            Set<SaleOrderComboDetail> comboOrderDetailPromotion = new HashSet<>();
            // biến tạm dùng để lưu tổng tiền + số lượng KM để tính số xuất -> dùng cho việc update table PromotionShopMap
            Double tempDiscount = 0D;

            // lấy chương trính khuyến mãi
            PromotionProgramDTO promotionProgram = promotionClient.getByIdV1(promotionShopMap.getPromotionProgramId()).getData();
            boolean isValidBuyingCondition;

            // nếu loại CTKM là KM theo nhóm sp (ZV07 -> ZV12)
            if (EnumUtils.isValidEnum(PromotionGroupProducts.class, promotionProgram.getType())) {
                // kiểm tra điều kiện mua hàng
                isValidBuyingCondition =
                        promotionClient.checkBuyingCondition(promotionProgram.getType(), coverOrderDetailDTO.getTotalQuantity(),
                                coverOrderDetailDTO.getTotalAmount(), coverOrderDetailDTO.getProductIds()) == 1 ? true : false;
            }
            // nếu loại CTKM là KM theo bộ sp (ZV13 -> ZV18): bắt buộc mua đủ tất cả sp trong bộ sp
            else if (EnumUtils.isValidEnum(PromotionSetProducts.class, promotionProgram.getType())) {
                // kiểm tra equal giữa 2 danh sách ids sản phẩm mua và ids sp trong bộ sp
                isValidBuyingCondition = promotionClient.getRequiredProducts(promotionProgram.getType())
                        .equals(coverOrderDetailDTO.getProductIds()) ? true : false;
            }
            // nếu loại CTKM là KM theo từng sp -> ko cần KT vì các điều kiện kiểm tra KM của từng sp đã đc thực hiện dưới hàm createSaleOrderDetail
            else
                isValidBuyingCondition = true;

            /* nếu chương trình khuyến mãi có tồn tại mapping với bảng PromotionShopMap và thỏa điều kiện mua
            -> tiến hành tính toán KM
             */
            if (promotionShopMap != null && isValidBuyingCondition) {
                for (SaleOrderDetail orderDetail : coverOrderDetailDTO.getListOrderDetail()) {
                    if (orderDetail.getPromotionCode() != null && orderDetail.getPromotionCode().equals(promotion.getPromotionProgramCode())) {
                        // if promotion is discount
                        if (orderDetail.getAutoPromotion() > 0) {
                            tempDiscount += orderDetail.getAutoPromotion();
                            promotionShopMap.setQuantityMax(promotionShopMap.getQuantityMax() - orderDetail.getAutoPromotion());
                            orderDetailPromotion.add(orderDetail);
                        }
                        // if promotion is free item
                        if (orderDetail.getIsFreeItem()) {
                            // nếu relation == 0 (KM tất cả sp) -> thêm sp KM vào đơn hàng + update promotionShopMap
                            // nếu relation == 1 (chọn 1 spkm) -> ko thêm ngay vào đơn hàng/update promotionShopMap -> sp đc chọn sẽ đc truyền về lại từ phía FE
                            if (promotion.getRelation() == 0) {
                                promotionShopMap.setQuantityMax(promotionShopMap.getQuantityMax() - orderDetail.getQuantity());
                                orderDetailPromotion.add(orderDetail);
                            }
                        }
                    }
                }

                for (SaleOrderComboDetail comboDetail : coverOrderDetailDTO.getListOrderComboDetail()) {
                    if (comboDetail.getPromotionCode() != null && comboDetail.getPromotionCode().equals(promotion.getPromotionProgramCode())) {
                        if (comboDetail.getAutoPromotion() > 0) {
                            tempDiscount += comboDetail.getAutoPromotion();
                            promotionShopMap.setQuantityMax(promotionShopMap.getQuantityMax() - comboDetail.getAutoPromotion().longValue());

                            comboOrderDetailPromotion.add(comboDetail);
                        }
                    }
                }

                // if quantity max and promotion time in day still available
                if (promotionShopMap.getQuantityMax() > 0
                        && checkPromotionNumInDay(saleOrder.getCustomerId(), promotionProgram.getPromotionProgramCode(), saleOrder.getId())) {

                    totalPromotion += saleOrder.getAutoPromotion();
                    totalPromotion += voucherDiscount;
                    totalPromotion += zmPromotion;

                    saleOrder.setTotalPromotion(totalPromotion);

                    // case ZV19 -> ZV21
                    if (EnumUtils.isValidEnum(PromotionDiscountOverTotalBill.class, promotionProgram.getType())) {
                        double promotionDiscount = 0;
                        Double percent;
                        percent = promotionClient.getDiscountPercent(promotionProgram.getType(), promotionProgram.getPromotionProgramCode(), saleOrder.getAmount());
                        percent = percent == null ? 0 : percent;

                        if (promotionProgram.getAmountOrderType() == 1)
                            promotionDiscount = (saleOrder.getAmount() - saleOrder.getTotalPromotion()) * percent;

                        else if (promotionProgram.getAmountOrderType() == 2) {
                            promotionDiscount = (saleOrder.getAmount() - saleOrder.getTotalPromotion() - zmPromotion) * percent;
                        }
                        saleOrder.setAutoPromotion(promotionDiscount);
                    }

                    promotionClient.saveChangePromotionShopMapV1(promotion.getPromotionProgramId(), shopId, promotionShopMap.getQuantityMax());

                    setSaleOrderCreatedInfo(saleOrder, request.getTotalPaid(), zmPromotion);
                    repository.save(saleOrder);
                    for (SaleOrderDetail orderDetail : orderDetailPromotion)
                        saleOrderDetailRepository.save(orderDetail);
                    for (SaleOrderComboDetail comboDetail : comboOrderDetailPromotion)
                        orderComboDetailRepository.save(comboDetail);
                } else {
                    saleOrder.setAutoPromotion(saleOrder.getAutoPromotion() - tempDiscount);
                    repository.save(saleOrder);
                    for (SaleOrderDetail orderDetail : orderDetailPromotion) {
                        orderDetail.setAutoPromotion(0D);
                        orderDetail.setPromotionCode(null);
                        orderDetail.setPromotionName(null);
                        saleOrderDetailRepository.save(orderDetail);
                    }
                    for (SaleOrderComboDetail comboDetail : comboOrderDetailPromotion) {
                        comboDetail.setAutoPromotion(0D);
                        comboDetail.setPromotionCode(null);
                        comboDetail.setPromotionName(null);
                        orderComboDetailRepository.save(comboDetail);
                    }
                }
            }
        }
        // set zm promotion free item as sale order detail
        if (request.getFreeItemList() != null) {
            // danh sách sản phẩm KM đc truyền về từ FE
            // ghi chú: vào hàm convertFreItemToOrderDetail() để thêm các CTKM giảm trừ tiền vào đơn hàng
            for (ZmFreeItemDTO freeItemDTO : request.getFreeItemList()) {
                saleOrderDetailRepository.save(convertFreItemToOrderDetail(freeItemDTO));
            }
        }
        repository.save(saleOrder);
        if (request.getFreeItemList() != null)
            setZmPromotionFreeItemToSaleOrder(request.getFreeItemList(), saleOrder.getId(), saleOrder.getShopId());

        return response.withData(saleOrder);
    }

    private CoverOrderDetailDTO createSaleOrderDetail(SaleOrder saleOrder, List<ProductOrderRequest> products, List<PromotionProgramDetailDTO> programDetails, Long shopId, Long customerTypeId, Long warehouseTypeId) {
        List<SaleOrderDetail> listOrderDetail = new ArrayList<>();
        List<SaleOrderComboDetail> listOrderComboDetail = new ArrayList<>();
        List<OrderDetailShopMapDTO> listPromotions = new ArrayList<>();
        List<Long> productIds = new ArrayList<>();

        int totalQuantity = 0;
        double totalAmount = 0;
        for (ProductOrderRequest detail : products) {

            if (!productRepository.existsById(detail.getProductId()))
                throw new ValidateException(ResponseMessage.PRODUCT_NOT_FOUND);
            Product product = productRepository.getById(detail.getProductId());

            Price productPrice = priceRepository.getProductPrice(detail.getProductId(), customerTypeId);
            if (productPrice == null)
                throw new ValidateException(ResponseMessage.NO_PRICE_APPLIED);

            // lấy danh sách id sản phẩm + tổng lượng mua + tổng tiền mua để kiểm tra điều kiện khuyến mãi (dùng trong hàm checkBuyingCondition của promotionClient)
            productIds.add(detail.getProductId());
            if (detail.getQuantity() != null) {
                totalQuantity += detail.getQuantity();
                totalAmount += detail.getQuantity()*productPrice.getPrice();
            }
            // kết thúc lấy danh sách id sản phẩm + tổng lượng mua + tổng tiền mua

            if (product.getIsCombo() != null && product.getIsCombo()) {
                ComboProduct combo = comboProductRepository.getById(product.getComboProductId());
                stockOutCombo(warehouseTypeId, combo);

                SaleOrderComboDetail orderComboDetail = modelMapper.map(detail, SaleOrderComboDetail.class);
                orderComboDetail.setComboProductId(combo.getId());
                orderComboDetail.setComboQuantity(combo.getNumProduct());
                setComboDetailCreatedInfo(orderComboDetail, saleOrder.getId(), productPrice.getPrice());

                double comboAutoPromotion = 0;
                for (ProductOrderRequest productInCombo : convertComboToProducts(combo)) {
                    OrderDetailShopMapDTO promotionInfo = getPromotions(programDetails, saleOrder, null, orderComboDetail, productInCombo, productPrice, shopId, listOrderDetail);
                    comboAutoPromotion += promotionInfo.getDiscount();

                    if (promotionInfo.getDiscount() > 0 || promotionInfo.getPromotionProgramId() != null)
                        listPromotions.add(promotionInfo);
                }
                orderComboDetail.setAutoPromotion(comboAutoPromotion);
                saleOrder.setAutomatePromotion(comboAutoPromotion);

                listOrderComboDetail.add(orderComboDetail);

            } else {
                StockTotal stockTotal = getStockTotal(detail.getProductId(), warehouseTypeId);
                if (stockTotal.getQuantity() < detail.getQuantity())
                    throw new ValidateException(ResponseMessage.PRODUCT_OUT_OF_STOCK);
                stockOut(stockTotal, detail.getQuantity());

                SaleOrderDetail orderDetail = modelMapper.map(detail, SaleOrderDetail.class);
                // get auto promotion
                OrderDetailShopMapDTO promotionInfo = getPromotions(programDetails, saleOrder, orderDetail, null, detail, productPrice, shopId, listOrderDetail);
                orderDetail.setAutoPromotion(promotionInfo.getDiscount());
                saleOrder.setAutomatePromotion(promotionInfo.getDiscount());

                if (promotionInfo.getDiscount() > 0 || promotionInfo.getPromotionProgramId() != null)
                    listPromotions.add(promotionInfo);
                // set created order detail information
                setDetailCreatedInfo(orderDetail, saleOrder.getId(), productPrice.getPrice(), detail.getQuantity(), shopId);

                listOrderDetail.add(orderDetail);
            }
        }
        return new CoverOrderDetailDTO(listOrderDetail, listOrderComboDetail, listPromotions, productIds, totalQuantity, totalAmount);
    }

    private OnlineOrder checkOnlineOrder(SaleOrder saleOrder, SaleOrderRequest request, Long shopId) {
        OnlineOrder onlineOrder = null;
        if (request.getOrderOnlineId() == null && request.getOnlineNumber() != null){
            boolean isManuallyCreatable = shopClient.isManuallyCreatableOnlineOrderV1(shopId).getData();
            if(!isManuallyCreatable)
                throw new ValidateException(ResponseMessage.MANUALLY_CREATABLE_ONLINE_ORDER_NOT_ALLOW);
            onlineOrderService.checkOnlineNumber(request.getOnlineNumber());
            saleOrder.setOnlineSubType(1);
        }

        if (request.getOrderOnlineId() != null) {
            onlineOrder = onlineOrderRepo.findById(request.getOrderOnlineId())
                    .orElseThrow(() -> new ValidateException(ResponseMessage.ORDER_ONLINE_NOT_FOUND));
            if (onlineOrder.getSynStatus() == 1) throw new ValidateException(ResponseMessage.SALE_ORDER_ALREADY_CREATED);

            List<OnlineOrderDetail> onlineDetails = onlineOrderDetailRepo.findByOnlineOrderId(request.getOrderOnlineId());
            if(!editableOnlineOrder(request, shopId, onlineDetails))
                throw new ValidateException(ResponseMessage.EDITABLE_ONLINE_ORDER_NOT_ALLOW);

            this.onlineSubType(request, saleOrder, onlineDetails);
            saleOrder.setOrderNumber(onlineOrder.getOrderNumber());
        }
        return onlineOrder;
    }

    private boolean editableOnlineOrder(SaleOrderRequest request, Long shopId, List<OnlineOrderDetail> onlineDetails) {
        boolean isEditable = shopClient.isEditableOnlineOrderV1(shopId).getData();
        if (!isEditable) {
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

    private void onlineSubType(SaleOrderRequest request, SaleOrder saleOrder, List<OnlineOrderDetail> onlineDetails) {
        saleOrder.setOnlineSubType(2);
        if (onlineDetails.size() == request.getProducts().size()) {
            for (OnlineOrderDetail productOld : onlineDetails) {
                boolean productExits = false;
                for (ProductOrderRequest productOrder : request.getProducts()) {
                    if (productOld.getSku().equals(productOrder.getProductCode())) {
                        productExits = true;
                        if (!productOld.getQuantity().equals(productOrder.getQuantity())) {
                            saleOrder.setOnlineSubType(3);
                            return;
                        }
                    }
                }
                if (!productExits) {
                    saleOrder.setOnlineSubType(3);
                    return;
                }
            }
        }
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

    public void setDetailCreatedInfo(SaleOrderDetail orderDetail, Long saleOrderId,
                                     double price, int quantity, Long shopId) {
        Date date = new Date();
        Timestamp time = new Timestamp(date.getTime());

        orderDetail.setOrderDate(time);
        orderDetail.setSaleOrderId(saleOrderId);
        orderDetail.setPrice(price);
        orderDetail.setAmount(quantity * price);
        orderDetail.setTotal(quantity * price - orderDetail.getAutoPromotion());
        orderDetail.setPriceNotVat(price - price * VAT);
        orderDetail.setShopId(shopId);
        orderDetail.setIsFreeItem(false);
    }

    public void setComboDetailCreatedInfo(SaleOrderComboDetail orderComboDetail, Long saleOrderId, double price) {
        Date date = new Date();
        Timestamp time = new Timestamp(date.getTime());

        orderComboDetail.setOrderDate(time);
        orderComboDetail.setSaleOrderId(saleOrderId);
        orderComboDetail.setPrice(price);
        orderComboDetail.setPriceNotVat(price - price * VAT);
    }

    public void setSaleOrderCreatedInfo(SaleOrder saleOrder, double totalPaid, double zmPromotion) {
        if (saleOrder.getAmount() - saleOrder.getTotalPromotion() < 0)
            saleOrder.setTotal(new Double(0));
        else
            saleOrder.setTotal(saleOrder.getAmount() - saleOrder.getTotalPromotion());
        // total payment of the bill
        saleOrder.setBalance(totalPaid - (saleOrder.getAmount() - saleOrder.getTotalPromotion())); // change money
        saleOrder.setZmPromotion(zmPromotion);
    }

    // call api from promotion service to set and save
    public void setVoucherInUsed(VoucherDTO voucher, Long saleOrderId) {
        Date date = new Date();
        Timestamp time = new Timestamp(date.getTime());

        voucher.setIsUsed(true);
        voucher.setSaleOrderId(saleOrderId);
        voucher.setOrderDate(time);

        try {
            promotionClient.updateVoucher(voucher);
        } catch (Exception e) {
            throw new ValidateException(ResponseMessage.UPDATE_VOUCHER_FAIL);
        }
    }

    public OrderDetailShopMapDTO getPromotions(List<PromotionProgramDetailDTO> programDetails, SaleOrder saleOrder, SaleOrderDetail saleOrderDetail,
                                               SaleOrderComboDetail saleOrderComboDetail, ProductOrderRequest detail, Price price, Long shopId, List<SaleOrderDetail> listPromotion) {
        double discount = 0;
        OrderDetailShopMapDTO orderDetailShopMapDTO = new OrderDetailShopMapDTO();

        saleOrder.setOrderAmount(detail.getQuantity() * price.getPrice());

        // for each promotion program detail -> if product is in promotion list and match condition -> discount
        for (PromotionProgramDetailDTO promotionProgram : programDetails) {
            if (detail.getProductId() == promotionProgram.getProductId()) {

                // get promotion program
                PromotionProgramDTO promotionProgramDTO = getPromotionProgramById(promotionProgram.getPromotionProgramId());

                double promotionAppliedPrice = price.getPrice();
                // check which discount price type will be applied
                if (promotionProgramDTO.getDiscountPriceType() == 0) {
                    if (EnumUtils.isValidEnum(ProgramApplyDiscountPriceType.class, promotionProgramDTO.getType()))
                        promotionAppliedPrice = price.getPriceNotVat();
                }

                // if sale quantity or sale amount match promotion requirement
                if ((promotionProgram.getSaleQty() != null && detail.getQuantity() >= promotionProgram.getSaleQty())
                        || (promotionProgram.getSaleAmt() != null && detail.getQuantity() * promotionAppliedPrice >= promotionProgram.getSaleAmt())) {

                    // discount amount
                    if (promotionProgram.getDiscAmt() != null)
                        discount += promotionProgram.getDiscAmt();
                    // discount percent
                    if (promotionProgram.getDisPer() != null)
                        discount += (detail.getQuantity() * promotionAppliedPrice) * promotionProgram.getDisPer();
                    // give free item
                    if (promotionProgram.getFreeProductId() != null) {
                        SaleOrderDetail freeItem = new SaleOrderDetail();
                        freeItem.setIsFreeItem(true);
                        freeItem.setAutoPromotion(0D);
                        freeItem.setProductId(promotionProgram.getFreeProductId());
                        freeItem.setQuantity(promotionProgram.getFreeQty());
                        freeItem.setSaleOrderId(saleOrder.getId());
                        freeItem.setShopId(shopId);
                        freeItem.setPrice(price.getPrice());
                        freeItem.setAmount(promotionProgram.getFreeQty() * promotionAppliedPrice);
                        freeItem.setPromotionCode(promotionProgramDTO.getPromotionProgramCode());
                        freeItem.setPromotionName(promotionProgramDTO.getPromotionProgramName());

                        listPromotion.add(freeItem);
                        orderDetailShopMapDTO.setSaleOrderDetail(freeItem);
                    }
                    // set promotion program info for order detail
                    if (saleOrderDetail != null && discount > 0) {
                        saleOrderDetail.setPromotionCode(promotionProgramDTO.getPromotionProgramCode());
                        saleOrderDetail.setPromotionName(promotionProgramDTO.getPromotionProgramName());
                    }
                    if (saleOrderComboDetail != null && discount > 0) {
                        saleOrderComboDetail.setPromotionCode(promotionProgramDTO.getPromotionProgramCode());
                        saleOrderComboDetail.setPromotionName(promotionProgramDTO.getPromotionProgramName());
                    }

                    // set promotion program info
                    modelMapper.map(promotionProgramDTO, OrderDetailShopMapDTO.class);
                    orderDetailShopMapDTO.setPromotionProgramCode(promotionProgram.getPromotionProgramCode());
                    orderDetailShopMapDTO.setPromotionProgramName(promotionProgram.getPromotionProgramName());
                    orderDetailShopMapDTO.setPromotionProgramId(promotionProgram.getPromotionProgramId());
                    orderDetailShopMapDTO.setRelation(promotionProgramDTO.getRelation());
                    orderDetailShopMapDTO.setIsAuto(true);
                }
            }
        }
        orderDetailShopMapDTO.setDiscount(discount);
        return orderDetailShopMapDTO;
    }

    @Override
    public Response<List<ZmFreeItemDTO>> getFreeItems(List<ProductOrderRequest> productList, Long shopId, Long saleOrderId) {
        List<PromotionSaleProductDTO> totalListSaleProduct = new ArrayList<>();
        List<ZmFreeItemDTO> freeItemList = new ArrayList<>();

        freeItemList.addAll(convertOrderDetailToFreeItemDTO(saleOrderId, productList, shopId));
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

    public List<ZmFreeItemDTO> convertOrderDetailToFreeItemDTO(Long saleOrderId, List<ProductOrderRequest> productList, Long shopId) {
        List<ZmFreeItemDTO> response = new ArrayList<>();
        Optional<SaleOrder> saleOrder = repository.findById(saleOrderId);
        if (!saleOrder.isPresent())
            throw new ValidateException(ResponseMessage.SALE_ORDER_DOES_NOT_EXIST);
        CustomerDTO customer = customerClient.getCustomerByIdV1(saleOrder.get().getCustomerId()).getData();
        if (customer == null)
            throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
        Long warehouseTypeId = customerTypeClient.getWarehouseTypeIdByCustomer(shopId).getData();
        if (warehouseTypeId == null)
            throw new ValidateException(ResponseMessage.WARE_HOUSE_NOT_EXIST);
        List<PromotionProgramDetailDTO> programDetails = promotionClient.getPromotionDetailByPromotionIdV1(shopId).getData();
        if (programDetails == null)
            throw new ValidateException(ResponseMessage.PROMOTION_DOSE_NOT_EXISTS);

        List<OrderDetailShopMapDTO> promotionList = createSaleOrderDetail(saleOrder.get(), productList, programDetails, shopId, customer.getCustomerTypeId(), warehouseTypeId).getListPromotions();

        for (OrderDetailShopMapDTO promotion : promotionList) {
            ZmFreeItemDTO freeItem = new ZmFreeItemDTO();

            freeItem.setPromotionProgramCode(promotion.getPromotionProgramCode());
            freeItem.setPromotionProgramName(promotion.getPromotionProgramName());
            if (promotion.getIsAuto() != null && promotion.getIsAuto())
                freeItem.setIsAuto(true);
            if (promotion.getDiscount() != null && promotion.getDiscount() > 0)
                freeItem.setDiscountAmount(promotion.getDiscount());
            if (promotion.getSaleOrderDetail() != null) {
                SaleOrderDetail orderDetail = promotion.getSaleOrderDetail();
                freeItem.setProductId(orderDetail.getProductId());
                freeItem.setPromotionQuantity(orderDetail.getQuantity());

                StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(orderDetail.getProductId(), warehouseTypeId);
                if (stockTotal == null)
                    throw new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND);
                freeItem.setStockQuantity(stockTotal.getQuantity());
            }
            response.add(freeItem);
        }
        return response;
    }

    public SaleOrderDetail convertFreItemToOrderDetail(ZmFreeItemDTO freeItem) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        SaleOrderDetail saleOrderDetail = modelMapper.map(freeItem, SaleOrderDetail.class);
        saleOrderDetail.setIsFreeItem(true);
        saleOrderDetail.setPromotionCode(freeItem.getPromotionProgramCode());
        saleOrderDetail.setPromotionName(freeItem.getPromotionProgramName());

        return saleOrderDetail;
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

    public void setSaleOrderPromotion(SaleOrderDetail saleOrderDetail, Double autoPromotion, Double zmPromotion) {
        saleOrderDetail.setAutoPromotion(autoPromotion);
        saleOrderDetail.setAutoPromotionVat(1D);
        saleOrderDetail.setAutoPromotionNotVat(1D);

        saleOrderDetail.setZmPromotion(zmPromotion);
        saleOrderDetail.setZmPromotionVat(1D);
        saleOrderDetail.setZmPromotionNotVat(1D);
    }

    public List<ProductOrderRequest> convertComboToProducts(ComboProduct combo) {
        List<ProductOrderRequest> result = new ArrayList<>();
        List<ComboProductDetail> productInCombo = comboDetailRepository.findByComboProductIdAndStatus(combo.getId(), 1);

        for (ComboProductDetail comboDetail : productInCombo) {
            ProductOrderRequest product = new ProductOrderRequest();
            product.setProductId(comboDetail.getProductId());
            product.setQuantity(comboDetail.getFactor());
            result.add(product);
        }
        return result;
    }

    public boolean checkPromotionNumInDay(Long customerId, String code, Long orderId) {
        int promotionNumInDay = repository.getNumInDayByPromotionCode(customerId, code, orderId);
        PromotionProgramDTO promotionProgram = promotionClient.getByCodeV1(code).getData();

        if (promotionProgram == null)
            throw new ValidateException(ResponseMessage.PROMOTION_DOSE_NOT_EXISTS);
        if (promotionNumInDay >= promotionProgram.getPromotionDateTime())
            return false;
        return true;
    }

}

