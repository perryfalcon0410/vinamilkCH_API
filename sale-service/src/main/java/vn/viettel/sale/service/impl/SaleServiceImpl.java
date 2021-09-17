package vn.viettel.sale.service.impl;

import org.hibernate.exception.ConstraintViolationException;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import vn.viettel.core.messaging.CustomerRequest;
import vn.viettel.core.messaging.MemberCustomerRequest;
import vn.viettel.core.messaging.RPT_ZV23Request;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.util.StringUtils;
import vn.viettel.core.utils.JMSType;
import vn.viettel.sale.entities.*;
import vn.viettel.sale.messaging.*;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.*;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.service.feign.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLIntegrityConstraintViolationException;
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
        if ((request.getPromotionInfo() == null || request.getPromotionInfo().isEmpty()) && (request.getProducts() == null || request.getProducts().isEmpty()))
            throw new ValidateException(ResponseMessage.PROMOTION_NOT_FOUND);

        // check existing customer
        CustomerDTO customer = customerClient.getCustomerByIdV1(request.getCustomerId()).getData();
        HashMap<String,List<Long>> syncMap = new HashMap<>();
        if (customer == null)
            throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);

        // check existing shop and order type
        ShopDTO shop = shopClient.getByIdV1(shopId).getData();
        if (shop == null)
            throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);

        // check order type
        ApParamDTO apParamDTO = apparamClient.getApParamByTypeAndvalue(apParamOrderType, request.getOrderType().toString()).getData();
        if(apParamDTO == null) throw new ValidateException(ResponseMessage.AP_PARAM_NOT_EXISTS);

        //check warehouse
        CustomerTypeDTO customerType = customerTypeClient.getCustomerTypeForSale(request.getCustomerId(), shopId);

      /*  if(request.getCustomerId() != null) customerType = customerTypeClient.getCusTypeByCustomerIdV1(request.getCustomerId());
        if(customerType == null) customerType = customerTypeClient.getCusTypeByShopIdV1(shopId);*/
        if (customerType == null) throw new ValidateException(ResponseMessage.WARE_HOUSE_NOT_EXIST);

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
        List<ProductOrderRequest> lstProductOrder = new ArrayList<>();
        HashMap<Long, ProductOrderRequest> mapProductOrder = new HashMap<>();
        if(request.getProducts() != null && !request.getProducts().isEmpty()) {
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
            lstProductOrder = new ArrayList<>(mapProductOrder.values());
            request.setProducts(lstProductOrder);
        }

        //danh sách id sản phẩm theo số lượng mua và km
        HashMap<Long, Integer> mapProductWithQty = new HashMap<>();
        boolean isReturn = true;
        double customerPurchase = 0;
        List<Long> productNotAccumulated = new ArrayList<>();
        List<PromotionProgramDiscountDTO> discountDTOs = new ArrayList<>();

        if(!lstProductOrder.isEmpty()) {
            productNotAccumulated = promotionClient.getProductsNotAccumulatedV1(new ArrayList<>(mapProductOrder.keySet())).getData();
            List<Price> productPrices = priceRepository.findProductPriceWithType(lstProductOrder.stream().map(i -> i.getProductId()).collect(Collectors.toList()),
                    customer.getCustomerTypeId(), DateUtils.convertToDate(LocalDateTime.now()));
            // gán sản phẩm mua vào
            for (ProductOrderRequest item : lstProductOrder){
                if (item.getQuantity() != null && item.getQuantity() > 0) {

                    if (!mapProductWithQty.containsKey(item.getProductId())) {
                        mapProductWithQty.put(item.getProductId(), item.getQuantity());
                    }else{
                        Integer qty = mapProductWithQty.get(item.getProductId()) + item.getQuantity();
                        mapProductWithQty.put(item.getProductId(), qty);
                    }

                    //tạo order detail
                    Price productPrice = getPrice(productPrices, item.getProductId(), false);

                    SaleOrderDetail saleOrderDetail = new SaleOrderDetail();
                    saleOrderDetail.setIsFreeItem(false);
                    saleOrderDetail.setQuantity(item.getQuantity());
                    saleOrderDetail.setPrice(productPrice.getPrice());
                    saleOrderDetail.setPriceNotVat(productPrice.getPriceNotVat());
                    saleOrderDetail.setProductId(item.getProductId());
                    saleOrderDetail.setShopId(shopId);
                    saleOrderDetail.setAmount(saleOrderDetail.getPrice() * saleOrderDetail.getQuantity());
                    saleOrderDetail.setTotal(saleOrderDetail.getAmount());
                    saleOrderDetail.setProductCode(item.getProductCode());
                    saleOrderDetail.setProductName(item.getProductName());

                    saleOrderDetails.add(saleOrderDetail);
                }
            }


        //kiểm tra xem tổng sản phẩm mua có vượt quá tôn kho
            List<FreeProductDTO> freeProductDTOs = productRepository.findProductWithStock(shopId, customerType.getWareHouseTypeId(),
                    new ArrayList<>(mapProductWithQty.keySet()));
            for (FreeProductDTO freeProductDTO : freeProductDTOs){
                if(freeProductDTO == null || (freeProductDTO.getStockQuantity() != null &&
                        freeProductDTO.getStockQuantity() < mapProductWithQty.get(freeProductDTO.getProductId())))
                    throw new ValidateException(ResponseMessage.PRODUCT_OUT_OF_STOCK, freeProductDTO.getProductCode() + " - " +
                            freeProductDTO.getProductName(), this.withLargeIntegers(freeProductDTO.getStockQuantity()) + "");
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

                if (voucher == null || (voucher != null && voucher.getIsUsed() != null  && ( voucher.getIsUsed() || voucher.getPrice().compareTo(orderVoucher.getPrice()) != 0 )))
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

                    if(inputPro.getProgramType().equalsIgnoreCase("ZM") && inputPro.getAmount()!=null && inputPro.getAmount().getAmount()!=null &&
                    inputPro.getAmount().getMaxAmount() !=null && inputPro.getAmount().getMaxAmount() >0 && inputPro.getAmount().getAmount() > inputPro.getAmount().getMaxAmount() && dbPro.getIsUse() == false )
                        throw new ValidateException(ResponseMessage.PROMOTION_NOT_AMOUNT_VALUE, inputPro.getPromotionProgramName());

                    //kiểm tra đã đủ số xuất
                    if(inputPro.getAmount()!=null) {
                        if (!salePromotionService.checkPromotionLimit(dbPro, shopId))
                            throw new ValidateException(ResponseMessage.PROMOTION_NOT_ENOUGH_VALUE, inputPro.getPromotionProgramName());
                    }else {
                        if (!salePromotionService.checkPromotionLimit(inputPro, shopId))
                            throw new ValidateException(ResponseMessage.PROMOTION_NOT_ENOUGH_VALUE, inputPro.getPromotionProgramName());
                    }

                    if (dbPro.getIsReturn() != null && !dbPro.getIsReturn()) isReturn = false;

                    PromotionShopMapDTO promotionShopMap = promotionClient.getPromotionShopMapV1(inputPro.getProgramId(), shopId).getData();
                    // kiểm tra tồn kho có đủ
                    if (inputPro.getProducts() != null && !inputPro.getProducts().isEmpty()){
                        checkFreeProduct(inputPro, dbPro, mapProductWithQty, lstSalePromotions);

                        List<Long> productIds = inputPro.getProducts().stream().map(item -> item.getProductId()).collect(Collectors.toList());
                        List<Price> productPrices1 = priceRepository.findProductPriceWithType(productIds, customer.getCustomerTypeId(), DateUtils.convertToDate(LocalDateTime.now()));

                        for(SaleOrderDetail buyP : saleOrderDetails) {
                            if (dbPro.getLstProductHasPromtion() != null && dbPro.getLstProductHasPromtion().contains(buyP.getProductId()) && !buyP.getIsFreeItem()) {
                                if (buyP.getPromotionCode() == null) {
                                    buyP.setPromotionType(dbPro.getProgramType());
                                    buyP.setPromotionCode(dbPro.getPromotionProgramCode());
                                    buyP.setPromotionName(dbPro.getPromotionProgramName());
                                } else if(!buyP.getPromotionCode().contains(dbPro.getPromotionProgramCode())) {
                                    buyP.setPromotionType(buyP.getPromotionType() + ", " + dbPro.getProgramType());
                                    buyP.setPromotionCode(buyP.getPromotionCode() + ", " + dbPro.getPromotionProgramCode());
                                    buyP.setPromotionName(buyP.getPromotionName() + ", " + dbPro.getPromotionProgramName());
                                }
                            }
                        }

                        for (FreeProductDTO product : inputPro.getProducts()){
                            if (product.getQuantity() != null && product.getQuantity() > 0){
                                //new sale detail
                                SaleOrderDetail saleOrderDetail = createOrderDetail(shopId, inputPro, product);
                                Price price = getPrice(productPrices1, saleOrderDetail.getProductId(), true);
                                if(price!=null) {
                                    saleOrderDetail.setPrice(price.getPrice());
                                    saleOrderDetail.setPriceNotVat(price.getPriceNotVat());
                                }
                                saleOrderDetails.add(saleOrderDetail);

                                if (product.getQuantity() != null){
                                    Double received = promotionShopMap.getQuantityReceived()!=null?promotionShopMap.getQuantityReceived():0;
                                    promotionShopMap.setQuantityReceived(received + product.getQuantity());
                                    promotionShopMaps.add(promotionShopMap);
                                }
                            }
                        }
                    }else if (inputPro.getAmount() != null && dbPro.getAmount() != null && inputPro.getAmount().getAmount() > 0){
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
                        inputPro.getAmount().setPercentage(dbPro.getAmount().getPercentage());
                        promotionInfo.add(createCalItemRequest(inputPro));

                        if (dbPro.getAmount().getDiscountInfo() != null){
                            for (SaleDiscountSaveDTO item : dbPro.getAmount().getDiscountInfo()){
                                //tạo sale discount
                                saleOrderDiscounts.add(createSaleOrderDiscount(dbPro, item));
                                //update buying product
                                updateOrderDetail(saleOrderDetails, dbPro, item);
                            }
                        }

                        Double received = promotionShopMap.getQuantityReceived()!=null?promotionShopMap.getQuantityReceived():0;
                        promotionShopMap.setQuantityReceived(received + (dbPro.getAmount().getAmount() == null ? 0.0 : dbPro.getAmount().getAmount()));
                        promotionShopMaps.add(promotionShopMap);
                    }
                    if(dbPro.getDiscountDTOs() != null && !dbPro.getDiscountDTOs().isEmpty()) discountDTOs.addAll(dbPro.getDiscountDTOs());
                }
                else{
                    throw new ValidateException(ResponseMessage.PROMOTION_IN_USE, inputPro.getPromotionProgramName());
                }
            }

            //Nếu tiền giảm giá > tiền đơn hàng : thì thông báo không thể tạo đơn hàng có doanh số <0
            if(request.getProducts()!=null && !request.getProducts().isEmpty() && request.getTotalOrderAmount() < promotionInVat)
                throw new ValidateException(ResponseMessage.PROMOTION_OVER_BILL);

            //3. kiểm tra số tiền km có đúng
