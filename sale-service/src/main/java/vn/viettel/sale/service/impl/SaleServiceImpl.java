package vn.viettel.sale.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.dto.promotion.PromotionProgramDTO;
import vn.viettel.core.dto.promotion.PromotionProgramDiscountDTO;
import vn.viettel.core.dto.promotion.PromotionShopMapDTO;
import vn.viettel.core.dto.promotion.RPT_ZV23DTO;
import vn.viettel.core.dto.voucher.VoucherDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.*;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.util.StringUtils;
import vn.viettel.core.util.ValidationUtils;
import vn.viettel.sale.entities.*;
import vn.viettel.sale.messaging.*;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.*;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.service.feign.*;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    SaleOrderComboDetailRepository saleOrderComboDetailRepo;
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
    ApparamClient apparamClient;
    @Autowired
    ShopClient shopClient;
    @Autowired
    OnlineOrderService onlineOrderService;
    @Autowired
    SalePromotionService salePromotionService;
    @Autowired
    SaleOrderDiscountRepository saleOrderDiscountRepo;
    @Autowired
    SaleOrderComboDiscountRepository saleOrderComboDiscountRepo;
    @Autowired
    SaleOrderService saleOrderService;

    @Autowired
    StockTotalService stockTotalService;

    @Value( "${sale.order.type.apparam}" )
    private String apParamOrderType;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Object createSaleOrder(SaleOrderRequest request, long userId, long roleId, long shopId, boolean printTemp) {
        // check existing customer
        CustomerDTO customer = customerClient.getCustomerByIdV1(request.getCustomerId()).getData();
        if (customer == null)
            throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);

        // check existing shop and order type
        ShopDTO shop = shopClient.getByIdV1(shopId).getData();
        if (shop == null)
            throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);

        // check order type
        ApParamDTO apParamDTO = apparamClient.getApParamByTypeAndvalue(apParamOrderType, request.getOrderType().toString()).getData();
        if(apParamDTO == null) throw new ValidateException(ResponseMessage.AP_PARAM_NOT_EXISTS);

        // information need to be save
        // thông tin giảm giá
        PromotionProgramDiscountDTO discountNeedSave = null;
        // danh sách voucher cần cập nhật
        List<VoucherDTO> lstVoucherNeedSave = new ArrayList<>();
        //danh sale order detail
        List<SaleOrderDetail> saleOrderDetails = new ArrayList<>();
        List<SaleOrderDiscount> saleOrderDiscounts = new ArrayList<>();
        // Online order
        OnlineOrder onlineOrder = null;
        //dùng để cập nhật số suất
        List<PromotionShopMapDTO> promotionShopMaps = new ArrayList<>();
        List<SaleOrderComboDetail> listOrderComboDetails = new ArrayList<>();
        List<SaleOrderComboDiscount> listOrderComboDiscounts = new ArrayList<>();
        // gộp sản phẩm nếu có mua sản phẩm trùng
        HashMap<Long, ProductOrderRequest> mapProductOrder = new HashMap<>();
        for (ProductOrderRequest dto : request.getProducts()){
            if (dto.getQuantity() == null || dto.getQuantity() == 0)
                throw new ValidateException(ResponseMessage.NUMBER_GREATER_THAN_ZERO);
            if (mapProductOrder.containsKey(dto.getProductId())){
                ProductOrderRequest exited = mapProductOrder.get(dto.getProductId());
                exited.setQuantity(exited.getQuantity() + dto.getQuantity());
                mapProductOrder.put(dto.getProductId(), exited);
            }else{
                mapProductOrder.put(dto.getProductId(), dto);
            }
        }
        List<ProductOrderRequest> lstProductOrder = new ArrayList<>(mapProductOrder.values());
        request.setProducts(lstProductOrder);

        //1. check existing promotion code - mã giảm giá
        if (StringUtils.stringNotNullOrEmpty(request.getDiscountCode())){
            OrderPromotionRequest orderRequest = new OrderPromotionRequest();
            orderRequest.setCustomerId(request.getCustomerId());
            orderRequest.setOrderType(request.getOrderType());
            orderRequest.setProducts(lstProductOrder);

            SalePromotionDTO salePromotion = salePromotionService.getDiscountCode(request.getDiscountCode(), shopId, orderRequest );
            if (salePromotion == null) throw new ValidateException(ResponseMessage.PROMOTION_IN_USE, request.getDiscountCode());

            Double discountValue = salePromotion.getAmount().getAmount();
            if (!request.getDiscountAmount().equals(discountValue)) throw new ValidateException(ResponseMessage.PROMOTION_AMOUNT_NOTEQUALS);

            discountNeedSave = promotionClient.getPromotionDiscount(request.getDiscountCode(), shopId).getData();
            discountNeedSave.setIsUsed(1);
            discountNeedSave.setOrderCustomerCode(customer.getCustomerCode());
            discountNeedSave.setActualDiscountAmount(discountValue);
            discountNeedSave.setOrderShopCode(shop.getShopCode());

            PromotionShopMapDTO promotionShopMap = promotionClient.getPromotionShopMapV1(discountNeedSave.getPromotionProgramId(), shopId).getData();
            Double received = promotionShopMap.getQuantityReceived()!=null?promotionShopMap.getQuantityReceived():0;
            promotionShopMap.setQuantityReceived(received + discountValue);
            promotionShopMaps.add(promotionShopMap);

        }

        //sanh sách id sản phẩm theo số lượng mua và km
        HashMap<Long, Integer> mapProductWithQty = new HashMap<>();
        boolean isReturn = true;
        double customerPurchase = 0;
        List<Long> productNotAccumulated = promotionClient.getProductsNotAccumulatedV1(new ArrayList<>(mapProductOrder.keySet())).getData();
        List<Price> productPrices = priceRepository.findProductPriceWithType(lstProductOrder.stream().map(i -> i.getProductId()).collect(Collectors.toList()),
                customer.getCustomerTypeId(), DateUtils.convertToDate(LocalDateTime.now()));

        // gán sản phẩm mua vào trước
        for (ProductOrderRequest item : lstProductOrder){
            if (item.getQuantity() != null && item.getQuantity() > 0) {

                if (!mapProductWithQty.containsKey(item.getProductId())) {
                    mapProductWithQty.put(item.getProductId(), item.getQuantity());
                }else{
                    Integer qty = mapProductWithQty.get(item.getProductId()) + item.getQuantity();
                    mapProductWithQty.put(item.getProductId(), qty);
                }

                //tạo order detail
                Price productPrice = getPrice(productPrices, item.getProductId());

                SaleOrderDetail saleOrderDetail = new SaleOrderDetail();
                saleOrderDetail.setIsFreeItem(false);
                saleOrderDetail.setQuantity(item.getQuantity());
                saleOrderDetail.setPrice(productPrice.getPrice());
                saleOrderDetail.setPriceNotVat(productPrice.getPriceNotVat());
                saleOrderDetail.setProductId(item.getProductId());
                saleOrderDetail.setShopId(shopId);
                saleOrderDetail.setAmount(saleOrderDetail.getPrice() * saleOrderDetail.getQuantity());
                saleOrderDetail.setTotal(saleOrderDetail.getAmount());

                if(!productNotAccumulated.contains(item.getProductId()))
                    customerPurchase += saleOrderDetail.getAmount();

                // printTemp
                if(printTemp) {
                    saleOrderDetail.setProductCode(item.getProductCode());
                    saleOrderDetail.setProductName(item.getProductName());
                }

                saleOrderDetails.add(saleOrderDetail);
            }
        }

        // 4. voucher
        double voucherAmount = 0;
        double autoPromtion = 0;
        double autoPromotionExVat = 0;
        double autoPromotionInVat = 0;
        double zmPromotion = 0;
        double zmPromotionExVat = 0;
        double zmPromotionInVat = 0;
        double promotion = 0;
        double promotionExVat = 0;
        double promotionInVat = 0;
        if(request.getVouchers() != null){
            VoucherDTO voucher = null;
            for (OrderVoucherRequest orderVoucher : request.getVouchers() ) {
                if (orderVoucher.getId() != null)
                    voucher = promotionClient.getVouchersV1(orderVoucher.getId()).getData();

                if (voucher == null || (voucher != null && (voucher.getIsUsed() || voucher.getPrice().compareTo(orderVoucher.getPrice()) != 0 )))
                    throw new ValidateException(ResponseMessage.VOUCHER_DOES_NOT_EXISTS);

                if (voucher.getPrice() != null) voucherAmount += voucher.getPrice();

                if (voucher != null) {
                    voucher.setOrderShopCode(shop.getShopCode());
                    voucher.setIsUsed(true);
                    voucher.setPriceUsed(orderVoucher.getPrice());
                    voucher.setOrderCustomerCode(customer.getCustomerCode());
                    lstVoucherNeedSave.add(voucher);
                }
            }
        }

        // 2. check promotion - khuyến mãi
        if (request.getPromotionInfo() != null && !request.getPromotionInfo().isEmpty()){
            OrderPromotionRequest orderRequest = new OrderPromotionRequest();
            orderRequest.setCustomerId(request.getCustomerId());
            orderRequest.setOrderType(request.getOrderType());
            orderRequest.setProducts(lstProductOrder);
            //key id program, key amount receive
            HashMap<Long,Double> mapMoneys = new HashMap<>();
            for (SalePromotionDTO inputPro : request.getPromotionInfo()){
                if(inputPro.getAmount() != null && inputPro.getAmount().getAmount() != null)
                    mapMoneys.put(inputPro.getProgramId(), inputPro.getAmount().getAmount());
            }

            SalePromotionCalculationDTO calculationDTO = salePromotionService.getSaleItemPromotions(orderRequest, shopId, mapMoneys, true);
            if (calculationDTO == null)
                throw new ValidateException(ResponseMessage.PROMOTION_IN_USE, "");

            List<SalePromotionDTO> lstSalePromotions = calculationDTO.getLstSalePromotions();

            List<Long> dbPromotionIds = lstSalePromotions.stream().map(item -> item.getProgramId()).collect(Collectors.toList());
            // danh sách km tiền -> dùng kiểm tra tổng tiền km có đúng
            List<SalePromotionCalItemRequest> promotionInfo = new ArrayList<>();

            for (SalePromotionDTO inputPro : request.getPromotionInfo()){
                if (dbPromotionIds.contains(inputPro.getProgramId()) && inputPro.getIsUse()){ // kiểm tra ctkm còn được sử dụng
                    SalePromotionDTO dbPro = new SalePromotionDTO();
                    for (SalePromotionDTO dbP : lstSalePromotions){
                        if(dbP.getProgramId().equals(inputPro.getProgramId())){
                            dbPro = dbP;
                            break;
                        }
                    }

                    if (dbPro.getIsReturn() != null && !dbPro.getIsReturn()) isReturn = false;

                    // tổng số lượng sản phẩm khuyến mãi
                    if(inputPro.getProducts()!=null && !inputPro.getProducts().isEmpty()){
                        //kiểm tra nếu km tay tổng số isEditable = {Boolean@18816} false lượng km > 0
                        int totalQty = 0;
                        for(FreeProductDTO product: inputPro.getProducts()){
                            totalQty += product.getQuantity()!=null?product.getQuantity():0;
                        }
                        inputPro.setTotalQty(totalQty);

                        if("zm".equalsIgnoreCase(dbPro.getProgramType())){
                            if(inputPro.getTotalQty() < 1) throw new ValidateException(ResponseMessage.NO_PRODUCT, inputPro.getPromotionProgramName());
                        }else {//km tự động
                            if(dbPro.getContraintType() == 1){ // one free item
                                List<String> groupLevels = dbPro.getProducts().stream().map(ie ->ie.getGroupOneFreeItem()).distinct().collect(Collectors.toList());

                                for(String group : groupLevels){
                                     totalQty = 0;
                                    for(FreeProductDTO product: inputPro.getProducts()){
                                        if(group.equalsIgnoreCase(product.getGroupOneFreeItem())) {
                                            totalQty += product.getQuantity()!=null?product.getQuantity():0;
                                        }
                                    }

                                    List<Integer> lstMax = dbPro.getProducts().stream().map(ie -> {
                                        if(group.equals(ie.getGroupOneFreeItem())) return ie.getQuantityMax();
                                        return null;
                                    }).filter(Objects::nonNull).distinct().collect(Collectors.toList());
                                    if(lstMax.size() == 1){ // cùng max value
                                        if(dbPro.getEditable() == null || dbPro.getEditable() == 0){ // không được sửa tổng số lượng tặng < số lượng cơ cấu
                                            if(totalQty != lstMax.get(0)) throw new ValidateException(ResponseMessage.NO_PRODUCT, inputPro.getPromotionProgramName());
                                        }else{ // được tặng số lượng nhỏ hơn số cơ cấu
                                            //TODO
                                        }
                                    }else{ // khác max value
                                        if(dbPro.getEditable() == null || dbPro.getEditable() == 0 ){ // không được sửa tổng số lượng tặng < số lượng cơ cấu
                                            for(FreeProductDTO product: inputPro.getProducts()){
                                                if(product.getQuantity() != null && product.getQuantity() > 0 && group.equalsIgnoreCase(product.getGroupOneFreeItem())){
                                                    if(totalQty < product.getQuantityMax() || totalQty > product.getQuantityMax())
                                                        throw new ValidateException(ResponseMessage.NO_PRODUCT, inputPro.getPromotionProgramName());
                                                }
                                            }
                                        }else{//được tặng số lượng nhỏ hơn số cơ cấu
                                            //TODO
                                        }
                                    }
                                }
                            }
                        }
                    }

                    //kiểm tra đã đủ số xuất
                    if (!salePromotionService.checkPromotionLimit(inputPro, shopId))
                        throw new ValidateException(ResponseMessage.PROMOTION_NOT_ENOUGH_VALUE, inputPro.getPromotionProgramName());

                    PromotionShopMapDTO promotionShopMap = promotionClient.getPromotionShopMapV1(inputPro.getProgramId(), shopId).getData();
                    // kiểm tra tồn kho có đủ
                    if (inputPro.getProducts() != null && !inputPro.getProducts().isEmpty()){
                        List<Long> productIds = inputPro.getProducts().stream().map(item -> item.getProductId()).collect(Collectors.toList());
//                        List<ComboProductDetailDTO> combosDis = comboProductRepository.findComboProduct(customer.getCustomerTypeId(), productIds);
//                        if(combosDis != null) {
//                            for (ComboProductDetailDTO combo : combosDis) {
//                                if(!productIds.contains(combo.getProductId())) productIds.add(combo.getProductId());
//                            }
//                        }
                        List<Price> productPrices1 = priceRepository.findProductPriceWithType(productIds, customer.getCustomerTypeId(), DateUtils.convertToDate(LocalDateTime.now()));

                        for (FreeProductDTO ipP : inputPro.getProducts()){
                            if (!checkProductInPromotion(lstSalePromotions, inputPro.getProgramId(), ipP.getProductId(), ipP.getQuantity()))
                                throw new ValidateException(ResponseMessage.PRODUCT_NOT_IN_PROMOTION, ipP.getProductCode(), inputPro.getPromotionProgramName());

                            if (ipP.getQuantity() != null && ipP.getQuantity() > 0){

                                if (!mapProductWithQty.containsKey(ipP.getProductId())) {
                                    mapProductWithQty.put(ipP.getProductId(), ipP.getQuantity());
                                }else{
                                    Integer qty = mapProductWithQty.get(ipP.getProductId()) + ipP.getQuantity();
                                    mapProductWithQty.put(ipP.getProductId(), qty);
                                }

                                //new sale detail
                                SaleOrderDetail saleOrderDetail = new SaleOrderDetail();
                                saleOrderDetail.setIsFreeItem(true);
                                saleOrderDetail.setQuantity(ipP.getQuantity());
                                saleOrderDetail.setPrice(0.0);
                                saleOrderDetail.setPriceNotVat(0.0);
                                saleOrderDetail.setProductId(ipP.getProductId());
                                Price price = getPrice(productPrices1, saleOrderDetail.getProductId());
                                saleOrderDetail.setPrice(price.getPrice());
                                saleOrderDetail.setPriceNotVat(price.getPriceNotVat());
                                saleOrderDetail.setShopId(shopId);
                                saleOrderDetail.setAmount(0.0);
                                saleOrderDetail.setTotal(0.0);
                                saleOrderDetail.setPromotionCode(inputPro.getPromotionProgramCode());
                                saleOrderDetail.setPromotionName(inputPro.getPromotionProgramName());
                                saleOrderDetail.setPromotionType(inputPro.getProgramType());
                                saleOrderDetail.setLevelNumber(ipP.getLevelNumber() == null ? 1 : ipP.getLevelNumber());
                                // printTemp
                                if(printTemp) {
                                    saleOrderDetail.setProductCode(ipP.getProductCode());
                                    saleOrderDetail.setProductName(ipP.getProductName());
                                }

                                saleOrderDetails.add(saleOrderDetail);

                                if (ipP.getQuantity() != null){
                                    Double received = promotionShopMap.getQuantityReceived()!=null?promotionShopMap.getQuantityReceived():0;
                                    promotionShopMap.setQuantityReceived(received + ipP.getQuantity());
                                    promotionShopMaps.add(promotionShopMap);
                                }

                                //get combo
//                                if(combosDis != null) {
//                                    for (ComboProductDetailDTO combo : combosDis) {
//                                        if (ipP.getProductId().equals(combo.getRefProductId()) && combo.getFactor() != null) {
//                                            SaleOrderComboDetail orderComboDetail = new SaleOrderComboDetail();
//                                            orderComboDetail.setComboProductId(combo.getComboProductId());
//                                            orderComboDetail.setComboQuantity(ipP.getQuantity());
//                                            orderComboDetail.setQuantity(ipP.getQuantity() * combo.getFactor());
//                                            orderComboDetail.setProductId(combo.getProductId());
//                                            orderComboDetail.setPrice(0.0);
//                                            orderComboDetail.setPriceNotVat(0.0);
//                                            Price price1 = getPrice(productPrices1, orderComboDetail.getProductId());
//                                            if(price1 != null){
//                                                orderComboDetail.setPrice(price1.getPrice());
//                                                orderComboDetail.setPriceNotVat(price1.getPriceNotVat());
//                                            }
//                                            orderComboDetail.setAmount(0.0);
//                                            orderComboDetail.setTotal(0.0);
//                                            orderComboDetail.setIsFreeItem(true);
//                                            orderComboDetail.setPromotionCode(dbPro.getPromotionProgramCode());
//                                            orderComboDetail.setLevelNumber(ipP.getLevelNumber() == null ? 1 : ipP.getLevelNumber());
//
//                                            listOrderComboDetails.add(orderComboDetail);
//                                        }
//                                    }
//                                }
                            }
                        }
                    }else if (inputPro.getAmount() != null && dbPro.getAmount() != null){
                        promotion += dbPro.getAmount().getAmount() == null ? 0 : dbPro.getAmount().getAmount();
                        promotionExVat += dbPro.getTotalAmtExTax() == null ? 0 : dbPro.getTotalAmtExTax();
                        promotionInVat += dbPro.getTotalAmtInTax() == null ? 0 : dbPro.getTotalAmtInTax();
                        if("zm".equalsIgnoreCase(dbPro.getProgramType())){
                            zmPromotion += dbPro.getAmount().getAmount() == null ? 0 : dbPro.getAmount().getAmount();
                            zmPromotionExVat += dbPro.getTotalAmtExTax() == null ? 0 : dbPro.getTotalAmtExTax();
                            zmPromotionInVat += dbPro.getTotalAmtInTax() == null ? 0 : dbPro.getTotalAmtInTax();
                        }else{
                            autoPromtion += dbPro.getAmount().getAmount() == null ? 0 : dbPro.getAmount().getAmount();
                            autoPromotionExVat += dbPro.getTotalAmtExTax() == null ? 0 : dbPro.getTotalAmtExTax();
                            autoPromotionInVat += dbPro.getTotalAmtInTax() == null ? 0 : dbPro.getTotalAmtInTax();
                        }
                        SalePromotionCalItemRequest sPP = new SalePromotionCalItemRequest();
                        inputPro.getAmount().setPercentage(dbPro.getAmount().getPercentage());
                        sPP.setAmount(inputPro.getAmount());
                        sPP.setPromotionType(inputPro.getPromotionType());
                        sPP.setProgramId(inputPro.getProgramId());
                        promotionInfo.add(sPP);

                        if (dbPro.getAmount().getDiscountInfo() != null){
                            for (SaleDiscountSaveDTO item : dbPro.getAmount().getDiscountInfo()){
                                //tạo sale discount
                                SaleOrderDiscount saleOrderDiscount = new SaleOrderDiscount();
                                saleOrderDiscount.setPromotionProgramId(dbPro.getProgramId());
                                saleOrderDiscount.setPromotionCode(dbPro.getPromotionProgramCode());
                                saleOrderDiscount.setPromotionName(dbPro.getPromotionProgramName());
                                saleOrderDiscount.setPromotionType(dbPro.getProgramType());
                                saleOrderDiscount.setIsAutoPromotion(dbPro.getPromotionType() == 0);
                                saleOrderDiscount.setLevelNumber(item.getLevelNumber() == null ? 1 : item.getLevelNumber());
                                saleOrderDiscount.setDiscountAmount(roundValue(item.getAmount()));
                                saleOrderDiscount.setDiscountAmountNotVat(roundValue(item.getAmountExTax()));
                                saleOrderDiscount.setDiscountAmountVat(roundValue(item.getAmountInTax()));
                                saleOrderDiscount.setMaxDiscountAmount(roundValue(item.getMaxAmount()));
                                saleOrderDiscount.setProductId(item.getProductId());
                                saleOrderDiscounts.add(saleOrderDiscount);

                                //update buying product
                                for(SaleOrderDetail buyP : saleOrderDetails){
                                    if(buyP.getProductId().equals(item.getProductId()) && !buyP.getIsFreeItem()){
                                        if("zm".equalsIgnoreCase(dbPro.getProgramType())){
                                            buyP.setZmPromotion((roundValue(buyP.getZmPromotion() == null? 0 : buyP.getZmPromotion()) + item.getAmount()));
                                            buyP.setZmPromotionVat(roundValue((buyP.getZmPromotionVat() == null? 0 : buyP.getZmPromotionVat()) + item.getAmountInTax()));
                                            buyP.setZmPromotionNotVat(roundValue((buyP.getZmPromotionNotVat() == null? 0 : buyP.getZmPromotionNotVat()) + item.getAmountExTax()));
                                        }else{
                                            buyP.setAutoPromotion(roundValue((buyP.getAutoPromotion() == null? 0 : buyP.getAutoPromotion()) + item.getAmount()));
                                            buyP.setAutoPromotionVat(roundValue((buyP.getAutoPromotionVat() == null? 0 : buyP.getAutoPromotionVat()) + item.getAmountInTax()));
                                            buyP.setAutoPromotionNotVat(roundValue((buyP.getAutoPromotionNotVat() == null? 0 : buyP.getAutoPromotionNotVat()) + item.getAmountExTax()));
                                        }
                                        double disAmt = 0;
                                        if(buyP.getAutoPromotionVat() != null) disAmt = buyP.getAutoPromotionVat();
                                        if(buyP.getZmPromotionVat() != null) disAmt += buyP.getZmPromotionVat();
                                        buyP.setTotal(roundValue(buyP.getAmount() - disAmt));
                                    }
                                }
                            }
                        }

                        if(inputPro.getTotalAmtInTax() != null){
//                            promotionShopMap.setAmountMax(promotionShopMap.getAmountMax() - inputPro.getTotalAmtInTax());
//                            promotionShopMaps.add(promotionShopMap);
                            Double received = promotionShopMap.getQuantityReceived()!=null?promotionShopMap.getQuantityReceived():0;
                            promotionShopMap.setQuantityReceived(received + inputPro.getTotalAmtInTax());
                            promotionShopMaps.add(promotionShopMap);
                        }
                    }

                    //update buying product
                    if(inputPro.getLstProductId() != null) {
                        for (SaleOrderDetail buyP : saleOrderDetails) {
                            if (inputPro.getLstProductId().contains(buyP.getProductId()) && !buyP.getIsFreeItem()) {
                                if (buyP.getPromotionCode() == null) {
                                    buyP.setPromotionType(inputPro.getProgramType());
                                    buyP.setPromotionCode(inputPro.getPromotionProgramCode());
                                    buyP.setPromotionName(inputPro.getPromotionProgramName());
                                } else {
                                    buyP.setPromotionType(buyP.getPromotionType() + ", " + inputPro.getProgramType());
                                    buyP.setPromotionCode(buyP.getPromotionCode() + ", " + inputPro.getPromotionProgramCode());
                                    buyP.setPromotionName(buyP.getPromotionName() + ", " + inputPro.getPromotionProgramName());
                                }
                            }
                        }
                    }
                }
                else{
                    throw new ValidateException(ResponseMessage.PROMOTION_IN_USE, inputPro.getPromotionProgramName());
                }
            }

            //Nếu tiền giảm giá > tiền đơn hàng : thì thông báo không thể tạo đơn hàng có doanh số <0
            if(request.getTotalOrderAmount() < promotionInVat)
                throw new ValidateException(ResponseMessage.PROMOTION_OVER_BILL);

            List<ComboProductDetailDTO> combos = comboProductRepository.findComboProduct(new ArrayList<>(mapProductOrder.keySet()));
            if(!combos.isEmpty()) {
                List<Price> subProductPrices = priceRepository.findProductPriceWithType(combos.stream().map(item -> item.getProductId()).distinct().collect(Collectors.toList()),
                        customer.getCustomerTypeId(), DateUtils.convertToDate(LocalDateTime.now()));
                createSaleOrderComboDetail(saleOrderDetails, combos, subProductPrices).stream().forEachOrdered(listOrderComboDetails::add);
                createSaleOrderComboDiscount(saleOrderDiscounts, combos, subProductPrices).stream().forEachOrdered(listOrderComboDiscounts::add);
            }

            //3. kiểm tra số tiền km có đúng
            SalePromotionCalculationRequest calculationRequest = new SalePromotionCalculationRequest();
            calculationRequest.setOrderType(request.getOrderType());
            calculationRequest.setCustomerId(request.getCustomerId());
            calculationRequest.setDiscountAmount(request.getDiscountAmount());
            calculationRequest.setAccumulatedAmount(request.getAccumulatedAmount());
            calculationRequest.setTotalOrderAmount(request.getTotalOrderAmount());
            calculationRequest.setVoucherAmount(voucherAmount);
            calculationRequest.setPromotionInfo(promotionInfo);
            calculationRequest.setOrderRequest(orderRequest);
            SalePromotionCalculationDTO salePromotionCalculation = salePromotionService.promotionCalculation(calculationRequest, shopId);
            if(salePromotionCalculation.getPromotionAmount() == null) salePromotionCalculation.setPromotionAmount(0.0);
            if(salePromotionCalculation.getPaymentAmount() == null) salePromotionCalculation.setPaymentAmount(0.0);
            if(request.getPromotionAmount() == null) request.setPromotionAmount(0.0);
            if(request.getPromotionAmount() == null) request.setPromotionAmount(0.0);

//            if(salePromotionCalculation.getPromotionAmount().intValue() != request.getPromotionAmount().intValue() ||
//           salePromotionCalculation.getPaymentAmount().intValue() != request.getPaymentAmount().intValue())
//                throw new ValidateException(ResponseMessage.PROMOTION_AMOUNT_NOT_CORRECT);
        }else{
            List<ComboProductDetailDTO> combos = comboProductRepository.findComboProduct(new ArrayList<>(mapProductOrder.keySet()));
            if(!combos.isEmpty()) {
                List<Price> subProductPrices = priceRepository.findProductPriceWithType(combos.stream().map(item -> item.getProductId()).distinct().collect(Collectors.toList()),
                        customer.getCustomerTypeId(), DateUtils.convertToDate(LocalDateTime.now()));
                createSaleOrderComboDetail(saleOrderDetails, combos, subProductPrices).stream().forEachOrdered(listOrderComboDetails::add);
            }
        }

        //check warehouse
        CustomerTypeDTO customerType = null;
        if(request.getCustomerId() == null) customerType = customerTypeClient.getCusTypeIdByShopIdV1(shopId);
        else customerType = customerTypeClient.getCusTypeByCustomerIdV1(request.getCustomerId());
        if (customerType == null) throw new ValidateException(ResponseMessage.WARE_HOUSE_NOT_EXIST);

        //kiểm tra xem tổng sản phẩm mua + km có vượt quá tôn kho
        List<FreeProductDTO> freeProductDTOs = productRepository.findProductWithStock(shopId, customerType.getWareHouseTypeId(), new ArrayList<>(mapProductWithQty.keySet()));
        for (FreeProductDTO freeProductDTO : freeProductDTOs){
            if(freeProductDTO == null || (freeProductDTO.getStockQuantity() != null && freeProductDTO.getStockQuantity() < mapProductWithQty.get(freeProductDTO.getProductId())))
                throw new ValidateException(ResponseMessage.PRODUCT_OUT_OF_STOCK, freeProductDTO.getProductCode() + " - " + freeProductDTO.getProductName(), this.withLargeIntegers(freeProductDTO.getStockQuantity()) + "");
        }

        // 5. kiểm tra tiền tích lũy
        if ((customer.getAmountCumulated()!=null &&  request.getAccumulatedAmount()!=null && customer.getAmountCumulated() < request.getAccumulatedAmount()))
            throw new ValidateException(ResponseMessage.ACCUMULATED_AMOUNT_OVER);

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        if(saleOrderDetails.isEmpty())
            throw new ValidateException(ResponseMessage.PLEASE_IMPORT_PRODUCTS);

        SaleOrder saleOrder = modelMapper.map(request, SaleOrder.class);
        saleOrder.setOnlineNumber(null);
        saleOrder.setOrderNumber(createOrderNumber(shop));
        saleOrder.setOrderDate(LocalDateTime.now());
        saleOrder.setShopId(shopId);
        saleOrder.setSalemanId(userId);
        saleOrder.setCustomerId(customer.getId());
        saleOrder.setWareHouseTypeId(customerType.getWareHouseTypeId());
        saleOrder.setAmount(request.getTotalOrderAmount());
        saleOrder.setTotalPromotion(roundValue(promotion));
        saleOrder.setTotalPromotionVat(roundValue(promotionInVat));
        saleOrder.setTotalPromotionNotVat(roundValue(promotionExVat));
        saleOrder.setTotalVoucher(roundValue(voucherAmount));
        saleOrder.setPaymentType(request.getPaymentType());
        saleOrder.setDeliveryType(request.getDeliveryType());
        saleOrder.setOrderType(request.getOrderType());
        saleOrder.setAutoPromotion(roundValue(autoPromtion));
        saleOrder.setAutoPromotionNotVat(roundValue(autoPromotionExVat));
        saleOrder.setAutoPromotionVat(roundValue(autoPromotionInVat));
        saleOrder.setZmPromotion(roundValue(zmPromotion));
        saleOrder.setZmPromotionVat(roundValue(zmPromotionInVat));
        saleOrder.setZmPromotionNotVat(roundValue(zmPromotionExVat));
        //tiền mua hàng sau chiết khấu, và không tính những sp không được tích luỹ
        saleOrder.setCustomerPurchase(roundValue(customerPurchase));
        saleOrder.setDiscountCodeAmount(roundValue(request.getDiscountAmount()));
        saleOrder.setTotalPaid(roundValue(request.getRemainAmount()));
        saleOrder.setTotal(roundValue(request.getPaymentAmount()));
        saleOrder.setBalance(roundValue(request.getExtraAmount()));
        saleOrder.setMemberCardAmount(request.getAccumulatedAmount());
        saleOrder.setUsedRedInvoice(false);
        saleOrder.setNote(request.getNote());
        saleOrder.setType(1);
        saleOrder.setTotalCustomerPurchase(customer.getTotalBill());
        saleOrder.setIsReturn(isReturn);
        if(saleOrder.getTotalPaid() < 1) {
            double amountDisTotal = 0;
            // trừ tiền giảm giá
            if (saleOrder.getTotalPromotion() != null) {
                amountDisTotal += saleOrder.getTotalPromotion();
            }
            if (saleOrder.getTotalVoucher() != null) {
                amountDisTotal += saleOrder.getTotalVoucher();
            }
            // trừ tiền khuyến mãi
            if (saleOrder.getDiscountCodeAmount() != null) {
                amountDisTotal += saleOrder.getDiscountCodeAmount();
            }

            double remain = saleOrder.getAmount() - amountDisTotal;
            // trừ tiền tích lũy
            if(saleOrder.getMemberCardAmount() != null && saleOrder.getMemberCardAmount() > 0) {
                saleOrder.setMemberCardAmount(remain);
            }
            if (saleOrder.getDiscountCodeAmount() != null && saleOrder.getDiscountCodeAmount() > 0 && remain < 0) {
                if(saleOrder.getDiscountCodeAmount() >= -remain ) {
                    saleOrder.setDiscountCodeAmount(-remain);
                    remain = 0;
                }
                else {
                    remain = saleOrder.getDiscountCodeAmount() + remain;
                    saleOrder.setDiscountCodeAmount(0D);
                }
            }
            if (saleOrder.getTotalVoucher() != null && saleOrder.getTotalVoucher() > 0 && remain < 0) {
                saleOrder.setTotalVoucher(remain);
            }
        }

        if (apParamDTO.getApParamCode().startsWith("ONLINE") && (request.getOrderOnlineId() != null || (request.getOnlineNumber() != null && !request.getOnlineNumber().trim().isEmpty())))
            onlineOrder = this.checkOnlineOrder(saleOrder, request, shopId);

        if(printTemp){
            return saleOrderService.createPrintSaleOrderDTO(shopId, customer, saleOrder, saleOrderDetails, saleOrderDiscounts);
        }

        repository.save(saleOrder);

        if (onlineOrder != null) {
            onlineOrder.setSynStatus(1);
            onlineOrder.setSaleOrderId(saleOrder.getId());
            onlineOrder.setOrderDate(saleOrder.getOrderDate());
            onlineOrderRepo.save(onlineOrder);
        }

        //update sale detail
        for(SaleOrderDetail saleOrderDetail : saleOrderDetails){
            saleOrderDetail.setOrderDate(saleOrder.getOrderDate());
            saleOrderDetail.setSaleOrderId(saleOrder.getId());
            saleOrderDetailRepository.save(saleOrderDetail);
        }

        if(saleOrderDiscounts != null){
            for(SaleOrderDiscount saleOrderDiscount : saleOrderDiscounts){
                saleOrderDiscount.setOrderDate(saleOrder.getOrderDate());
                saleOrderDiscount.setSaleOrderId(saleOrder.getId());
                saleOrderDiscountRepo.save(saleOrderDiscount);
            }
        }

        //update voucher
        if(lstVoucherNeedSave != null){
            for(VoucherDTO voucher : lstVoucherNeedSave){
                voucher.setOrderAmount(saleOrder.getAmount());
                voucher.setOrderDate(saleOrder.getOrderDate());
                voucher.setSaleOrderId(saleOrder.getId());
                voucher.setOrderNumber(saleOrder.getOrderNumber());
                promotionClient.updateVoucherV1(voucher);
            }
        }

        //update discount
        if (discountNeedSave != null){
            discountNeedSave.setOrderAmount(saleOrder.getAmount());
            discountNeedSave.setOrderNumber(saleOrder.getOrderNumber());
            discountNeedSave.setOrderDate(saleOrder.getOrderDate());
            promotionClient.updatePromotionProgramDiscountV1(discountNeedSave).getData();
        }

        //update combo
        if(listOrderComboDetails != null){
            for(SaleOrderComboDetail combo : listOrderComboDetails){
                combo.setOrderDate(saleOrder.getOrderDate());
                combo.setSaleOrderId(saleOrder.getId());
                saleOrderComboDetailRepo.save(combo);
            }
        }

        //update combo discount
        if(listOrderComboDiscounts != null){
            for(SaleOrderComboDiscount combo : listOrderComboDiscounts){
                combo.setOrderDate(saleOrder.getOrderDate());
                combo.setSaleOrderId(saleOrder.getId());
                saleOrderComboDiscountRepo.save(combo);
            }
        }

        //update số suât
        for(PromotionShopMapDTO item : promotionShopMaps){
            promotionClient.updatePromotionShopMapV1(item);
        }

        this.updateStockTotal(mapProductWithQty, shopId, customerType.getWareHouseTypeId() );

        //update doanh số tích lũy và tiền tích lũy cho customer, số đơn mua trong ngày...
        updateCustomer(saleOrder, customer, false);

        //update AccumulatedAmount (bảng RPT_CUS_MEM_AMOUNT) (tiền tích lũy) = tiền tích lũy hiện tại - saleOrder.getMemberCardAmount()
        updateAccumulatedAmount(saleOrder.getMemberCardAmount(), customer.getId());
        // update RPT_ZV23: nếu có km zv23

        if (request.getPromotionInfo() != null && !request.getPromotionInfo().isEmpty()) {
            for (SalePromotionDTO inputPro : request.getPromotionInfo()) {
                if ("zv23".equalsIgnoreCase(inputPro.getProgramType())) this.updateRPTZV23(inputPro, customer, shopId);
            }
        }

        return saleOrder.getId();
    }

    private Price getPrice(List<Price> productPrices, Long productId){
        Price productPrice = null;
        for(Price price : productPrices){
            if(price.getProductId().equals(productId)) {
                productPrice = price;
                if (productPrice.getPrice() == null) productPrice.setPrice(0.0);
                if (productPrice.getPriceNotVat() == null) productPrice.setPriceNotVat(0.0);
                break;
            }
        }
        if (productPrice == null)
            throw new ValidateException(ResponseMessage.NO_PRICE_APPLIED);

        return productPrice;
    }

    private double roundValue(Double value){
        if(value == null) return 0;
        return Math.round(value);
    }

    public void updateRPTZV23(SalePromotionDTO inputPro, CustomerDTO customer, Long shopId) {
        RPT_ZV23DTO rpt_zv23DTO = promotionClient.checkZV23RequireV1(inputPro.getPromotionProgramCode(), customer.getId(), shopId).getData();
        if(rpt_zv23DTO!=null) {
            Double amount =  rpt_zv23DTO.getTotalAmount()!=null?rpt_zv23DTO.getTotalAmount():0;
            RPT_ZV23Request zv23Request = new RPT_ZV23Request();
            zv23Request.setTotalAmount(amount + inputPro.getZv23Amount());
            promotionClient.updateRPTZV23V1(rpt_zv23DTO.getId(), zv23Request);
        }else{
            PromotionProgramDTO program = promotionClient.getByIdV1(inputPro.getProgramId()).getData();
            RPT_ZV23Request zv23Request =
                new RPT_ZV23Request(program.getId(), program.getPromotionProgramCode(), program.getFromDate(), program.getToDate(), shopId, customer.getId(), inputPro.getZv23Amount());
            promotionClient.createRPTZV23V1(zv23Request);
        }
    }

    public void updateAccumulatedAmount(Double accumulatedAmount, Long customerId) {
        if (accumulatedAmount == null) return;
        MemberCustomerRequest request = new MemberCustomerRequest(accumulatedAmount);
        customerClient.updateMemberCustomerV1(customerId, request);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateStockTotal( Map<Long, Integer> productTotalMaps, Long shopId, Long warehouseTypeId) {
        if(productTotalMaps != null) {
            List<StockTotal> stockTotals = stockTotalRepository.getStockTotal(shopId, warehouseTypeId,
                    new ArrayList<>(productTotalMaps.keySet()));
            for(Map.Entry<Long, Integer> entry : productTotalMaps.entrySet()) {
                stockTotalService.validateStockTotal(stockTotals, entry.getKey(), -entry.getValue());
            }
            for(Map.Entry<Long, Integer> entry : productTotalMaps.entrySet()) {
                stockTotalService.updateWithLock(shopId, warehouseTypeId, entry.getKey(), (-1) * entry.getValue());
            }
        }
    }

    /*
    Tính % km cho từng sản phẩm
    totalAmount là tổng tiền trước thuế / sau thuế
    disAmount là tiền giảm giá trước thuế / sau thuế
    totalAmount và disAmount cùng loại trước / sau
     */
    private double calPercent(Double totalAmount, Double disAmount){
        if (totalAmount == null || totalAmount == 0 || disAmount == null || disAmount == 0)
            return 0;
        return ((disAmount / totalAmount) * 100);
    }

    private Float convertToFloat(Double doubleValue) {
        return doubleValue == null ? null : doubleValue.floatValue();
    }

    /*
    Tạo số đơn mua hàng
     */
    public String createOrderNumber(ShopDTO shop){
        LocalDateTime now = DateUtils.convertDateToLocalDateTime(new Date());
        int day = now.getDayOfMonth();
        int month = now.getMonthValue();
        LocalDateTime start =  DateUtils.convertFromDate(now);
        LocalDateTime end =  DateUtils.convertToDate(now);
        List<SaleOrder> saleOrders = repository.getLastSaleOrderNumber(shop.getId(), start);

        int STT = 1;
        if(!saleOrders.isEmpty()) {
            String str = saleOrders.get(0).getOrderNumber();
            String numberString = str.substring(str.length() - 5);
            STT = Integer.valueOf(numberString) + 1;
        }

        String  year = Integer.toString(now.getYear()).substring(2);

        return  "SAL." +  shop.getShopCode() + year + Integer.toString(month + 100).substring(1)  + Integer.toString(day + 100).substring(1) + Integer.toString(STT + 100000).substring(1);
    }

    /*
    kiểm tra sản phẩm có được km
     */
    private boolean checkProductInPromotion(List<SalePromotionDTO> lstSalePromotions, Long promotionId, Long productId, int qty){
        if (productId != null && promotionId != null && lstSalePromotions != null) {
            for (SalePromotionDTO dbPro : lstSalePromotions) {
                if (promotionId.equals(dbPro.getProgramId())) {
                    if (dbPro.getProducts() == null && dbPro.getAmount() == null) return true;
                    if (dbPro.getProducts() != null){
                        for (FreeProductDTO item : dbPro.getProducts()){
                            if (productId.equals(item.getProductId()) && item.getQuantityMax() != null && item.getQuantityMax() >= qty)
                                return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    // get product combo
    private List<SaleOrderComboDetail> createSaleOrderComboDetail(List<SaleOrderDetail> products, List<ComboProductDetailDTO> combos, List<Price> productPrices) {
        List<SaleOrderComboDetail> listOrderComboDetail = new ArrayList<>();
        if (products == null || products.isEmpty() || combos == null || combos.isEmpty()) return  listOrderComboDetail;

//        List<Price> productPrices = priceRepository.findProductPriceWithType(combos.stream().map(item -> item.getProductId()).distinct().collect(Collectors.toList()),
//                customerTypeId, DateUtils.convertToDate(LocalDateTime.now()));

        for (SaleOrderDetail item : products) {
            double amountInTax = 0;
            double amountExTax = 0;
            for (ComboProductDetailDTO detail : combos) {
                if(item.getProductId().equals(detail.getRefProductId())) {
                    if(detail.getFactor() == null) detail.setFactor(0);
                    Price price = getPrice(productPrices, detail.getProductId());
                    amountInTax += price.getPrice() * detail.getFactor() * item.getQuantity();
                    amountExTax += price.getPriceNotVat() * detail.getFactor() * item.getQuantity();
                }
            }
            double percentZM = 0;
            double percentZMInTax = 0;
            double percentZMExTax = 0;
            double percentZV = 0;
            double percentZVInTax = 0;
            double percentZVExTax = 0;
            if (item.getZmPromotion() != null && item.getZmPromotion() > 0){
                percentZM = calPercent(amountInTax, item.getZmPromotion());
                percentZMInTax = calPercent(amountInTax, item.getZmPromotionVat());
                percentZMExTax = calPercent(amountExTax, item.getZmPromotionNotVat());
            }
            if (item.getAutoPromotion() != null && item.getAutoPromotion() > 0){
                percentZV = calPercent(amountInTax, item.getAutoPromotion());
                percentZVInTax = calPercent(amountInTax, item.getAutoPromotionVat());
                percentZVExTax = calPercent(amountExTax, item.getAutoPromotionNotVat());
            }

            for (ComboProductDetailDTO detail : combos) {
                if(item.getProductId().equals(detail.getRefProductId())) {
                    if(detail.getFactor() == null) detail.setFactor(0);
                    SaleOrderComboDetail orderComboDetail = new SaleOrderComboDetail();
                    orderComboDetail.setComboProductId(detail.getComboProductId());
                    if(item.getQuantity() == null) item.setQuantity(0);
                    orderComboDetail.setComboQuantity(item.getQuantity());
                    orderComboDetail.setQuantity(item.getQuantity() * detail.getFactor());
                    orderComboDetail.setProductId(detail.getProductId());
                    Price price = getPrice(productPrices, detail.getProductId());
                    orderComboDetail.setPrice(price.getPrice());
                    orderComboDetail.setPriceNotVat(price.getPriceNotVat());
                    orderComboDetail.setAmount(roundValue(orderComboDetail.getPrice() * orderComboDetail.getQuantity()));
                    orderComboDetail.setPromotionCode(item.getPromotionCode());
                    orderComboDetail.setPromotionName(item.getPromotionName());
                    orderComboDetail.setIsFreeItem(item.getIsFreeItem());
                    orderComboDetail.setZmPromotion(roundValue((orderComboDetail.getPrice() * detail.getFactor() * item.getQuantity()) * percentZM / 100));
                    orderComboDetail.setZmPromotionVat(roundValue((orderComboDetail.getPrice() * detail.getFactor() * item.getQuantity()) * percentZMInTax / 100));
                    orderComboDetail.setZmPromotionNotVat(roundValue((orderComboDetail.getPriceNotVat() * detail.getFactor() * item.getQuantity()) * percentZMExTax / 100));
                    orderComboDetail.setAutoPromotion(roundValue((orderComboDetail.getPrice() * detail.getFactor() * item.getQuantity()) * percentZV / 100));
                    orderComboDetail.setAutoPromotionVat(roundValue((orderComboDetail.getPrice() * detail.getFactor() * item.getQuantity()) * percentZVInTax / 100));
                    orderComboDetail.setAutoPromotionNotVat(roundValue((orderComboDetail.getPriceNotVat() * detail.getFactor() * item.getQuantity()) * percentZVExTax / 100));
                    orderComboDetail.setTotal(roundValue(orderComboDetail.getAmount() - (orderComboDetail.getZmPromotionVat() + orderComboDetail.getAutoPromotionVat())));

                    listOrderComboDetail.add(orderComboDetail);
                }
            }
        }

        return listOrderComboDetail;
    }

    // create combo discount SaleOrderDiscount
    private List<SaleOrderComboDiscount> createSaleOrderComboDiscount(List<SaleOrderDiscount> orderDiscounts, List<ComboProductDetailDTO> combos, List<Price> productPrices ) {
        List<SaleOrderComboDiscount> lstComboDiscount = new ArrayList<>();
        if (orderDiscounts == null) return  lstComboDiscount;

        for(SaleOrderDiscount orderDiscount : orderDiscounts){
            double amountInTax = 0;
            double amountExTax = 0;
            for (ComboProductDetailDTO detail : combos) {
                if(orderDiscount.getProductId().equals(detail.getRefProductId())) {
                    if(detail.getFactor() == null) detail.setFactor(0);
                    Price price = getPrice(productPrices, detail.getProductId());
                    amountInTax += price.getPrice() * detail.getFactor();
                    amountExTax += price.getPriceNotVat() * detail.getFactor();
                }
            }
            double percent = 0;
            double percentInTax = 0;
            double percentExTax = 0;
            if (orderDiscount.getDiscountAmount() != null && orderDiscount.getDiscountAmount() > 0){
                percent = calPercent(amountInTax, orderDiscount.getDiscountAmount().doubleValue());
                percentInTax = calPercent(amountInTax, orderDiscount.getDiscountAmountVat().doubleValue());
                percentExTax = calPercent(amountExTax, orderDiscount.getDiscountAmountNotVat().doubleValue());
            }

            for (ComboProductDetailDTO detail : combos) {
                if (orderDiscount.getProductId().equals(detail.getRefProductId())) {
                    SaleOrderComboDiscount comboDiscount = new SaleOrderComboDiscount();
                    Price price = getPrice(productPrices, detail.getProductId());
                    comboDiscount.setComboProductId(detail.getComboProductId());
                    comboDiscount.setPromotionCode(orderDiscount.getPromotionCode());
                    comboDiscount.setPromotionProgramId(orderDiscount.getPromotionProgramId());
                    comboDiscount.setIsAutoPromotion(orderDiscount.getIsAutoPromotion());
                    comboDiscount.setLevelNumber(orderDiscount.getLevelNumber() == null ? 1 : orderDiscount.getLevelNumber());
                    comboDiscount.setProductId(detail.getProductId());
//                    if(detail.getProductPrice() == null) detail.setProductPrice(0.0);
                    if(detail.getFactor() == null) detail.setFactor(0);
                    comboDiscount.setDiscountAmount(convertToFloat(roundValue((price.getPrice() * detail.getFactor()) * percent / 100)));
                    comboDiscount.setDiscountAmountVat(convertToFloat(roundValue((price.getPrice() * detail.getFactor()) * percentInTax / 100)));
                    comboDiscount.setDiscountAmountNotVat(convertToFloat(roundValue((price.getPriceNotVat() * detail.getFactor()) * percentExTax / 100)));

                    lstComboDiscount.add(comboDiscount);
                }
            }
        }

        return lstComboDiscount;
    }

    private OnlineOrder checkOnlineOrder(SaleOrder saleOrder, SaleOrderRequest request, Long shopId) {
        OnlineOrder onlineOrder = null;
        if (request.getOrderOnlineId() == null && request.getOnlineNumber() != null){
            boolean isManuallyCreatable = shopClient.isManuallyCreatableOnlineOrderV1(shopId).getData();
            if(!isManuallyCreatable)
                throw new ValidateException(ResponseMessage.MANUALLY_CREATABLE_ONLINE_ORDER_NOT_ALLOW);
            onlineOrderService.checkOnlineNumber(request.getOnlineNumber());
            saleOrder.setOnlineSubType(1);
            saleOrder.setOnlineNumber(request.getOnlineNumber());
        }

        if (request.getOrderOnlineId() != null) {
            onlineOrder = onlineOrderRepo.findById(request.getOrderOnlineId())
                    .orElseThrow(() -> new ValidateException(ResponseMessage.ORDER_ONLINE_NOT_FOUND));
            if (onlineOrder.getSynStatus()!=null && onlineOrder.getSynStatus() == 1) throw new ValidateException(ResponseMessage.ORDER_ONLINE_NOT_FOUND);

            List<OnlineOrderDetail> onlineDetails = onlineOrderDetailRepo.findByOnlineOrderId(request.getOrderOnlineId());
            if(!editableOnlineOrder(request.getProducts(), shopId, onlineDetails))
                throw new ValidateException(ResponseMessage.EDITABLE_ONLINE_ORDER_NOT_ALLOW);

            this.onlineSubType(request.getProducts(), saleOrder, onlineDetails);
            saleOrder.setOnlineNumber(onlineOrder.getOrderNumber());
        }
        return onlineOrder;
    }

    private boolean editableOnlineOrder(List<ProductOrderRequest> lstProductOrder, Long shopId, List<OnlineOrderDetail> onlineDetails) {
        boolean isEditable = shopClient.isEditableOnlineOrderV1(shopId).getData();
        if (!isEditable) {
            if (onlineDetails.size() == lstProductOrder.size()) {
                for (OnlineOrderDetail productOld : onlineDetails) {
                    boolean productExits = false;
                    for (ProductOrderRequest productOrder : lstProductOrder) {
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

    private void onlineSubType(List<ProductOrderRequest> lstProductOrder, SaleOrder saleOrder, List<OnlineOrderDetail> onlineDetails) {
        saleOrder.setOnlineSubType(2);
        if (onlineDetails.size() == lstProductOrder.size()) {
            for (OnlineOrderDetail productOld : onlineDetails) {
                boolean productExits = false;
                for (ProductOrderRequest productOrder : lstProductOrder) {
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
        }else{
            saleOrder.setOnlineSubType(3);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCustomer(SaleOrder saleOrder, CustomerDTO customer, boolean saleOrderReturn) {
        int quantity = 1;
        Double customerPurchase = saleOrder.getCustomerPurchase()!=null?saleOrder.getCustomerPurchase():0.0;
        Double totalBillCus = customer.getTotalBill()!=null?customer.getTotalBill():0;
        Integer dayOrderNumber = customer.getDayOrderNumber()!=null?customer.getDayOrderNumber():0;
        Integer monthOrderNumber = customer.getMonthOrderNumber()!=null?customer.getMonthOrderNumber():0;
        Double dayOrderAmount = customer.getDayOrderAmount()!=null?customer.getDayOrderAmount():0.0;
        Double monthOrderAmount = customer.getMonthOrderAmount()!=null?customer.getMonthOrderAmount():0.0;

        if(saleOrderReturn) {
            customerPurchase = -customerPurchase;
            quantity = -1;
        }

        CustomerRequest customerRequest = modelMapper.map(customer, CustomerRequest.class);
            customerRequest.setTotalBill(totalBillCus + customerPurchase);
            customerRequest.setDayOrderNumber(dayOrderNumber + quantity);
            customerRequest.setDayOrderAmount(dayOrderAmount + saleOrder.getAmount());
            customerRequest.setMonthOrderNumber(monthOrderNumber + quantity);
            customerRequest.setMonthOrderAmount(monthOrderAmount + saleOrder.getAmount());
        customerClient.updateFeignV1(customerRequest.getId(), customerRequest);
    }


    public static String withLargeIntegers(Integer value) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(value);
    }

}

