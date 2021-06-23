package vn.viettel.sale.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.customer.RptCusMemAmountDTO;
import vn.viettel.core.dto.promotion.PromotionProgramDTO;
import vn.viettel.core.dto.promotion.PromotionProgramDiscountDTO;
import vn.viettel.core.dto.promotion.PromotionShopMapDTO;
import vn.viettel.core.dto.promotion.RPT_ZV23DTO;
import vn.viettel.core.dto.voucher.VoucherDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.CustomerRequest;
import vn.viettel.core.messaging.PromotionProductRequest;
import vn.viettel.core.messaging.RPT_ZV23Request;
import vn.viettel.core.messaging.RptCusMemAmountRequest;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.util.StringUtils;
import vn.viettel.sale.entities.*;
import vn.viettel.sale.messaging.*;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.OnlineOrderService;
import vn.viettel.sale.service.SaleOrderService;
import vn.viettel.sale.service.SalePromotionService;
import vn.viettel.sale.service.SaleService;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.service.feign.CustomerTypeClient;
import vn.viettel.sale.service.feign.PromotionClient;
import vn.viettel.sale.service.feign.ShopClient;

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

    private static final double VAT = 0.1;

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
        if (SaleOrderType.getValueOf(request.getOrderType()) == null)
            throw new ValidateException(ResponseMessage.SALE_ORDER_TYPE_NOT_EXIST);

        //check warehouse
        Long warehouseTypeId = customerTypeClient.getWarehouseTypeIdByCustomer(shopId).getData();
        if (warehouseTypeId == null)
            throw new ValidateException(ResponseMessage.WARE_HOUSE_NOT_EXIST);

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
            List<PromotionProductRequest> products = lstProductOrder.stream()
                    .map(item -> new PromotionProductRequest(item.getProductId(), item.getQuantity())).collect(Collectors.toList());

            discountNeedSave = promotionClient.getPromotionDiscountV1(request.getDiscountCode(), customer.getId(), products).getData();
            if (discountNeedSave == null)
                throw new ValidateException(ResponseMessage.PROMOTION_IN_USE, "");
            if (!request.getDiscountAmount().equals(discountNeedSave.getDiscountAmount()))
                throw new ValidateException(ResponseMessage.PROMOTION_AMOUNT_NOTEQUALS);

            discountNeedSave.setIsUsed(1);
            discountNeedSave.setOrderCustomerCode(customer.getCustomerCode());
            discountNeedSave.setOrderShopCode(shop.getShopCode());
            discountNeedSave.setActualDiscountAmount(request.getDiscountAmount());
        }

        //sanh sách id sản phẩm theo số lượng mua và km
        HashMap<Long, Integer> mapProductWithQty = new HashMap<>();
        boolean isReturn = true;
        double customerPurchase = 0;
        List<Long> productNotAccumulated = promotionClient.getProductsNotAccumulatedV1(new ArrayList<>(mapProductOrder.keySet())).getData();
        List<Price> productPrices = priceRepository.findProductPrice(lstProductOrder.stream().map(i -> i.getProductId()).collect(Collectors.toList()),
                customer.getCustomerTypeId(), LocalDateTime.now());

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
                Price productPrice = null;
                for(Price price : productPrices){
                    if(price.getProductId() == item.getProductId()) {
                        productPrice = price;
                        break;
                    }
                }
                if (productPrice == null)
                    throw new ValidateException(ResponseMessage.NO_PRICE_APPLIED);
                if (productPrice.getPrice() == null) productPrice.setPrice(0.0);
                if (productPrice.getPriceNotVat() == null) productPrice.setPriceNotVat(0.0);

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

                saleOrderDetails.add(saleOrderDetail);
            }
        }

        // 4. voucher
        double voucherAmount = 0;
        double autoPromtion = 0;
        double autoPromtionExVat = 0;
        double autoPromtionInVat = 0;
        double zmPromotion = 0;
        double promotionExVat = 0;
        Double zv23Amoount = null;
        if(request.getVouchers() != null){
            VoucherDTO voucher = null;
            for (OrderVoucherRequest orderVoucher : request.getVouchers() ) {
                if (orderVoucher.getVoucherId() != null)
                    voucher = promotionClient.getVouchersV1(orderVoucher.getVoucherId()).getData();

                if (voucher == null || (voucher != null && (voucher.getIsUsed() || voucher.getPrice().compareTo(orderVoucher.getVoucherAmount()) != 0 )))
                    throw new ValidateException(ResponseMessage.VOUCHER_DOES_NOT_EXISTS);

                if (voucher.getPrice() != null) voucherAmount += voucher.getPrice();

                if (voucher != null) {
                    voucher.setOrderShopCode(shop.getShopCode());
                    voucher.setIsUsed(true);
                    voucher.setPriceUsed(orderVoucher.getVoucherAmount());
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
            SalePromotionCalculationDTO calculationDTO = salePromotionService.getSaleItemPromotions(orderRequest, shopId, true);
            if (calculationDTO == null)
                throw new ValidateException(ResponseMessage.PROMOTION_IN_USE, "");

            List<SalePromotionDTO> lstSalePromotions = calculationDTO.getLstSalePromotions();

            List<Long> dbPromotionIds = lstSalePromotions.stream().map(item -> item.getProgramId()).collect(Collectors.toList());
            // danh sách km tiền -> dùng kiểm tra tổng tiền km có đúng
            List<SalePromotionCalItemRequest> promotionInfo = new ArrayList<>();

            for (SalePromotionDTO inputPro : request.getPromotionInfo()){
                if (dbPromotionIds.contains(inputPro.getProgramId())){      // kiểm tra ctkm còn được sử dụng
                    SalePromotionDTO dbPro = new SalePromotionDTO();
                    for (SalePromotionDTO dbP : lstSalePromotions){
                        if(dbP.getProgramId().equals(inputPro.getProgramId())){
                            dbPro = dbP;
                            break;
                        }
                    }

                    if("zv23".equalsIgnoreCase(dbPro.getProgramType())){
                        zv23Amoount = dbPro.getZv23Amount();
                    }

                    if (dbPro.getIsReturn() != null && !dbPro.getIsReturn()) isReturn = false;

                    // tổng số lượng sản phẩm khuyến mãi
                    if(inputPro.getProducts()!=null){
                        int totalQty = 0;
                        for(FreeProductDTO product: inputPro.getProducts()){
                            totalQty += product.getQuantity();
                        }
                        inputPro.setTotalQty(totalQty);
                    }
                    //kiểm tra đã đủ số xuất

                    if (!salePromotionService.checkPromotionLimit(inputPro, shopId))
                        throw new ValidateException(ResponseMessage.PROMOTION_NOT_ENOUGH_VALUE, inputPro.getPromotionProgramName());

                    PromotionShopMapDTO promotionShopMap = promotionClient.getPromotionShopMapV1(inputPro.getProgramId(), shopId).getData();
                    // kiểm tra tồn kho có đủ
                    if (inputPro.getProducts() != null && !inputPro.getProducts().isEmpty()){
                        List<Long> productIds = inputPro.getProducts().stream().map(item -> item.getProductId()).collect(Collectors.toList());
                        List<ComboProductDetailDTO> combos = comboProductRepository.findComboProduct(customer.getCustomerTypeId(), productIds);

                        for (FreeProductDTO ipP : inputPro.getProducts()){
                            if (ipP.getQuantity() == null) ipP.setQuantity(0);
                            if (checkProductInPromotion(lstSalePromotions, inputPro.getProgramId(), ipP.getProductId(), ipP.getQuantity())){

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
                                saleOrderDetail.setShopId(shopId);
                                saleOrderDetail.setAmount(0.0);
                                saleOrderDetail.setTotal(0.0);
                                saleOrderDetail.setPromotionCode(inputPro.getPromotionProgramCode());
                                saleOrderDetail.setPromotionName(inputPro.getPromotionProgramName());
                                saleOrderDetail.setLevelNumber(ipP.getLevelNumber());
//                                if("zm".equalsIgnoreCase(dbPro.getProgramType())){
//                                    saleOrderDetail.setZmPromotion();
//                                    saleOrderDetail.setZmPromotionNotVat();
//                                    saleOrderDetail.setZmPromotionVat();
//                                }else{
//                                    saleOrderDetail.setAutoPromotion();
//                                    saleOrderDetail.setAutoPromotionNotVat();
//                                    saleOrderDetail.setAutoPromotionVat();
//                                }

                                saleOrderDetails.add(saleOrderDetail);

                                if (inputPro.getTotalQty() != null){
                                    promotionShopMap.setQuantityMax(promotionShopMap.getQuantityMax() - inputPro.getTotalQty());
                                    promotionShopMaps.add(promotionShopMap);
                                }

                                //get combo
                                for(ComboProductDetailDTO combo : combos){
                                    if(ipP.getProductId().equals(combo.getRefProductId()) && combo.getFactor() != null) {
                                        SaleOrderComboDetail orderComboDetail = new SaleOrderComboDetail();
                                        orderComboDetail.setComboProductId(combo.getComboProductId());
                                        orderComboDetail.setComboQuantity(ipP.getQuantity());
                                        orderComboDetail.setQuantity(ipP.getQuantity() * combo.getFactor());
                                        orderComboDetail.setProductId(combo.getProductId());
                                        orderComboDetail.setPrice(0.0);
                                        orderComboDetail.setPriceNotVat(0.0);
                                        orderComboDetail.setAmount(0.0);
                                        orderComboDetail.setTotal(0.0);
                                        orderComboDetail.setIsFreeItem(false);
                                        orderComboDetail.setPromotionCode(inputPro.getPromotionProgramCode());
//                                        orderComboDetail.setAutoPromotion();
//                                        orderComboDetail.setAutoPromotionNotVat();
//                                        orderComboDetail.setAutoPromotionVat();
//                                        orderComboDetail.setZmPromotion();
//                                        orderComboDetail.setZmPromotionNotVat();
//                                        orderComboDetail.setZmPromotionVat();
                                        orderComboDetail.setLevelNumber(ipP.getLevelNumber());

                                        listOrderComboDetails.add(orderComboDetail);
                                    }
                                }
                            }else{
                                throw new ValidateException(ResponseMessage.PRODUCT_NOT_IN_PROMOTION, ipP.getProductCode(), inputPro.getPromotionProgramName());
                            }
                        }
                    }else if (inputPro.getAmount() != null){
                        promotionExVat += inputPro.getTotalAmtExTax() == null ? 0 : inputPro.getTotalAmtExTax();
                        if("zm".equalsIgnoreCase(dbPro.getProgramType())){
                            zmPromotion += inputPro.getAmount().getAmount() == null ? 0 : inputPro.getAmount().getAmount();
                        }else{
                            autoPromtion += inputPro.getAmount().getAmount() == null ? 0 : inputPro.getAmount().getAmount();
                            autoPromtionExVat += inputPro.getTotalAmtExTax() == null ? 0 : inputPro.getTotalAmtExTax();
                            autoPromtionInVat += inputPro.getTotalAmtInTax() == null ? 0 : inputPro.getTotalAmtInTax();
                        }
                        SalePromotionCalItemRequest sPP = new SalePromotionCalItemRequest();
                        if(inputPro.getAmount()!=null && dbPro.getAmount()!=null)
                            inputPro.getAmount().setPercentage(dbPro.getAmount().getPercentage());
                        sPP.setAmount(inputPro.getAmount());
                        sPP.setPromotionType(inputPro.getPromotionType());
                        sPP.setProgramId(inputPro.getProgramId());
                        promotionInfo.add(sPP);

                        if (dbPro.getAmount().getDiscountInfo() != null){
                            for (SaleDiscountSaveDTO item : dbPro.getAmount().getDiscountInfo()){
                                //tạo sale discount
                                SaleOrderDiscount saleOrderDiscount = new SaleOrderDiscount();
                                saleOrderDiscount.setPromotionProgramId(inputPro.getProgramId());
                                saleOrderDiscount.setPromotionCode(inputPro.getPromotionProgramCode());
                                saleOrderDiscount.setPromotionName(inputPro.getPromotionProgramName());
                                saleOrderDiscount.setPromotionType(inputPro.getProgramType());
                                saleOrderDiscount.setIsAutoPromotion(inputPro.getPromotionType() == 0 ? true : false);
                                saleOrderDiscount.setLevelNumber(item.getLevelNumber());
                                saleOrderDiscount.setDiscountAmount(convertToFloat(item.getAmount()));
                                saleOrderDiscount.setDiscountAmountNotVat(convertToFloat(item.getAmountExTax()));
                                saleOrderDiscount.setDiscountAmountVat(convertToFloat(item.getAmountInTax()));
                                saleOrderDiscount.setMaxDiscountAmount(convertToFloat(item.getMaxAmount()));
                                saleOrderDiscount.setProductId(item.getProductId());
                                saleOrderDiscounts.add(saleOrderDiscount);

                                //update buying product
                                for(SaleOrderDetail buyP : saleOrderDetails){
                                    if(buyP.getProductId().equals(item.getProductId()) && !buyP.getIsFreeItem()){
                                        if(!productNotAccumulated.contains(buyP.getProductId()))
                                            customerPurchase -= item.getAmountInTax();

                                        if("zm".equalsIgnoreCase(dbPro.getProgramType())){
                                            buyP.setZmPromotion((buyP.getZmPromotion() == null? 0 : buyP.getZmPromotion()) + item.getAmount());
                                            buyP.setZmPromotionVat((buyP.getZmPromotionVat() == null? 0 : buyP.getZmPromotionVat()) + item.getAmountInTax());
                                            buyP.setZmPromotionNotVat((buyP.getZmPromotionNotVat() == null? 0 : buyP.getZmPromotionNotVat()) + item.getAmountExTax());
                                        }else{
                                            buyP.setAutoPromotion((buyP.getAutoPromotion() == null? 0 : buyP.getAutoPromotion()) + item.getAmount());
                                            buyP.setAutoPromotionVat((buyP.getAutoPromotionVat() == null? 0 : buyP.getAutoPromotionVat()) + item.getAmountInTax());
                                            buyP.setAutoPromotionNotVat((buyP.getAutoPromotionNotVat() == null? 0 : buyP.getAutoPromotionNotVat()) + item.getAmountExTax());
                                        }
                                        double disAmt = 0;
                                        if(buyP.getAutoPromotionVat() != null) disAmt = buyP.getAutoPromotionVat();
                                        if(buyP.getZmPromotionVat() != null) disAmt += buyP.getZmPromotionVat();
                                        buyP.setTotal(buyP.getAmount() - disAmt);
                                    }
                                }
                            }
                        }

                        if(inputPro.getTotalAmtInTax() != null){
                            promotionShopMap.setAmountMax(promotionShopMap.getAmountMax() - inputPro.getTotalAmtInTax());
                            promotionShopMaps.add(promotionShopMap);
                        }
                    }

                    //update buying product
                    if(inputPro.getLstProductId() != null) {
                        for (SaleOrderDetail buyP : saleOrderDetails) {
                            if (inputPro.getLstProductId().contains(buyP.getProductId()) && !buyP.getIsFreeItem()) {
                                if (buyP.getPromotionCode() == null) {
                                    buyP.setPromotionCode(inputPro.getPromotionProgramCode());
                                    buyP.setPromotionName(inputPro.getPromotionProgramName());
                                } else {
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

            List<Long> productIds = saleOrderDetails.stream().map(item -> item.getProductId()).collect(Collectors.toList());
            List<ComboProductDetailDTO> combos = comboProductRepository.findComboProduct(customer.getCustomerTypeId(), productIds);
            createSaleOrderComboDetail(saleOrderDetails, request.getPromotionInfo(), combos, lstSalePromotions).stream().forEachOrdered(listOrderComboDetails::add);
            createSaleOrderComboDiscount(saleOrderDetails, request.getPromotionInfo(), combos, lstSalePromotions).stream().forEachOrdered(listOrderComboDiscounts::add);

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
        }

        //kiểm tra xem tổng sản phẩm mua + km có vượt quá tôn kho
        List<FreeProductDTO> freeProductDTOs = productRepository.findFreeProductDTONoOrders(shopId, warehouseTypeId, new ArrayList<>(mapProductWithQty.keySet()));
        for (FreeProductDTO freeProductDTO : freeProductDTOs){
            if(freeProductDTO == null || (freeProductDTO.getStockQuantity() != null && freeProductDTO.getStockQuantity() < mapProductWithQty.get(freeProductDTO.getProductId())))
                throw new ValidateException(ResponseMessage.PRODUCT_OUT_OF_STOCK, freeProductDTO.getProductCode() + " - " + freeProductDTO.getProductName(), freeProductDTO.getStockQuantity() + "");
        }

        // 5. kiểm tra tiền tích lũy
        if ((customer.getAmountCumulated()!=null &&  request.getAccumulatedAmount()!=null && customer.getAmountCumulated() < request.getAccumulatedAmount()))
            throw new ValidateException(ResponseMessage.ACCUMULATED_AMOUNT_OVER);

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        if(saleOrderDetails.isEmpty())
            throw new ValidateException(ResponseMessage.PLEASE_IMPORT_PRODUCTS);

        SaleOrder saleOrder = modelMapper.map(request, SaleOrder.class);
        saleOrder.setOrderNumber(createOrderNumber(shop.getShopCode()));
        saleOrder.setOrderDate(LocalDateTime.now());
        saleOrder.setShopId(shopId);
        saleOrder.setSalemanId(userId);
        saleOrder.setCustomerId(customer.getId());
        saleOrder.setWareHouseTypeId(warehouseTypeId);
        saleOrder.setAmount(request.getTotalOrderAmount());
        saleOrder.setTotalPromotion(request.getPromotionAmount());
        saleOrder.setTotalPromotionNotVat(promotionExVat);
        saleOrder.setTotalVoucher(voucherAmount);
        saleOrder.setPaymentType(request.getPaymentType());
        saleOrder.setDeliveryType(request.getDeliveryType());
        saleOrder.setOrderType(request.getOrderType());
        saleOrder.setAutoPromotion(autoPromtion);
        saleOrder.setAutoPromotionNotVat(autoPromtionExVat);
        saleOrder.setAutoPromotionVat(autoPromtionInVat);
        saleOrder.setZmPromotion(zmPromotion);
        //tiền mua hàng sau chiết khấu, và không tính những sp không được tích luỹ
        saleOrder.setCustomerPurchase(customerPurchase);
        saleOrder.setDiscountCodeAmount(request.getDiscountAmount());
        saleOrder.setTotalPaid(request.getPaymentAmount());
        saleOrder.setTotal(request.getRemainAmount());
        saleOrder.setBalance(request.getExtraAmount());
        saleOrder.setMemberCardAmount(request.getAccumulatedAmount());
        saleOrder.setUsedRedInvoice(false);
        saleOrder.setNote(request.getNote());
        saleOrder.setType(1);
        saleOrder.setTotalCustomerPurchase(customer.getAmountCumulated());
        saleOrder.setIsReturn(isReturn);
        if(saleOrder.getTotalPaid() < 1 && saleOrder.getMemberCardAmount() != null && saleOrder.getMemberCardAmount() > 0) {
            double amountDisTotal = 0;
            // trừ tiền khuyến mãi
            if (saleOrder.getDiscountCodeAmount() != null) {
                amountDisTotal += saleOrder.getDiscountCodeAmount();
            }

            // trừ tiền giảm giá
            if (saleOrder.getTotalPromotion() != null) {
                amountDisTotal += saleOrder.getTotalPromotion();
            }
            if (saleOrder.getTotalVoucher() != null) {
                amountDisTotal += saleOrder.getTotalVoucher();
            }

            // trừ tiền tích lũy
            if ((saleOrder.getAmount() - amountDisTotal) < request.getAccumulatedAmount()) {
                saleOrder.setTotalCustomerPurchase(saleOrder.getAmount() - amountDisTotal);
            }
        }

        if (request.getOrderOnlineId() != null || (request.getOnlineNumber() != null && !request.getOnlineNumber().trim().isEmpty()))
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

        this.updateStockTotal(mapProductWithQty, shopId, warehouseTypeId );

        //update doanh số tích lũy và tiền tích lũy cho customer
        updateCustomerTotalBill(customerPurchase, customer);

        //update AccumulatedAmount (bảng RPT_CUS_MEM_AMOUNT) (tiền tích lũy) = tiền tích lũy hiện tại - saleOrder.getMemberCardAmount()
        updateAccumulatedAmount(saleOrder.getMemberCardAmount(), customer.getId(), shopId);
        // update RPT_ZV23: nếu có km zv23

        if (request.getPromotionInfo() != null && !request.getPromotionInfo().isEmpty()) {
            for (SalePromotionDTO inputPro : request.getPromotionInfo()) {
                if ("zv23".equalsIgnoreCase(inputPro.getProgramType())) this.updateRPTZV23(inputPro, customer, shopId);
            }
        }

        return saleOrder.getId();
    }

    public void updateRPTZV23(SalePromotionDTO inputPro, CustomerDTO customer, Long shopId) {
        RPT_ZV23DTO rpt_zv23DTO = promotionClient.checkZV23RequireV1(inputPro.getProgramId(), customer.getId(), shopId).getData();
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

    public void updateCustomerTotalBill(Double customerPurchase, CustomerDTO customer) {
        if (customerPurchase == null) return;
        CustomerRequest customerRequest = modelMapper.map(customer, CustomerRequest.class);
        double totalBillCus = customerRequest.getTotalBill()!=null?customerRequest.getTotalBill():0;
        customerRequest.setTotalBill(totalBillCus + customerPurchase);
        customerRequest.setLastOrderDate(LocalDateTime.now());
        customerClient.updateFeignV1(customerRequest.getId(), customerRequest);
    }

    public void updateAccumulatedAmount(Double accumulatedAmount, Long customerId, Long shopId) {
        if (accumulatedAmount == null) return;
        RptCusMemAmountDTO rptDTO = customerClient.getRptCusV1(customerId, shopId).getData();
        if(rptDTO!=null) {
            double amount = rptDTO.getAmount()!=null?rptDTO.getAmount():0;
            if(accumulatedAmount > amount) throw new ValidateException(ResponseMessage.RPT_CUST_MEM_AMOUNT_AMOUNT_INVALID);
            RptCusMemAmountRequest request = new RptCusMemAmountRequest();
            request.setAmount(amount - accumulatedAmount);
            customerClient.updateRptCusV1(rptDTO.getId(), request);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateStockTotal( Map<Long, Integer> productTotalMaps, Long shopId, Long warehouseTypeId) {
        List<StockTotal> stockTotals = stockTotalRepository.getStockTotal(shopId, warehouseTypeId, new ArrayList<>(productTotalMaps.keySet()));
        if(stockTotals != null) {
            for(StockTotal stockTotal : stockTotals) {
                stockTotal.setQuantity(stockTotal.getQuantity() - productTotalMaps.get(stockTotal.getProductId()));
                stockTotalRepository.save(stockTotal);
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

    // todo Son
    /*
    Tạo số đơn mua hàng
     */
    private String createOrderNumber(String shopCode){
        int STT = repository.countSaleOrder() + 1;
        LocalDateTime now = DateUtils.convertDateToLocalDateTime(new Date());
        int day = now.getDayOfMonth();
        int month = now.getMonthValue();
        String  year = Integer.toString(now.getYear()).substring(2);
        return  "SAL." +  shopCode + "." + year + Integer.toString(month + 100).substring(1)  + Integer.toString(day + 100).substring(1) + Integer.toString(STT + 10000).substring(1);
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
    private List<SaleOrderComboDetail> createSaleOrderComboDetail(List<SaleOrderDetail> products, List<SalePromotionDTO> discountInfo, List<ComboProductDetailDTO> combos, List<SalePromotionDTO> forSaving) {
        List<SaleOrderComboDetail> listOrderComboDetail = new ArrayList<>();
        if (products == null) return  listOrderComboDetail;

        for (ComboProductDetailDTO detail : combos) {
            for (SaleOrderDetail item : products) {
                if(!item.getIsFreeItem() && item.getProductId().equals(detail.getRefProductId()) && item.getQuantity() != null && item.getQuantity() > 0 && detail.getFactor() != null) {
                    SaleOrderComboDetail orderComboDetail = new SaleOrderComboDetail();
                    orderComboDetail.setComboProductId(detail.getComboProductId());
                    orderComboDetail.setComboQuantity(item.getQuantity());
                    orderComboDetail.setQuantity(item.getQuantity() * detail.getFactor());
                    orderComboDetail.setProductId(detail.getProductId());
                    orderComboDetail.setPrice(detail.getProductPrice());
                    orderComboDetail.setPriceNotVat(detail.getProductPriceNotVat());
                    orderComboDetail.setAmount(orderComboDetail.getPrice() * orderComboDetail.getQuantity());

                    orderComboDetail.setIsFreeItem(false);
                    if(discountInfo != null && forSaving != null){
                        for(SalePromotionDTO inputPro : discountInfo){
                            SalePromotionDTO dbPro = new SalePromotionDTO();
                            for (SalePromotionDTO dbP : forSaving){
                                if(dbP.getProgramId().equals(inputPro.getProgramId())){
                                    dbPro = dbP;
                                    break;
                                }
                            }

                            if (dbPro.getAmount() != null && dbPro.getAmount().getDiscountInfo() != null){
                                for (SaleDiscountSaveDTO item1 : dbPro.getAmount().getDiscountInfo()){
                                    if(item1.getProductId().equals(item.getProductId())){
                                        double percent = 0;
                                        double amountInTax = 0;
                                        double amountEXTax = 0;
                                        double amountDefault = 0;
                                        if(orderComboDetail.getAmount() == null) orderComboDetail.setAmount(0.0);
                                        if(item1.getAmount().equals(item1.getAmountExTax())){
                                            percent = calPercent(item.getPriceNotVat() * item.getQuantity(), item1.getAmount());
                                            amountDefault = ((detail.getProductPriceNotVat() * detail.getFactor() * item.getQuantity()) * percent / 100);
                                            amountEXTax = amountDefault;
                                            amountInTax = ((detail.getProductPrice() * detail.getFactor() * item.getQuantity()) * percent / 100);
                                        }
                                        else{
                                            percent = calPercent(item.getPrice() * item.getQuantity(), item1.getAmount());
                                            amountDefault = ((detail.getProductPrice() * detail.getFactor() * item.getQuantity()) * percent / 100);
                                            amountEXTax = ((detail.getProductPriceNotVat() * detail.getFactor() * item.getQuantity()) * percent / 100);
                                            amountInTax = amountDefault;
                                        }

                                        if("zm".equalsIgnoreCase(inputPro.getProgramType())){
                                            orderComboDetail.setZmPromotion((orderComboDetail.getZmPromotion() == null? 0 : orderComboDetail.getZmPromotion()) + amountDefault);
                                            orderComboDetail.setZmPromotionVat((orderComboDetail.getZmPromotionVat() == null? 0 : orderComboDetail.getZmPromotionVat()) + amountInTax);
                                            orderComboDetail.setZmPromotionNotVat((orderComboDetail.getZmPromotionNotVat() == null? 0 : orderComboDetail.getZmPromotionNotVat()) + amountEXTax);
                                        }else{
                                            orderComboDetail.setAutoPromotion((orderComboDetail.getAutoPromotion() == null? 0 : orderComboDetail.getAutoPromotion()) + amountDefault);
                                            orderComboDetail.setAutoPromotionVat((orderComboDetail.getAutoPromotionVat() == null? 0 : orderComboDetail.getAutoPromotionVat()) + amountInTax);
                                            orderComboDetail.setAutoPromotionNotVat((orderComboDetail.getAutoPromotionNotVat() == null? 0 : orderComboDetail.getAutoPromotionNotVat()) + amountEXTax);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    orderComboDetail.setTotal(orderComboDetail.getAmount() - ((orderComboDetail.getZmPromotion() == null? 0 : orderComboDetail.getZmPromotion()) + (orderComboDetail.getAutoPromotion() == null? 0 : orderComboDetail.getAutoPromotion())));

                    listOrderComboDetail.add(orderComboDetail);
                    break;
                }
            }
        }

        return listOrderComboDetail;
    }

    // create combo discount
    private List<SaleOrderComboDiscount> createSaleOrderComboDiscount(List<SaleOrderDetail> products, List<SalePromotionDTO> discountInfo, List<ComboProductDetailDTO> combos, List<SalePromotionDTO> forSaving) {
        List<SaleOrderComboDiscount> lstComboDiscount = new ArrayList<>();
        if (products == null) return  lstComboDiscount;

        if(discountInfo != null && forSaving != null){
            for(SalePromotionDTO inputPro : discountInfo){
                SalePromotionDTO dbPro = new SalePromotionDTO();
                for (SalePromotionDTO dbP : forSaving){
                    if(dbP.getProgramId().equals(inputPro.getProgramId())){
                        dbPro = dbP;
                        break;
                    }
                }

                if (dbPro.getAmount() != null && dbPro.getAmount().getDiscountInfo() != null){
                    for (SaleDiscountSaveDTO item1 : dbPro.getAmount().getDiscountInfo()){
                        for (ComboProductDetailDTO detail : combos) {
                            SaleOrderDetail saleOrderDetail = null;
                            for (SaleOrderDetail item : products) {
                                if(!item.getIsFreeItem() && item.getProductId().equals(detail.getRefProductId()) && item.getQuantity() != null && item.getQuantity() > 0 && detail.getFactor() != null) {
                                    saleOrderDetail = item;
                                    break;
                                }
                            }

                            if (item1.getProductId().equals(detail.getRefProductId()) && saleOrderDetail != null) {
                                SaleOrderComboDiscount comboDiscount = new SaleOrderComboDiscount();
                                comboDiscount.setComboProductId(detail.getComboProductId());
                                comboDiscount.setPromotionCode(inputPro.getPromotionProgramCode());
                                comboDiscount.setPromotionProgramId(inputPro.getProgramId());
                                comboDiscount.setIsAutoPromotion(inputPro.getPromotionType() == 0 ? true : false);
                                comboDiscount.setLevelNumber(item1.getLevelNumber());
                                comboDiscount.setProductId(detail.getProductId());
                                if(detail.getProductPrice() == null) detail.setProductPrice(0.0);
                                if(detail.getFactor() == null) detail.setFactor(0);

                                double percent = 0;
                                if(item1.getAmount().equals(item1.getAmountExTax())){
                                    percent = calPercent(saleOrderDetail.getPriceNotVat() * saleOrderDetail.getQuantity(), item1.getAmount());
                                    double amount = detail.getProductPriceNotVat() * detail.getFactor() * saleOrderDetail.getQuantity();

                                    comboDiscount.setDiscountAmount(convertToFloat(amount * percent / 100));
                                    comboDiscount.setDiscountAmountNotVat(convertToFloat(amount * percent / 100));
                                    amount = detail.getProductPrice() * detail.getFactor() * saleOrderDetail.getQuantity();
                                    comboDiscount.setDiscountAmountVat(convertToFloat(amount * percent / 100));
                                }
                                else{
                                    percent = calPercent(saleOrderDetail.getPrice() * saleOrderDetail.getQuantity(), item1.getAmount());
                                    double amount = detail.getProductPrice() * detail.getFactor() * saleOrderDetail.getQuantity();

                                    comboDiscount.setDiscountAmount(convertToFloat(amount * percent / 100));
                                    comboDiscount.setDiscountAmountVat(convertToFloat(amount * percent / 100));
                                    amount = detail.getProductPriceNotVat() * detail.getFactor() * saleOrderDetail.getQuantity();
                                    comboDiscount.setDiscountAmountNotVat(convertToFloat(amount * percent / 100));
                                }

                                lstComboDiscount.add(comboDiscount);
                            }
                        }
                    }
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
        }
    }
}