//            checkPromotionValue(request, shopId, voucherAmount, orderRequest, promotionInfo )
        }

        List<ComboProductDetailDTO> combos = comboProductRepository.findComboProduct(saleOrderDetails.stream().map(SaleOrderDetail::getProductId).collect(Collectors.toList()));
        if(!combos.isEmpty()) {
            List<Price> subProductPrices = priceRepository.findProductPriceWithType(combos.stream().map(item -> item.getProductId()).distinct().collect(Collectors.toList()),
                    customer.getCustomerTypeId(), DateUtils.convertToDate(LocalDateTime.now()));
            createSaleOrderComboDetail(saleOrderDetails, combos, subProductPrices).stream().forEachOrdered(listOrderComboDetails::add);
            createSaleOrderComboDiscount(saleOrderDiscounts, combos, subProductPrices).stream().forEachOrdered(listOrderComboDiscounts::add);
        }

        //4. check existing promotion code - mã giảm giá
        if (StringUtils.stringNotNullOrEmpty(request.getDiscountCode())){
            OrderPromotionRequest orderRequest = new OrderPromotionRequest();
            orderRequest.setCustomerId(request.getCustomerId());
            orderRequest.setOrderType(request.getOrderType());
            orderRequest.setProducts(lstProductOrder);
            orderRequest.setPromotionAmount(promotionInVat);
            orderRequest.setPromotionAmountExTax(promotionExVat);

            SalePromotionDTO salePromotion = salePromotionService.getDiscountCode(request.getDiscountCode(), shopId, orderRequest );
            if (salePromotion == null) throw new ValidateException(ResponseMessage.PROMOTION_IN_USE, request.getDiscountCode());

            Double discountValue = salePromotion.getAmount().getAmount();
            if (roundValue(request.getDiscountAmount())!=(roundValue(discountValue))) throw new ValidateException(ResponseMessage.PROMOTION_AMOUNT_NOTEQUALS);

            discountNeedSave = promotionClient.getPromotionDiscount(request.getDiscountCode(), shopId).getData();
        }

        // 5. kiểm tra tiền tích lũy
        if ((customer.getAmountCumulated()!=null &&  request.getAccumulatedAmount()!=null && customer.getAmountCumulated() < request.getAccumulatedAmount()))
            throw new ValidateException(ResponseMessage.ACCUMULATED_AMOUNT_OVER);

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        if(saleOrderDetails.isEmpty())
            throw new ValidateException(ResponseMessage.PLEASE_IMPORT_PRODUCTS);

        for(SaleOrderDetail buyP : saleOrderDetails) {
            if (!buyP.getIsFreeItem()) {
                if (!productNotAccumulated.contains(buyP.getProductId()))
                    customerPurchase += buyP.getTotal();
            }
        }

        SaleOrder saleOrder = createSaleOrder(request, userId, shop, customer, customerType, promotion, promotionInVat, promotionExVat,
                voucherAmount, autoPromtion, autoPromotionExVat, autoPromotionInVat, zmPromotion, zmPromotionInVat, zmPromotionExVat, customerPurchase, isReturn );

        if (apParamDTO.getApParamCode().startsWith("ONLINE") && (request.getOrderOnlineId() != null || (request.getOnlineNumber() != null && !request.getOnlineNumber().trim().isEmpty())))
            onlineOrder = this.checkOnlineOrder(saleOrder, request, shopId);

        if(printTemp){
            return saleOrderService.createPrintSaleOrderDTO(shopId, customer, saleOrder, saleOrderDetails, saleOrderDiscounts);
        }

       /* saleOrder.setOrderNumber(createOrderNumber(shop));*/
        /*repository.save(saleOrder);*/
        this.safeSave(saleOrder, shop);


        updateOnlineOrder(saleOrder, onlineOrder);

        //update sale detail
        saveOrderDetail(saleOrder, saleOrderDetails);

        updateOrderDiscount(saleOrder, saleOrderDiscounts);

        //update voucher
        updateVoucher(saleOrder, lstVoucherNeedSave);
        if(!lstVoucherNeedSave.isEmpty()) {
            List<Long> voucherIds = lstVoucherNeedSave.stream().map(vc -> vc.getId()).collect(Collectors.toList());
            syncMap.put(JMSType.vouchers, voucherIds);
        }

        
        List<Long> programDiscountIds = new ArrayList<Long>();
        //update discount
        if(discountNeedSave != null && saleOrder.getDiscountCodeAmount() != null && saleOrder.getDiscountCodeAmount() > 0) {
            discountNeedSave.setIsUsed(1);
            discountNeedSave.setOrderCustomerCode(customer.getCustomerCode());
            discountNeedSave.setActualDiscountAmount(saleOrder.getDiscountCodeAmount());
            discountNeedSave.setOrderShopCode(shop.getShopCode());

            PromotionShopMapDTO promotionShopMap = promotionClient.getPromotionShopMapV1(discountNeedSave.getPromotionProgramId(), shopId).getData();
            Double received = promotionShopMap.getQuantityReceived() != null ? promotionShopMap.getQuantityReceived() : 0;
            promotionShopMap.setQuantityReceived(received + saleOrder.getDiscountCodeAmount());
            promotionShopMaps.add(promotionShopMap);
            updatePromotionProgramDiscount(saleOrder, discountNeedSave);
            programDiscountIds.add(discountNeedSave.getId());
        }

        //update combo
        updateComboDetail(saleOrder, listOrderComboDetails);

        //update combo discount
        updateComboDiscount(saleOrder, listOrderComboDiscounts);

        //update số suât
        for(PromotionShopMapDTO item : promotionShopMaps){
            try {
                promotionClient.updatePromotionShopMapV1(item);
            }catch (Exception ex) {
                throw new ValidateException(ResponseMessage.PAYMENT_UPDATE_P_SHOP_MAP_FAIL);
            }
        }

        //update zm with given type = 3
        for(PromotionProgramDiscountDTO item : discountDTOs){
            item.setIsUsed(1);
            item.setOrderDate(saleOrder.getOrderDate());
            item.setOrderNumber(saleOrder.getOrderNumber());
            item.setOrderShopCode(shop.getShopCode());
            item.setOrderCustomerCode(customer.getCustomerCode());
            item.setOrderAmount(saleOrder.getTotal());
            try{
                promotionClient.updatePromotionProgramDiscountV1(item);
                programDiscountIds.add(item.getId());
            }catch (Exception ex) {
                throw new ValidateException(ResponseMessage.PAYMENT_UPDATE_PROMOTION_DISCOUNT_TYPE3_FAIL);
            }
        }
        syncMap.put(JMSType.promotion_program_discount, programDiscountIds);

        this.updateStockTotal(mapProductWithQty, shopId, customerType.getWareHouseTypeId() );

        //update doanh số tích lũy và tiền tích lũy cho customer, số đơn mua trong ngày...
        updateCustomer(saleOrder, customer, false);

        //update AccumulatedAmount (bảng RPT_CUS_MEM_AMOUNT) (tiền tích lũy) = tiền tích lũy hiện tại - saleOrder.getMemberCardAmount()
        Long memberCustomerId = updateAccumulatedAmount(saleOrder.getMemberCardAmount(), customer.getId());
        if(memberCustomerId != null) {
        	syncMap.put(JMSType.member_customer , Arrays.asList(memberCustomerId));
        }
        // update RPT_ZV23: nếu có km zv23

        if (request.getPromotionInfo() != null && !request.getPromotionInfo().isEmpty()) {
            for (SalePromotionDTO inputPro : request.getPromotionInfo()) {
                if ("zv23".equalsIgnoreCase(inputPro.getProgramType())) this.updateRPTZV23(inputPro, customer, shopId);
            }
        }
        syncMap.put(JMSType.sale_order, Arrays.asList(saleOrder.getId()));
        return syncMap;
    }

    private SaleOrderDetail createOrderDetail(Long shopId, SalePromotionDTO inputPro, FreeProductDTO product){
        SaleOrderDetail saleOrderDetail = new SaleOrderDetail();
        saleOrderDetail.setIsFreeItem(true);
        saleOrderDetail.setQuantity(product.getQuantity());
        saleOrderDetail.setProductId(product.getProductId());
        saleOrderDetail.setPrice(0.0);
        saleOrderDetail.setPriceNotVat(0.0);
        saleOrderDetail.setShopId(shopId);
        saleOrderDetail.setAmount(0.0);
        saleOrderDetail.setTotal(0.0);
        saleOrderDetail.setPromotionCode(inputPro.getPromotionProgramCode());
        saleOrderDetail.setPromotionName(inputPro.getPromotionProgramName());
        saleOrderDetail.setPromotionType(inputPro.getProgramType());
        saleOrderDetail.setLevelNumber(product.getLevelNumber() == null ? 1 : product.getLevelNumber());
        saleOrderDetail.setProductCode(product.getProductCode());
        saleOrderDetail.setProductName(product.getProductName());

        return saleOrderDetail;
    }

    private void checkFreeProduct(SalePromotionDTO inputPro, SalePromotionDTO dbPro, HashMap<Long, Integer> mapProductWithQty,
                                  List<SalePromotionDTO> lstSalePromotions){
        if(inputPro.getProducts()!=null && !inputPro.getProducts().isEmpty()){
            int totalQty = 0;
            int allStockQty = 0;
            String msg = "";

            if(!"zm".equalsIgnoreCase(dbPro.getProgramType())){
                if(dbPro.getContraintType() == 1){ // one free item
                    List<String> groupLevels = dbPro.getProducts().stream().map(ie ->ie.getGroupOneFreeItem()).distinct().
                            collect(Collectors.toList());

                    for(String group : groupLevels){
                        totalQty = 0;
                        allStockQty = 0;
                        int count = 0;
                        int cnt = 0;
                        for(FreeProductDTO product: inputPro.getProducts()){
                            if(group.equalsIgnoreCase(product.getGroupOneFreeItem())) {
                                cnt += 1;
                                totalQty += product.getQuantity();
                                int qtyOd = mapProductWithQty.get(product.getProductId())!=null?mapProductWithQty.get(product.getProductId()):0;
                                allStockQty += product.getStockQuantity() - qtyOd;
                                if(product.getQuantityMax() > (product.getStockQuantity() - qtyOd)){
                                    count += 1;
                                }
                            }
                        }

                        List<Integer> lstMax = dbPro.getProducts().stream().map(ie -> {
                            if(group.equals(ie.getGroupOneFreeItem())) return ie.getQuantityMax();
                            return null;
                        }).filter(Objects::nonNull).distinct().collect(Collectors.toList());
                        if(lstMax.size() == 1){ // cùng max value
                            //Nếu tổng tồn kho còn lại của SP km không đủ thì phép sửa lại SLKM = 0,
                            // xem như không áp dụng KM mức đó cho đơn hàng và cho phép lưu đơn hàng
                            if(dbPro.getEditable() == null || dbPro.getEditable() == 0){
                                // không được sửa tổng số lượng tặng < số lượng cơ cấu
                                if((totalQty < 1 || totalQty != lstMax.get(0)) && allStockQty >= lstMax.get(0) )
                                    throw new ValidateException(ResponseMessage.NO_PRODUCT, inputPro.getPromotionProgramName());
                            }else{ // được tặng số lượng nhỏ hơn số cơ cấu
                                //TODO
                            }
                        }else{ // khác max value
                            // Nếu tất cả các SP của KM của mức đều thiếu tồn khi thì cho phép sửa lại SLKM = 0,
                            // xem như không áp dụng KM mức đó cho đơn hàng và cho phép lưu đơn hàng
                            if(count == cnt && totalQty < 1){
                                // coi như ko nhận km
                            }else {
                                if (dbPro.getEditable() == null || dbPro.getEditable() == 0) { // không được sửa tổng số lượng tặng < số lượng cơ cấu
                                    for (FreeProductDTO product : inputPro.getProducts()) {
                                        if ( group.equalsIgnoreCase(product.getGroupOneFreeItem())) {
                                            if (totalQty < 1 || (product.getQuantity() != null && product.getQuantity() > 0 && totalQty != product.getQuantityMax()))
                                                throw new ValidateException(ResponseMessage.NO_PRODUCT, inputPro.getPromotionProgramName());
                                        }
                                    }
                                } else {//được tặng số lượng nhỏ hơn số cơ cấu
                                    //TODO
                                }
                            }
                        }
                    }
                }else{ // all free item

                }
            }

            totalQty = 0;
            allStockQty = 0;

            for(FreeProductDTO product: inputPro.getProducts()){
                if(product.getQuantity() == null) product.setQuantity(0);
                if(product.getStockQuantity() == null) product.setStockQuantity(0);
                if (!checkProductInPromotion(lstSalePromotions, inputPro.getProgramId(), product.getProductId(), product.getQuantity()))
                    throw new ValidateException(ResponseMessage.PRODUCT_NOT_IN_PROMOTION, product.getProductCode(), inputPro.getPromotionProgramName());

                totalQty += product.getQuantity();

                if (mapProductWithQty.containsKey(product.getProductId())) {
                    Integer qty = mapProductWithQty.get(product.getProductId());
                    mapProductWithQty.put(product.getProductId(), qty + product.getQuantity());
                    allStockQty += product.getStockQuantity() - qty;
                }else{
                    mapProductWithQty.put(product.getProductId(), product.getQuantity());
                    allStockQty += product.getStockQuantity();
                }

                if(mapProductWithQty.get(product.getProductId()) > product.getStockQuantity()){
                    if(msg.isEmpty()) msg = product.getProductCode();
                    else msg += ", " + product.getProductCode();
                }
            }
            inputPro.setTotalQty(totalQty);

            if("zm".equalsIgnoreCase(dbPro.getProgramType())){
                if(inputPro.getTotalQty() < 1 && allStockQty > 0)
                    throw new ValidateException(ResponseMessage.TOTAL_FREE_PRODUCT_GREATER_THAN_ZERO, inputPro.getPromotionProgramName());
            }
            if(!msg.isEmpty()) throw new ValidateException(ResponseMessage.STOCK_TOTAL_CANNOT_BE_NEGATIVE_SSS, msg);
        }
    }

    private SalePromotionCalItemRequest createCalItemRequest(SalePromotionDTO inputPro){
        SalePromotionCalItemRequest sPP = new SalePromotionCalItemRequest();
        sPP.setAmount(inputPro.getAmount());
        sPP.setPromotionType(inputPro.getPromotionType());
        sPP.setProgramId(inputPro.getProgramId());
        return sPP;
    }

    private void updateOrderDetail(List<SaleOrderDetail> saleOrderDetails, SalePromotionDTO dbPro, SaleDiscountSaveDTO item){
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

                if (buyP.getPromotionCode() == null) {
                    buyP.setPromotionType(dbPro.getProgramType());
                    buyP.setPromotionCode(dbPro.getPromotionProgramCode());
                    buyP.setPromotionName(dbPro.getPromotionProgramName());
                } else if(!buyP.getPromotionCode().contains(dbPro.getPromotionProgramCode())) {
                    buyP.setPromotionType(buyP.getPromotionType() + ", " + dbPro.getProgramType());
                    buyP.setPromotionCode(buyP.getPromotionCode() + ", " + dbPro.getPromotionProgramCode());
                    buyP.setPromotionName(buyP.getPromotionName() + ", " + dbPro.getPromotionProgramName());
                }
            }
        }
    }

    private void checkPromotionValue(SaleOrderRequest request, Long shopId, double voucherAmount, OrderPromotionRequest orderRequest,
                                     List<SalePromotionCalItemRequest> promotionInfo ){
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

        if(salePromotionCalculation.getPromotionAmount().intValue() != request.getPromotionAmount().intValue() ||
                salePromotionCalculation.getPaymentAmount().intValue() != request.getPaymentAmount().intValue())
            throw new ValidateException(ResponseMessage.PROMOTION_AMOUNT_NOT_CORRECT);
    }

    private SaleOrder createSaleOrder(SaleOrderRequest request, long userId, ShopDTO shop, CustomerDTO customer, CustomerTypeDTO customerType,
                                     double promotion, double promotionInVat, double promotionExVat, double voucherAmount, double autoPromtion,
                                      double autoPromotionExVat, double autoPromotionInVat, double zmPromotion, double zmPromotionInVat,
                                      double zmPromotionExVat, double customerPurchase, boolean isReturn ){
        SaleOrder saleOrder = modelMapper.map(request, SaleOrder.class);
        saleOrder.setOnlineNumber(null);
        saleOrder.setOrderDate(LocalDateTime.now());
        saleOrder.setShopId(shop.getId());
        saleOrder.setSalemanId(userId);
        saleOrder.setCustomerId(customer.getId());
        saleOrder.setWareHouseTypeId(customerType.getWareHouseTypeId());
        saleOrder.setAmount(request.getTotalOrderAmount()!=null?request.getTotalOrderAmount():0);
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
        if(saleOrder.getTotalPaid() < 1 && request.getProducts()!= null && !request.getProducts().isEmpty()) {
            double amountDisTotal = 0;
            // trừ tiền giảm giá
            if (saleOrder.getTotalPromotion() != null) {
                amountDisTotal += saleOrder.getTotalPromotionVat();
            }
            if (saleOrder.getTotalVoucher() != null) {
                amountDisTotal += saleOrder.getTotalVoucher();
            }
            // trừ tiền khuyến mãi
            if (saleOrder.getDiscountCodeAmount() != null) {
                amountDisTotal += saleOrder.getDiscountCodeAmount();
            }
            // trừ tiền tích lũy
            if (saleOrder.getMemberCardAmount() != null) {
                amountDisTotal += saleOrder.getMemberCardAmount();
            }


            double remain = saleOrder.getAmount() - amountDisTotal;
            // trừ tiền tích lũy
            if(saleOrder.getMemberCardAmount() != null && saleOrder.getMemberCardAmount() > 0) {
                if(saleOrder.getMemberCardAmount() >= -remain ) {
                    saleOrder.setMemberCardAmount(saleOrder.getMemberCardAmount() + remain);
                    remain = 0;
                }
                else {
                    amountDisTotal = amountDisTotal - saleOrder.getMemberCardAmount();
                    remain = saleOrder.getAmount() - amountDisTotal;
                    saleOrder.setMemberCardAmount(0D);
                }
            }

            if (saleOrder.getDiscountCodeAmount() != null && saleOrder.getDiscountCodeAmount() > 0 && remain < 0) {
                if(saleOrder.getDiscountCodeAmount() >= -remain ) {
                    saleOrder.setDiscountCodeAmount(saleOrder.getDiscountCodeAmount() + remain);
                    remain = 0;
                }
                else {
                    amountDisTotal = amountDisTotal - saleOrder.getDiscountCodeAmount();
                    remain = saleOrder.getAmount() - amountDisTotal;
                    saleOrder.setDiscountCodeAmount(0D);
                }
            }
            if (saleOrder.getTotalVoucher() != null && saleOrder.getTotalVoucher() > 0 && remain < 0) {
                if(saleOrder.getTotalVoucher() >= -remain ) {
                    saleOrder.setTotalVoucher(saleOrder.getTotalVoucher() + remain);
                }
                else {
                    saleOrder.setDiscountCodeAmount(0D);
                }
            }
        }

        return saleOrder;
    }

    private void updateVoucher(SaleOrder saleOrder, List<VoucherDTO> lstVoucherNeedSave){
        if(lstVoucherNeedSave != null && saleOrder.getTotalVoucher() > 0){
            double priceUse = saleOrder.getTotalVoucher();
            for(VoucherDTO voucher : lstVoucherNeedSave){
                voucher.setOrderAmount(saleOrder.getAmount());
                voucher.setOrderDate(saleOrder.getOrderDate());
                voucher.setSaleOrderId(saleOrder.getId());
                voucher.setOrderNumber(saleOrder.getOrderNumber());
                if(priceUse >= voucher.getPriceUsed())
                    priceUse -= voucher.getPriceUsed();
                else if(priceUse > 0 && priceUse < voucher.getPriceUsed()) {
                    voucher.setPriceUsed(priceUse);
                    priceUse -= priceUse;
                }
                try {
                    promotionClient.updateVoucherV1(voucher);
                }catch (Exception ex) {
                    throw new ValidateException(ResponseMessage.PAYMENT_UPDATE_VOUCHER_FAIL);
                }
                if(saleOrder.getTotalVoucher() - priceUse == saleOrder.getTotalVoucher()) break;
            }
        }
    }

    private void updatePromotionProgramDiscount(SaleOrder saleOrder, PromotionProgramDiscountDTO discountNeedSave){
        if (discountNeedSave != null){
            discountNeedSave.setOrderAmount(saleOrder.getAmount());
            discountNeedSave.setOrderNumber(saleOrder.getOrderNumber());
            discountNeedSave.setOrderDate(saleOrder.getOrderDate());
            try {
                promotionClient.updatePromotionProgramDiscountV1(discountNeedSave);
            }catch (Exception e) {
                throw new ValidateException(ResponseMessage.PAYMENT_UPDATE_PROMOTION_DISCOUNT_CODE_FAIL);
            }
        }
    }

    private void updateComboDetail(SaleOrder saleOrder, List<SaleOrderComboDetail> listOrderComboDetails){
        if(listOrderComboDetails != null){
            for(SaleOrderComboDetail combo : listOrderComboDetails){
                combo.setOrderDate(saleOrder.getOrderDate());
                combo.setSaleOrderId(saleOrder.getId());
                saleOrderComboDetailRepo.save(combo);
            }
        }
    }

    private void updateComboDiscount(SaleOrder saleOrder, List<SaleOrderComboDiscount> listOrderComboDiscounts){
        if(listOrderComboDiscounts != null){
            for(SaleOrderComboDiscount combo : listOrderComboDiscounts){
                combo.setOrderDate(saleOrder.getOrderDate());
                combo.setSaleOrderId(saleOrder.getId());
                saleOrderComboDiscountRepo.save(combo);
            }
        }
    }

    private void saveOrderDetail(SaleOrder saleOrder, List<SaleOrderDetail> saleOrderDetails){
        for(SaleOrderDetail saleOrderDetail : saleOrderDetails){
            saleOrderDetail.setOrderDate(saleOrder.getOrderDate());
            saleOrderDetail.setSaleOrderId(saleOrder.getId());
            saleOrderDetailRepository.save(saleOrderDetail);
        }
    }

    private void updateOnlineOrder(SaleOrder saleOrder, OnlineOrder onlineOrder){
        if (onlineOrder != null) {
            onlineOrder.setSynStatus(1);
            onlineOrder.setSaleOrderId(saleOrder.getId());
            onlineOrder.setOrderDate(saleOrder.getOrderDate());
            onlineOrderRepo.save(onlineOrder);
        }
    }

    private void updateOrderDiscount(SaleOrder saleOrder, List<SaleOrderDiscount> saleOrderDiscounts){
        if(saleOrderDiscounts != null){
            for(SaleOrderDiscount saleOrderDiscount : saleOrderDiscounts){
                saleOrderDiscount.setOrderDate(saleOrder.getOrderDate());
                saleOrderDiscount.setSaleOrderId(saleOrder.getId());
                saleOrderDiscountRepo.save(saleOrderDiscount);
            }
        }
    }

    private SaleOrderDiscount createSaleOrderDiscount(SalePromotionDTO dbPro, SaleDiscountSaveDTO item){
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

        return saleOrderDiscount;
    }

    private Price getPrice(List<Price> productPrices, Long productId, boolean isFreeItem){
        Price productPrice = null;
        for(Price price : productPrices){
            if(price.getProductId().equals(productId)) {
                productPrice = price;
                if (productPrice.getPrice() == null) productPrice.setPrice(0.0);
                if (productPrice.getPriceNotVat() == null) productPrice.setPriceNotVat(0.0);
                break;
            }
        }
        if (productPrice == null && isFreeItem == false)
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
            zv23Request.setShopId(customer.getShopId());
            try {
                promotionClient.updateRPTZV23V1(rpt_zv23DTO.getId(), zv23Request);
            }catch (Exception ex) {
                throw new ValidateException(ResponseMessage.PAYMENT_UPDATE_RPT_ZV23_FAIL, inputPro.getPromotionProgramCode());
            }
        }else{
            PromotionProgramDTO program = promotionClient.getByIdV1(inputPro.getProgramId()).getData();
            RPT_ZV23Request zv23Request =
                new RPT_ZV23Request(program.getId(), program.getPromotionProgramCode(), program.getFromDate(), program.getToDate(), customer.getShopId(), customer.getId(), inputPro.getZv23Amount());
            try {
                promotionClient.createRPTZV23V1(zv23Request);
            }catch (Exception ex) {
                throw new ValidateException(ResponseMessage.PAYMENT_CREATE_RPT_ZV23_FAIL);
            }
        }
    }

    public Long updateAccumulatedAmount(Double accumulatedAmount, Long customerId) {
        if (accumulatedAmount == null) return null;
        MemberCustomerRequest request = new MemberCustomerRequest(accumulatedAmount);
        Long memberCustomerId;
        try{
            memberCustomerId =  customerClient.updateMemberCustomerV1(customerId, request).getData();
        }catch (Exception ex) {
            throw new ValidateException(ResponseMessage.PAYMENT_UPDATE_MEMBER_CUSTOMER_FAIL);
        }
        return memberCustomerId;
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

        String  year = Integer.toString(now.getYear()).substring(2);
        String code = "SAL." +  shop.getShopCode() + year + Integer.toString(month + 100).substring(1)  + Integer.toString(day + 100).substring(1);
        LocalDateTime start =  DateUtils.convertFromDate(now);
        Page<SaleOrder> saleOrders = repository.getLastSaleOrderNumber(shop.getId(), code, start, PageRequest.of(0,1));
        int STT = 1;
        if(!saleOrders.getContent().isEmpty()) {
            String str = saleOrders.getContent().get(0).getOrderNumber();
            String numberString = str.substring(str.length() - 5);
            STT = Integer.valueOf(numberString) + 1;
        }

        return  code + Integer.toString(STT + 100000).substring(1);
    }

    @Override
    public OnlineOrderValidDTO getValidOnlineOrder(Long shopId) {
        Boolean isEditable = shopClient.isEditableOnlineOrderV1(shopId).getData();
        Boolean isManuallyCreatable = shopClient.isManuallyCreatableOnlineOrderV1(shopId).getData();
        OnlineOrderValidDTO valid = new OnlineOrderValidDTO(isEditable, isManuallyCreatable);
        return valid;
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

        List<Long> productComboIds = combos.stream().map(ComboProductDetailDTO::getRefProductId).distinct().collect(Collectors.toList());
        for (SaleOrderDetail item : products) {
            if(!productComboIds.contains(item.getProductId())) continue;
            double amountInTax = 0;
            double amountExTax = 0;
            int totalComboDtl = 0;
            for (ComboProductDetailDTO detail : combos) {
                if(item.getProductId().equals(detail.getRefProductId())) {
                    totalComboDtl += 1;
                    if(detail.getFactor() == null) detail.setFactor(0);
                    Price price = getPrice(productPrices, detail.getProductId(), true);
                    if(price!=null) {
                        amountInTax += price.getPrice() * detail.getFactor() * item.getQuantity();
                        amountExTax += price.getPriceNotVat() * detail.getFactor() * item.getQuantity();
                    }

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

          //  double amountTotal = 0;
            double zmAmtTotal = 0;
            double zmAmtVatTotal = 0;
            double zmAmtNotVatTotal = 0;
            double proAmtTotal = 0;
            double proAmtVatTotal = 0;
            double proAmtNotVatTotal = 0;
            int cnt = 0;

            for (ComboProductDetailDTO detail : combos) {
                if(item.getProductId().equals(detail.getRefProductId())) {
                    cnt += 1;
                    if(detail.getFactor() == null) detail.setFactor(0);
                    SaleOrderComboDetail orderComboDetail = new SaleOrderComboDetail();
                    orderComboDetail.setComboProductId(detail.getComboProductId());
                    if(item.getQuantity() == null) item.setQuantity(0);
                    orderComboDetail.setComboQuantity(item.getQuantity());
                    orderComboDetail.setQuantity(item.getQuantity() * detail.getFactor());
                    orderComboDetail.setProductId(detail.getProductId());
                    orderComboDetail.setPrice(0.0);
                    orderComboDetail.setPriceNotVat(0.0);
                    orderComboDetail.setPromotionCode(item.getPromotionCode());
                    orderComboDetail.setPromotionName(item.getPromotionName());
                    orderComboDetail.setIsFreeItem(item.getIsFreeItem());

                    Price price = getPrice(productPrices, detail.getProductId(), true);
                    if(price != null) {
                        orderComboDetail.setPrice(price.getPrice());
                        orderComboDetail.setPriceNotVat(price.getPriceNotVat());
                    }
                    orderComboDetail.setAmount(0.0);
                    orderComboDetail.setTotal(roundValue(orderComboDetail.getAmount()));
                    if(!item.getIsFreeItem()) {
                        double amount = roundValue(orderComboDetail.getPrice() * orderComboDetail.getQuantity());
                        double zmAmt = roundValue(amount * percentZM / 100);
                        double zmAmtVat = roundValue(amount * percentZMInTax / 100);
                        double zmAmtNotVat = roundValue((orderComboDetail.getPriceNotVat() * orderComboDetail.getQuantity()) * percentZMExTax / 100);
                        double proAmt = roundValue(amount * percentZV / 100);
                        double proAmtVat = roundValue(amount * percentZVInTax / 100);
                        double proAmtNotVat = roundValue((orderComboDetail.getPriceNotVat() * orderComboDetail.getQuantity()) * percentZVExTax / 100);
                        if(cnt == totalComboDtl){
                         /*   amount = (item.getAmount() == null ? 0 : item.getAmount()) - amountTotal;*/
                            zmAmt = (item.getZmPromotion() == null ? 0 : item.getZmPromotion()) - zmAmtTotal;
                            zmAmtVat = (item.getZmPromotionVat() == null ? 0 : item.getZmPromotionVat()) - zmAmtVatTotal;
                            zmAmtNotVat = (item.getZmPromotionNotVat() == null ? 0 : item.getZmPromotionNotVat()) - zmAmtNotVatTotal;
                            proAmt = (item.getAutoPromotion() == null ? 0 : item.getAutoPromotion()) - proAmtTotal;
                            proAmtVat = (item.getAutoPromotionVat() == null ? 0 : item.getAutoPromotionVat()) - proAmtVatTotal;
                            proAmtNotVat = (item.getAutoPromotionNotVat() == null ? 0 : item.getAutoPromotionNotVat()) - proAmtNotVatTotal;
                        }
                   //     amountTotal += amount;
                        zmAmtTotal += zmAmt;
                        zmAmtVatTotal += zmAmtVat;
                        zmAmtNotVatTotal += zmAmtNotVat;
                        proAmtTotal += proAmt;
                        proAmtVatTotal += proAmtVat;
                        proAmtNotVatTotal += proAmtNotVat;
                        orderComboDetail.setAmount(amount);
                        orderComboDetail.setZmPromotion(zmAmt);
                        orderComboDetail.setZmPromotionVat(zmAmtVat);
                        orderComboDetail.setZmPromotionNotVat(zmAmtNotVat);
                        orderComboDetail.setAutoPromotion(proAmt);
                        orderComboDetail.setAutoPromotionVat(proAmtVat);
                        orderComboDetail.setAutoPromotionNotVat(proAmtNotVat);
                        orderComboDetail.setTotal(roundValue(orderComboDetail.getAmount() - (orderComboDetail.getZmPromotionVat() + orderComboDetail.getAutoPromotionVat())));
                    }
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

        List<Long> productComboIds = combos.stream().map(ComboProductDetailDTO::getRefProductId).distinct().collect(Collectors.toList());
        for(SaleOrderDiscount orderDiscount : orderDiscounts){
            if(!productComboIds.contains(orderDiscount.getProductId())) continue;
            double amountInTax = 0;
            double amountExTax = 0;
            int count = 0;

            for (ComboProductDetailDTO detail : combos) {
                if(orderDiscount.getProductId().equals(detail.getRefProductId())) {
                    count += 1;
                    if(detail.getFactor() == null) detail.setFactor(0);
                    Price price = getPrice(productPrices, detail.getProductId(), true);
                    if(price!=null) {
                        amountInTax += price.getPrice() * detail.getFactor();
                        amountExTax += price.getPriceNotVat() * detail.getFactor();
                    }
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

            float amtTotal = 0;
            float amtVatTotal = 0;
            float amtNotVatTotal = 0;
            int cnt = 0;

            for (ComboProductDetailDTO detail : combos) {
                if (orderDiscount.getProductId().equals(detail.getRefProductId())) {
                    cnt += 1;
                    SaleOrderComboDiscount comboDiscount = new SaleOrderComboDiscount();
                    comboDiscount.setComboProductId(detail.getComboProductId());
                    comboDiscount.setPromotionCode(orderDiscount.getPromotionCode());
                    comboDiscount.setPromotionProgramId(orderDiscount.getPromotionProgramId());
                    comboDiscount.setIsAutoPromotion(orderDiscount.getIsAutoPromotion());
                    comboDiscount.setLevelNumber(orderDiscount.getLevelNumber() == null ? 1 : orderDiscount.getLevelNumber());
                    comboDiscount.setProductId(detail.getProductId());
                    if(detail.getFactor() == null) detail.setFactor(0);
                    comboDiscount.setDiscountAmount(0F);
                    comboDiscount.setDiscountAmountVat(0F);
                    comboDiscount.setDiscountAmountNotVat(0F);
                    Price price = getPrice(productPrices, detail.getProductId(), true);

                    if(price!=null) {
                        float amt = convertToFloat(roundValue((price.getPrice() * detail.getFactor()) * percent / 100));
                        float amtVat = convertToFloat(roundValue((price.getPrice() * detail.getFactor()) * percentInTax / 100));
                        float amtNotVat = convertToFloat(roundValue((price.getPriceNotVat() * detail.getFactor()) * percentExTax / 100));
                        if(cnt == count){
                            amt = (orderDiscount.getDiscountAmount() == null ? 0F : orderDiscount.getDiscountAmount().floatValue()) - amtTotal;
                            amtVat = (orderDiscount.getDiscountAmountVat() == null ? 0F : orderDiscount.getDiscountAmountVat().floatValue()) - amtVatTotal;
                            amtNotVat = (orderDiscount.getDiscountAmountNotVat() == null ? 0F : orderDiscount.getDiscountAmountNotVat().floatValue()) - amtNotVatTotal;
                        }
                        amtTotal += amt;
                        amtVatTotal += amtVat;
                        amtNotVatTotal += amtNotVat;
                        comboDiscount.setDiscountAmount(amt);
                        comboDiscount.setDiscountAmountVat(amtVat);
                        comboDiscount.setDiscountAmountNotVat(amtNotVat);
                    }

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
            onlineOrder = onlineOrderRepo.getById(request.getOrderOnlineId(), shopId)
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
        try {
            customerClient.updateFeignV1(customerRequest.getId(), customerRequest);
        }catch (Exception ex) {
            throw new ValidateException(ResponseMessage.PAYMENT_UPDATE_CUSTOMER_FAIL);
        }
    }


    public static String withLargeIntegers(Integer value) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(value);
    }


    public SaleOrder safeSave(SaleOrder saleOrder, ShopDTO shopDTO){
        for(int i = 0; ; i++) {
            EntityManager newEntityM = entityManager.getEntityManagerFactory().createEntityManager();
            boolean flag = false;
            try {
                newEntityM.getTransaction().begin();
                saleOrder.setOrderNumber(createOrderNumber(shopDTO));
                newEntityM.persist(saleOrder);
                newEntityM.flush();
                newEntityM.getTransaction().commit();
                flag = true;
            }catch (Exception ex ){
                newEntityM.getTransaction().rollback();
                if(ex.getCause().getClass().equals(ConstraintViolationException.class)
                        || ex.getCause().getClass().equals(DataIntegrityViolationException.class) || ex.getCause().getClass().equals(SQLIntegrityConstraintViolationException.class))  {
                    continue;
                }else {
                    throw ex;
                }
            }finally {
                newEntityM.close();
            }

            if(flag) break;
        }
        return saleOrder;
    }

}

