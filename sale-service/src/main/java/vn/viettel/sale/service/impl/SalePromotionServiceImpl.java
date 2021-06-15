package vn.viettel.sale.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.dto.customer.MemberCardDTO;
import vn.viettel.core.dto.promotion.*;
import vn.viettel.core.enums.PromotionCustObjectType;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.entities.ComboProductDetail;
import vn.viettel.sale.entities.Price;
import vn.viettel.sale.entities.Product;
import vn.viettel.sale.entities.SaleOrder;
import vn.viettel.sale.messaging.ProductOrderRequest;
import vn.viettel.sale.messaging.OrderPromotionRequest;
import vn.viettel.sale.messaging.SalePromotionCalItemRequest;
import vn.viettel.sale.messaging.SalePromotionCalculationRequest;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.SalePromotionService;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.service.enums.PromotionProgramType;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.service.feign.CustomerTypeClient;
import vn.viettel.sale.service.feign.MemberCardClient;
import vn.viettel.sale.service.feign.PromotionClient;
import vn.viettel.sale.specification.SaleOderSpecification;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SalePromotionServiceImpl extends BaseServiceImpl<SaleOrder, SaleOrderRepository> implements SalePromotionService {

    @Autowired
    PromotionClient promotionClient;

    @Autowired
    CustomerClient customerClient;

    @Autowired
    MemberCardClient memberCardClient;

    @Autowired
    SaleOrderDiscountRepository saleOrderDiscountRepo;

    @Autowired
    ProductPriceRepository productPriceRepo;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CustomerTypeClient customerTypeClient;

    @Autowired
    ComboProductDetailRepository comboProductDetailRepo;

    @Autowired
    SaleOrderRepository saleOrderRepository;

    private final int MR_NO = 1;
    private final int MR_MULTIPLE = 2;
    private final int MR_RECURSIVE = 3;
    private final int MR_MULTIPLE_RECURSIVE = 4;

    /*
    Lấy danh sách các khuyến mãi cho 1 đơn hàng
     */
    @Override
    public List<SalePromotionDTO> getSaleItemPromotions(OrderPromotionRequest request, Long shopId, boolean forSaving) {
        List<SalePromotionDTO> results = new ArrayList<>();
        CustomerDTO customer = customerClient.getCustomerByIdV1(request.getCustomerId()).getData();
        if(customer == null) throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
        // get default warehouse
        CustomerTypeDTO customerType = customerTypeClient.getCusTypeIdByShopIdV1(shopId);
        Long warehouseId = 0L;
        if (customerType != null)
            warehouseId = customerType.getWareHouseTypeId();

        // Danh sách chương trình khuyến mãi thỏa các điều kiện cửa hàng, khách hàng
        List<PromotionProgramDTO> programs = this.validPromotionProgram(request, shopId, customer);
        if(programs.isEmpty()) return null;

        ProductOrderDataDTO orderData = this.getProductOrderData(request, customer);
        if (orderData == null || orderData.getProducts() == null || orderData.getProducts().isEmpty())
            return null;

        HashMap<Long, ProductOrderDetailDataDTO> mapProductOrder = new HashMap<>();
        // gộp sản phẩm nếu có mua sản phẩm trùng
        for (ProductOrderDetailDataDTO dto : orderData.getProducts()){
            if(dto.getQuantity() == null) dto.setQuantity(0);
            if(dto.getTotalPrice() == null) dto.setTotalPrice(0.0);
            if (mapProductOrder.containsKey(dto.getProductId())){
                ProductOrderDetailDataDTO exited = mapProductOrder.get(dto.getProductId());
                exited.setQuantity(exited.getQuantity() + dto.getQuantity());
                mapProductOrder.put(dto.getProductId(), exited);
            }else{
                mapProductOrder.put(dto.getProductId(), dto);
            }
        }

        orderData.setProducts(new ArrayList<>(mapProductOrder.values()));

        List<SalePromotionDTO> zv01zv18 = new ArrayList<>();
        List<SalePromotionDTO> zv19zv21 = new ArrayList<>();
        List<SalePromotionDTO> zm = new ArrayList<>();
        List<SalePromotionDTO> zv23 = new ArrayList<>();

        for (PromotionProgramDTO program: programs) {
            switch (PromotionProgramType.valueOf(program.getType())) {
                case ZV01:
                case ZV02:
                case ZV03:
                case ZV04:
                case ZV05:
                case ZV06:
                case ZV07:
                case ZV08:
                case ZV09:
                case ZV10:
                case ZV11:
                case ZV12:
                case ZV13:
                case ZV14:
                case ZV15:
                case ZV16:
                case ZV17:
                case ZV18:
                    this.addItemPromotion(zv01zv18, this.getAutoItemPromotionZV01ToZV21(program, orderData, shopId, warehouseId, 0,0,0,0, forSaving));
                    break;
                case ZV23:
                    this.addItemPromotion(zv23, this.getItemPromotionZV23(program, orderData, shopId, warehouseId, request.getCustomerId(), forSaving));
                    break;
                case ZM:
                    this.addItemPromotion(zm, this.getItemPromotionZM(program, orderData, shopId, warehouseId, forSaving));
                    break;
                default:
                    // Todo
            }
        }

        if (zv01zv18 != null && zv01zv18.size() > 0){
            zv01zv18.stream().forEachOrdered(results::add);
        }

        if (zm != null && zm.size() > 0){
            zm.stream().forEachOrdered(results::add);
        }

        if (zv23 != null && zv23.size() > 0){
            zv23.stream().forEachOrdered(results::add);
        }

        double totalBeforeZV23InTax = 0;
        double totalBeforeZV23ExTax = 0;
        double totalZV23InTax = 0;
        double totalZV23ExTax = 0;
        List<String> checkBefore = Arrays.asList("zv01", "zv02", "zv03", "zv04", "zv05", "zv06", "zv07", "zv08", "zv09", "zv10", "zv11", "zv12", "zv13"
                , "zv14", "zv15", "zv16", "zv17", "zv18", "zv19", "zv20", "zv21", "zm");
        for (SalePromotionDTO item : results){
            if(item.getIsUse()) {
                if (checkBefore.contains(item.getProgramType().trim().toUpperCase())) {
                    totalBeforeZV23InTax += item.getTotalAmtInTax() == null ? 0 : item.getTotalAmtInTax();
                    totalBeforeZV23ExTax += item.getTotalAmtExTax() == null ? 0 : item.getTotalAmtExTax();
                } else if ("zv23".equalsIgnoreCase(item.getProgramType().trim())) {
                    totalZV23InTax += item.getTotalAmtInTax() == null ? 0 : item.getTotalAmtInTax();
                    totalZV23ExTax += item.getTotalAmtExTax() == null ? 0 : item.getTotalAmtExTax();
                }
            }
        }

        // ví zv19 - 21 được tính với tổng tiền sau khi đã trừ hết các km khác nên phải kiểm tra riêng và sau tất cả các km
        SalePromotionDTO item = null;
        for (PromotionProgramDTO program: programs) {
            switch (PromotionProgramType.valueOf(program.getType())) {
                case ZV19:
                case ZV20:
                case ZV21:
                    item = this.getAutoItemPromotionZV01ToZV21(program, orderData, shopId, warehouseId, totalBeforeZV23InTax, totalBeforeZV23ExTax, totalZV23InTax, totalZV23ExTax, forSaving);
                    this.addItemPromotion(zv19zv21, item);
                    break;
                default:
                    // Todo
            }
            if(item != null && item.getIsUse()) {
                if (checkBefore.contains(item.getProgramType().trim().toUpperCase())) {
                    totalBeforeZV23InTax += item.getTotalAmtInTax() == null ? 0 : item.getTotalAmtInTax();
                    totalBeforeZV23ExTax += item.getTotalAmtExTax() == null ? 0 : item.getTotalAmtExTax();
                } else if ("zv23".equalsIgnoreCase(item.getProgramType().trim())) {
                    totalZV23InTax += item.getTotalAmtInTax() == null ? 0 : item.getTotalAmtInTax();
                    totalZV23ExTax += item.getTotalAmtExTax() == null ? 0 : item.getTotalAmtExTax();
                }
            }
        }

        if (zv19zv21 != null && zv19zv21.size() > 0){
            zv19zv21.stream().forEachOrdered(results::add);
        }

        return results;
    }

    private void addItemPromotion(List<SalePromotionDTO> lstPromotion, SalePromotionDTO promotion){
        if (lstPromotion != null && promotion != null){
            lstPromotion.add(promotion);
        }
    }

    /*
    Lấy danh sách khuyến mãi ZV01 đến ZV21
     */
    private SalePromotionDTO getAutoItemPromotionZV01ToZV21(PromotionProgramDTO program, ProductOrderDataDTO orderData, Long shopId, Long warehouseId,
                          double totalBeforeZV23InTax, double totalBeforeZV23ExTax, double totalZV23InTax, double totalZV23ExTax, boolean forSaving){
        SalePromotionDTO auto = null;
        switch (PromotionProgramType.valueOf(program.getType())) {
            case ZV01:
            case ZV02:
            case ZV03:
            case ZV04:
            case ZV05:
            case ZV06:
                auto = this.getZV01ToZV06(program, orderData, shopId, warehouseId, forSaving);
                break;
            case ZV07:
            case ZV08:
            case ZV09:
            case ZV10:
            case ZV11:
            case ZV12:
                auto = this.getZV07ToZV12(program, orderData, shopId, warehouseId, forSaving);
                break;
            case ZV13:
            case ZV14:
            case ZV15:
            case ZV16:
            case ZV17:
            case ZV18:
                auto = this.getZV13ToZV18(program, orderData, shopId, warehouseId, forSaving);
                break;
            case ZV19:
            case ZV20:
            case ZV21:
                auto = this.getZV19ToZV21(program, orderData, shopId, warehouseId, totalBeforeZV23InTax, totalBeforeZV23ExTax, totalZV23InTax, totalZV23ExTax, forSaving);
                break;
            default:
                // Todo
        }

        if(auto != null){
            auto.setPromotionType(0);
            auto.setProgramId(program.getId());
            auto.setProgramType(program.getType());
            auto.setPromotionProgramName(program.getPromotionProgramName());
            auto.setPromotionProgramCode(program.getPromotionProgramCode());
            if (program.getIsEdited() != null && program.getIsEdited() == 0)
                auto.setIsEditable(false);
            else
                auto.setIsEditable(true);
            if (program.getRelation() == null || program.getRelation() == 0)
                auto.setContraintType(0);
            else
                auto.setContraintType(program.getRelation());
            Double value = getPromotionLimit(auto, shopId);
            auto.setIsUse(false);
            auto.setNumberLimited(value);
            if(value == null || value > 0) {
                auto.setIsUse(true);
            }
        }

        return auto;
    }

    /*
    Lấy danh sách khuyến mãi tay ZM
     */
    private SalePromotionDTO getItemPromotionZM(PromotionProgramDTO program, ProductOrderDataDTO orderData, Long shopId, Long warehouseId, boolean forSaving){
        if (program == null || orderData == null || orderData.getProducts() == null || orderData.getProducts().isEmpty())
            return null;

        List<PromotionSaleProductDTO> details = promotionClient.findPromotionSaleProductByProgramIdV1(program.getId()).getData();
        if(details.isEmpty()) return null;

        HashMap<Long, PromotionSaleProductDTO> mapProductPro = new HashMap<>();
        double totalAmountInTax = 0;
        double totalAmountExtax = 0;
        // gộp sản phẩm nếu có trùng
        for (PromotionSaleProductDTO dto : details){
            if (dto.getQuantity() == null) dto.setQuantity(0);
            if (mapProductPro.containsKey(dto.getProductId())){
                PromotionSaleProductDTO exited = mapProductPro.get(dto.getProductId());
                exited.setQuantity(exited.getQuantity() + dto.getQuantity());
                mapProductPro.put(dto.getProductId(), exited);
            }else{
                mapProductPro.put(dto.getProductId(), dto);
            }
        }

        List<PromotionSaleProductDTO> programDetails = new ArrayList<>(mapProductPro.values());
        // các sp được km
        List<ProductOrderDetailDataDTO> lstProductHasPromotion = new ArrayList<>();
        boolean flag = false;
        boolean isInclusiveTax = isInclusiveTax(program.getDiscountPriceType());
        // lấy tổng giá trị mua
        // Mua 1 sản phẩm/bộ sản phẩm đạt số lượng - giảm giá khách hàng, tặng hàng
        for (PromotionSaleProductDTO productPromotion : programDetails) {
            if (productPromotion.getProductId() == null) {
                totalAmountInTax += orderData.getTotalPrice();
                totalAmountExtax += orderData.getTotalPriceNotVAT();
                if(orderData.getQuantity() >= productPromotion.getQuantity())
                    flag = true;
                orderData.getProducts().stream().forEachOrdered(lstProductHasPromotion::add);
                break;
            }else{
                for (ProductOrderDetailDataDTO productOrder : orderData.getProducts()){
                    if (productOrder.getTotalPrice() == null) productOrder.setTotalPrice(0.0);
                    if (productOrder.getTotalPriceNotVAT() == null) productOrder.setTotalPriceNotVAT(0.0);

                    if (productPromotion.getProductId().equals(productOrder.getProductId()) && productOrder.getQuantity() >= productPromotion.getQuantity()) {
                        totalAmountInTax += productOrder.getTotalPrice();
                        totalAmountExtax += productOrder.getTotalPriceNotVAT();
                        flag = true;
                        lstProductHasPromotion.add(productOrder);
                        break;
                    }

                }
            }
        }

        if (flag){
            List<PromotionProgramDiscountDTO> programDiscount = promotionClient.findPromotionDiscountByPromotion(program.getId()).getData();
            if (programDiscount == null || programDiscount.isEmpty())
                return null;

            SalePromotionDTO salePromotion = null;

            PromotionProgramDiscountDTO discountDTO = programDiscount.get(0);
            //đạt số tiền trong khoảng quy định
            if ( (discountDTO.getMinSaleAmount() != null &&
                    ((isInclusiveTax && discountDTO.getMinSaleAmount() > totalAmountInTax) || (!isInclusiveTax && discountDTO.getMinSaleAmount() > totalAmountExtax)))
                    || ( discountDTO.getMaxSaleAmount() != null &&
                    ((isInclusiveTax && discountDTO.getMaxSaleAmount() < totalAmountInTax) || (!isInclusiveTax && discountDTO.getMaxSaleAmount() < totalAmountExtax))) ) {
                return null;
            }

            if (program.getGivenType() != null && program.getGivenType() == 1){// tặng sản phẩm
                List<PromotionProductOpenDTO> freeProducts = promotionClient.getFreeItemV1(program.getId()).getData();
                salePromotion = new SalePromotionDTO();
                if(freeProducts != null) {
                    List<FreeProductDTO> products = new ArrayList<>();
                    int totalQty = 0;
                    for(PromotionProductOpenDTO productOpen : freeProducts) {
                        FreeProductDTO freeProductDTO = productRepository.getFreeProductDTONoOrder(shopId, warehouseId, productOpen.getProductId());
                        if(freeProductDTO != null){
                            products.add(freeProductDTO);
                            totalQty += freeProductDTO.getQuantity();
                        }
                    }
                    salePromotion.setProducts(products);
                    salePromotion.setTotalQty(totalQty);
                }
            }else { //tặng tiền + %
                if (discountDTO.getType() != null && discountDTO.getType() == 0) { // KM tiền
                    SalePromotionDiscountDTO spDto = new SalePromotionDiscountDTO();
                    if (discountDTO.getDiscountAmount() == null) discountDTO.setDiscountAmount(0.0);
                    Double amount = discountDTO.getDiscountAmount();
                    if (discountDTO.getMaxDiscountAmount() == null) {
                        spDto.setMaxAmount(discountDTO.getDiscountAmount());
                        spDto.setAmount(discountDTO.getDiscountAmount());
                    } else {
                        if (discountDTO.getDiscountAmount() > discountDTO.getMaxDiscountAmount()) {
                            spDto.setMaxAmount(discountDTO.getMaxDiscountAmount());
                            spDto.setAmount(discountDTO.getMaxDiscountAmount());
                            amount = discountDTO.getMaxDiscountAmount();
                        } else {
                            spDto.setMaxAmount(discountDTO.getMaxDiscountAmount());
                            spDto.setAmount(discountDTO.getDiscountAmount());
                        }
                    }
                    salePromotion = new SalePromotionDTO();
                    double percent = (totalAmountInTax - totalAmountExtax )/ totalAmountExtax * 100;
                    if(isInclusiveTax){
                        salePromotion.setTotalAmtInTax(amount);
                        salePromotion.setTotalAmtExTax(totalAmountExtax * percent / 100);
                    }else{
                        salePromotion.setTotalAmtInTax(totalAmountInTax * percent / 100);
                        salePromotion.setTotalAmtExTax(amount);
                    }
                    spDto.setPercentage(percent);
                    salePromotion.setAmount(spDto);
                } else if (discountDTO.getType() != null && discountDTO.getType() == 1) { // KM %
                    if (discountDTO.getDiscountPercent() == null || discountDTO.getDiscountPercent() == 0) {
                        return null;
                    }

                    SalePromotionDiscountDTO spDto = new SalePromotionDiscountDTO();
                    double amtInTax = totalAmountInTax * discountDTO.getDiscountPercent() / 100;
                    double amtExTax = totalAmountExtax * discountDTO.getDiscountPercent() / 100;
                    double amount = amtExTax;
                    if(isInclusiveTax){
                        amount = amtInTax;
                    }
                    if (discountDTO.getMaxDiscountAmount() == null) {
                        spDto.setMaxAmount(amount);
                        spDto.setAmount(amount);
                    } else {
                        if (amount > discountDTO.getMaxDiscountAmount()) {
                            spDto.setMaxAmount(amount);
                        } else {
                            spDto.setMaxAmount(discountDTO.getMaxDiscountAmount());
                        }
                        spDto.setAmount(amount);
                    }
                    if(forSaving)
                        spDto.setPercentage(discountDTO.getDiscountPercent());
                    salePromotion = new SalePromotionDTO();
                    salePromotion.setAmount(spDto);
                    salePromotion.setTotalAmtInTax(amtInTax);
                    salePromotion.setTotalAmtExTax(amtExTax);
                }
            }

            if (salePromotion != null) {
                salePromotion.setPromotionType(1);
                salePromotion.setProgramId(program.getId());
                salePromotion.setProgramType(program.getType());
                salePromotion.setPromotionProgramName(program.getPromotionProgramName());
                if (program.getIsEdited() != null && program.getIsEdited() == 0)
                    salePromotion.setIsEditable(false);
                else
                    salePromotion.setIsEditable(true);
                if (program.getRelation() == null || program.getRelation() == 0)
                    salePromotion.setContraintType(0);
                else
                    salePromotion.setContraintType(program.getRelation());
                Double value = getPromotionLimit(salePromotion, shopId);
                salePromotion.setIsUse(false);
                salePromotion.setNumberLimited(value);
                if(value == null || value > 0) {
                    salePromotion.setIsUse(true);
                }
                return salePromotion;
            }
        }

        return null;
    }

    /*
    Tính tiền km cho từng sản phẩm
    totalAmount là tổng tiền trước thuế / sau thuế
    disAmount là tiền giảm giá trước thuế / sau thuế
    totalAmount và disAmount cùng loại trước / sau
     */
    private double calPercent(Double totalAmount, Double disAmount){
        if (totalAmount == null || totalAmount == 0 || disAmount == null || disAmount == 0)
            return 0;
        return ((disAmount / totalAmount) * 100);
    }

    /*s
    Lấy danh sách khuyến mãi ZV23
     */
    private SalePromotionDTO getItemPromotionZV23(PromotionProgramDTO program, ProductOrderDataDTO orderData, Long shopId, Long warehouseId, Long customerId, boolean forSaving){
        if (program == null || orderData == null || orderData.getProducts() == null || orderData.getProducts().isEmpty())
            return null;
        /* lấy amount từ promotion service: lấy doanh số RPT_ZV23.TOTAL_AMOUNT của khách hàng
         Doanh số tại thời điểm mua = doanh số tổng hợp đồng bộ đầu ngày + doanh số phát sinh trong ngày */
        Date now = new Date();
        RPT_ZV23DTO rpt_zv23DTO = promotionClient.checkZV23RequireV1(program.getId(),customerId,shopId).getData();
        if(rpt_zv23DTO == null) return null;
        List<SaleOrder> customerSOList = saleOrderRepository.findAll(Specification.where(
                SaleOderSpecification.hasFromDateToDate(DateUtils.convertFromDate(now),DateUtils.convertToDate(now)))
                .and(SaleOderSpecification.hasCustomerId(customerId)));
        double totalInDay = 0F;
        for(SaleOrder customerSO:customerSOList) {
            totalInDay = totalInDay + customerSO.getTotal();
        }
        Double totalCusAmount = rpt_zv23DTO.getTotalAmount() + totalInDay;  //Doanh số tại thời điểm mua
        // danh sách sản phẩm loại trừ theo id ctkm
        List<Long> promotionIds = new ArrayList<>();
        promotionIds.add(program.getId());
        List<PromotionProgramProductDTO> programProduct = promotionClient.getRejectProductV1(promotionIds).getData();// danh sách sản phẩm loại trừ
        List<PromotionProgramDetailDTO> details = promotionClient.findPromotionProgramDetailV1(program.getId()).getData();
        if(details.isEmpty()) return null;

        double amountExTax = 0;
        double amountInTax = 0;
        double amountZV23 = 0;
        double percent = 0;
        //lấy sp được km
        HashMap<Long,ProductOrderDetailDataDTO> lstProductHasPromotion = new HashMap<>();
        // lấy tổng tiền theo những sản phẩm quy định
        for (PromotionProgramDetailDTO pItem : details){
            if (pItem.getSaleAmt() != null)
                amountZV23 = pItem.getSaleAmt();
            if (pItem.getDisPer() != null)
                percent = pItem.getDisPer();
            for (ProductOrderDetailDataDTO oItem : orderData.getProducts()){
                if (oItem.getProductId().equals(pItem.getProductId())){
                    if (oItem.getTotalPrice() == null) oItem.setTotalPrice(0.0);
                    if (oItem.getTotalPriceNotVAT() == null) oItem.setTotalPriceNotVAT(0.0);
                    if (program.getDiscountPriceType() == null || program.getDiscountPriceType() == 0){ // exclusive vat
                        amountExTax += oItem.getTotalPriceNotVAT();
                    }else{ // inclusive vat
                        amountInTax += oItem.getTotalPrice();
                    }

                    if(!lstProductHasPromotion.containsKey(oItem.getProductId())){
                        lstProductHasPromotion.put(oItem.getProductId(), oItem);
                    }
                }
            }
        }

        //nếu không quy định sản phẩm
        if (amountExTax == 0){
            if (orderData.getTotalPrice() == null) orderData.setTotalPrice(0.0);
            if (orderData.getTotalPriceNotVAT() == null) orderData.setTotalPriceNotVAT(0.0);
            amountExTax = orderData.getTotalPriceNotVAT();
            amountInTax = orderData.getTotalPrice();

            for (ProductOrderDetailDataDTO oItem : orderData.getProducts()){
                if(!lstProductHasPromotion.containsKey(oItem.getProductId())){
                    lstProductHasPromotion.put(oItem.getProductId(), oItem);
                }
            }
        }

        // loại trừ sản phẩm
        if (programProduct != null){
            for (PromotionProgramProductDTO exItem : programProduct){
                for (ProductOrderDetailDataDTO oItem : orderData.getProducts()){
                    if (oItem.getProductId().equals(exItem.getProductId())){
                        if (oItem.getTotalPrice() == null) oItem.setTotalPrice(0.0);
                        if (oItem.getTotalPriceNotVAT() == null) oItem.setTotalPriceNotVAT(0.0);

                        if (program.getDiscountPriceType() == null || program.getDiscountPriceType() == 0){ // exclusive vat
                            amountExTax -= oItem.getTotalPriceNotVAT();
                        }else{ // inclusive vat
                            amountInTax -= oItem.getTotalPrice();
                        }
                    }
                }
                if(lstProductHasPromotion.containsKey(exItem.getProductId())){
                    lstProductHasPromotion.remove(exItem.getProductId());
                }
            }
        }

        //-	Số tiền còn lại có thể hưởng CTKM ZV23 = [số tiền quy định của ZV23] – [doanh số tính tới thời điểm mua]
        double amountRemain = amountZV23 - totalCusAmount;
        // -	Nếu [số tiền còn lại có thể hưởng CTKM ZV23] ≥ [giá trị mua hàng của đơn] (lưu ý cách tính giá chiết khấu, xem mục 6):
        //	Số tiền có thể hưởng CTKM ZV23 = [giá trị mua hàng của đơn]
        //-	Nếu [số tiền còn lại có thể hưởng CTKM ZV23] < [giá trị mua hàng của đơn] (lưu ý cách tính giá chiết khấu, xem mục 6):
        //	Số tiền có thể hưởng CTKM ZV23 = [số tiền còn lại có thể hưởng CTKM ZV23]
        double p = (amountInTax-amountExTax)/amountExTax*100;
        double amount = 0;
        if (isInclusiveTax(program.getDiscountPriceType())){ // exclusive vat
            if(amountRemain < amountInTax) {
                amountInTax = amountRemain;
                amountExTax = amountInTax / (( 100 + p ) / 100);
            }
            amount = amountInTax;
        }else { // inclusive vat
            if(amountRemain < amountExTax) {
                amountExTax = amountRemain;
                amountInTax = amountExTax * ((100 + p) / 100);
            }
            amount = amountExTax;
        }

        if (percent > 0 && amount > 0){
            SalePromotionDiscountDTO discountDTO = new SalePromotionDiscountDTO();
            SalePromotionDTO salePromotion = new SalePromotionDTO();
            double amtInTax = amountInTax * percent / 100;
            double amtExTax = amountExTax * percent / 100;
            discountDTO.setMaxAmount(amount * percent / 100);
            discountDTO.setAmount(amount * percent / 100);
            if(forSaving)
                discountDTO.setPercentage(percent);
            salePromotion.setTotalAmtInTax(amtInTax);
            salePromotion.setTotalAmtExTax(amtExTax);
            salePromotion.setAmount(discountDTO);

            if(forSaving) {
                List<SaleDiscountSaveDTO> saveInfo = new ArrayList<>();
                for (Map.Entry<Long, ProductOrderDetailDataDTO> entry : lstProductHasPromotion.entrySet()) {
                    SaleDiscountSaveDTO saveDTO = initSaleDiscountSaveDTO(entry.getValue(), null, percent, isInclusiveTax(program.getDiscountPriceType()));
                    saveInfo.add(saveDTO);
                }
                discountDTO.setDiscountInfo(saveInfo);
            }

            salePromotion.setPromotionType(1);
            salePromotion.setProgramId(program.getId());
            salePromotion.setProgramType(program.getType());
            salePromotion.setPromotionProgramName(program.getPromotionProgramName());
            if (program.getIsEdited() != null && program.getIsEdited() == 0)
                salePromotion.setIsEditable(false);
            else
                salePromotion.setIsEditable(true);
            if (program.getRelation() == null || program.getRelation() == 0)
                salePromotion.setContraintType(0);
            else
                salePromotion.setContraintType(program.getRelation());

            Double value = getPromotionLimit(salePromotion, shopId);
            salePromotion.setIsUse(false);
            salePromotion.setNumberLimited(value);
            if(value == null || value > 0) {
                salePromotion.setIsUse(true);
            }

            return salePromotion;
        }

        return null;
    }

    /*
    Tính tiền khuyến mãi và tiền cần thanh toán cho 1 đơn hàng
     */
    @Override
    public SalePromotionCalculationDTO promotionCalculation(SalePromotionCalculationRequest calculationRequest, Long shopId){
        SalePromotionCalculationDTO result = new SalePromotionCalculationDTO();
        double promotionAmount = 0;
        double paymentAmount = 0;

        if (calculationRequest.getTotalOrderAmount() != null && calculationRequest.getTotalOrderAmount() != 0){
            paymentAmount = calculationRequest.getTotalOrderAmount();

            //tính tiền khuyến mãi
            if (calculationRequest.getPromotionInfo() != null && calculationRequest.getPromotionInfo().size() > 0){
                List<String> lstType = Arrays.asList("zv19", "zv20", "zv21");

                double totalBeforeZV23InTax = 0;
                double totalBeforeZV23ExTax = 0;
                double totalZV23InTax = 0;
                double totalZV23ExTax = 0;
                List<PromotionProgramDTO> lstZV1921 = new ArrayList<>();

                for (SalePromotionCalItemRequest item : calculationRequest.getPromotionInfo()){
                    if (item.getProgramId() != null){
                        PromotionProgramDTO programDTO = promotionClient.getByIdV1(item.getProgramId()).getData();
                        if (programDTO != null && item.getAmount() != null && item.getAmount().getAmount() != null && item.getAmount().getAmount() != null){
                            if (lstType.contains(programDTO.getType().trim().toLowerCase())){
                                lstZV1921.add(programDTO);
                            }else{
                                double amtInTax = 0;
                                double amtExTax = 0;
                                if (isInclusiveTax(programDTO.getDiscountPriceType())) { //inclusive tax
                                    amtInTax = item.getAmount().getAmount();
                                    amtExTax = item.getAmount().getAmount() * (100 - (item.getAmount().getPercentage() == null ? 1 : item.getAmount().getPercentage())) / 100;
                                } else {
                                    amtInTax = item.getAmount().getAmount() * (100 + (item.getAmount().getPercentage() == null ? 1 : item.getAmount().getPercentage())) / 100;
                                    amtExTax = item.getAmount().getAmount();
                                }
                                promotionAmount += amtInTax;

                                if ("zv23".equalsIgnoreCase(programDTO.getType().trim())) {
                                    totalZV23InTax += amtInTax;
                                    totalZV23ExTax += amtExTax;
                                } else {
                                    totalBeforeZV23InTax += amtInTax;
                                    totalBeforeZV23ExTax += amtExTax;
                                }
                            }
                        }
                    }
                }

                if(!lstZV1921.isEmpty() && calculationRequest.getOrderRequest() != null){
                    List<SalePromotionDTO> resultZV1921 = new ArrayList<>();
                    Long warehouseId = 0L;
                    CustomerDTO customer = customerClient.getCustomerByIdV1(calculationRequest.getOrderRequest().getCustomerId()).getData();
                    if(customer == null) throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
                    // get default warehouse
                    CustomerTypeDTO customerType = customerTypeClient.getCusTypeIdByShopIdV1(shopId);
                    if (customerType != null)
                        warehouseId = customerType.getWareHouseTypeId();
                    ProductOrderDataDTO orderData = this.getProductOrderData(calculationRequest.getOrderRequest(), customer);
                    if (orderData == null || orderData.getProducts() == null || orderData.getProducts().isEmpty())
                        throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);

                    for (PromotionProgramDTO programItem : lstZV1921){
                        SalePromotionDTO salePromotionDTO = this.getAutoItemPromotionZV01ToZV21(programItem, orderData, shopId, warehouseId, totalBeforeZV23InTax, totalBeforeZV23ExTax, totalZV23InTax, totalZV23ExTax, true);
                        if(salePromotionDTO != null){
                            resultZV1921.add(salePromotionDTO);
                            double amtInTax = 0;
                            double amtExTax = 0;
                            if (isInclusiveTax(programItem.getDiscountPriceType())) { //inclusive tax
                                amtInTax = salePromotionDTO.getAmount().getAmount();
                                amtExTax = salePromotionDTO.getAmount().getAmount() * (100 - (salePromotionDTO.getAmount().getPercentage() == null ? 1 : salePromotionDTO.getAmount().getPercentage())) / 100;
                            } else {
                                amtInTax = salePromotionDTO.getAmount().getAmount() * (100 + (salePromotionDTO.getAmount().getPercentage() == null ? 1 : salePromotionDTO.getAmount().getPercentage())) / 100;
                                amtExTax = salePromotionDTO.getAmount().getAmount();
                            }
                            promotionAmount += amtInTax;

                            if ("zv23".equalsIgnoreCase(programItem.getType().trim())) {
                                totalZV23InTax += amtInTax;
                                totalZV23ExTax += amtExTax;
                            } else {
                                totalBeforeZV23InTax += amtInTax;
                                totalBeforeZV23ExTax += amtExTax;
                            }
                        }
                    }

                    if(!resultZV1921.isEmpty()){
                        result.setResultZV1921(resultZV1921);
                    }
                }
            }

            // trừ tiền khuyến mãi
            if (promotionAmount > paymentAmount){
                paymentAmount = 0;
            }else{
                paymentAmount = paymentAmount - promotionAmount;
            }

            // trừ tiền giảm giá
            if (calculationRequest.getDiscountAmount() != null){
                if (calculationRequest.getDiscountAmount() > paymentAmount){
                    paymentAmount = 0;
                }else{
                    paymentAmount = paymentAmount - calculationRequest.getDiscountAmount();
                }
            }

            // trừ tiền voucher
            if (calculationRequest.getVoucherAmount() != null){
                if (calculationRequest.getVoucherAmount() > paymentAmount){
                    paymentAmount = 0;
                }else{
                    paymentAmount = paymentAmount - calculationRequest.getVoucherAmount();
                }
            }

            // trừ tiền tích lũy
            if (calculationRequest.getAccumulatedAmount() != null){
                if (calculationRequest.getAccumulatedAmount() > paymentAmount){
                    paymentAmount = 0;
                }else{
                    paymentAmount = paymentAmount - calculationRequest.getAccumulatedAmount();
                }
            }
        }

        result.setPromotionAmount(promotionAmount);
        result.setPaymentAmount(paymentAmount);

        return result;
    }

    @Override
    public boolean checkPromotionLimit(SalePromotionDTO salePromotion, Long shopId) {
        Double value = getPromotionLimit(salePromotion, shopId);
        if(value == null || value > 0) {
            return true;
        }

        return false;
    }

    /*
    Lấy số xuất theo ctkm, return null = vô hạn , = 0 hết suất, > 0 giới hạn số suât
     */
    private Double getPromotionLimit(SalePromotionDTO salePromotion, Long shopId) {
        if(salePromotion != null && salePromotion.getProgramId() != null && shopId != null) {
            PromotionShopMapDTO promotionShopMap = promotionClient.getPromotionShopMapV1(salePromotion.getProgramId(), shopId).getData();

            if (salePromotion.getTotalQty() != null && salePromotion.getTotalQty() > 0){
                if (promotionShopMap.getQuantityMax() == null) return null;
                else{
                    double quantityReceive = promotionShopMap.getQuantityReceived() != null ? promotionShopMap.getQuantityReceived() : 0;
                    if (promotionShopMap.getQuantityMax() >= (quantityReceive + salePromotion.getTotalQty()))
                        return promotionShopMap.getQuantityMax() - quantityReceive;
                }
            }
            if(salePromotion.getAmount() !=null && salePromotion.getAmount().getAmount() != null && salePromotion.getAmount().getAmount() > 0){
                if (promotionShopMap.getAmountMax() == null) return null;
                else{
                    double receive = promotionShopMap.getAmountReceived() != null ? promotionShopMap.getAmountReceived() : 0;
                    if (promotionShopMap.getAmountMax() >= (receive + salePromotion.getAmount().getAmount()))
                        return promotionShopMap.getAmountMax() - receive;
                }
            }
        }

        return 0.0;
    }

    /*
     *ZV01 to zv16
     */
    private SalePromotionDTO getZV01ToZV06(PromotionProgramDTO program, ProductOrderDataDTO orderData, Long shopId, Long warehouseId, boolean forSaving) {

        if (program == null || orderData == null || orderData.getProducts() == null || orderData.getProducts().isEmpty())
            return null;

        List<PromotionProgramDetailDTO> details = promotionClient.findPromotionProgramDetailV1(program.getId()).getData();
        if(details.isEmpty()) return null;

        List<Long> idProductOrder = new ArrayList<>();
        for (ProductOrderDetailDataDTO item : orderData.getProducts()){
            if (!idProductOrder.contains(item.getProductId()))
                idProductOrder.add(item.getProductId());
        }

        // gộp những sản phẩm km với key là sp km
        HashMap<Long, PromotionProgramDetailDTO> mapFreeProduct = new HashMap<>();
        // key sản phẩm mua: 1 sp mua có nhiều mức, 1 mức theo 1 sản phẩm sẽ có nhiều item km
        HashMap<Long, HashMap<Integer, List<PromotionProgramDetailDTO>>> mapOrderNumber = new HashMap<>();
        int checkMulti = checkMultipleRecursive(program.getMultiple(), program.getRecursive());
        Long productId = null;
        // gộp sản phẩm nếu có trùng
        for (PromotionProgramDetailDTO dto : details){
            // chỉ lấy những sản phẩm được mua có km và có có require = 1
            if (idProductOrder.contains(dto.getProductId()) && (dto.getRequired() != null && dto.getRequired() == 1)) {
                if (dto.getSaleQty() == null) dto.setSaleQty(0);
                if (dto.getSaleAmt() == null) dto.setSaleAmt(0.0);
                if (dto.getDisPer() == null) dto.setDisPer(0.0);
                if (dto.getDiscAmt() == null) dto.setDiscAmt(0.0);
                if (mapOrderNumber.containsKey(dto.getProductId())) {
                    HashMap<Integer, List<PromotionProgramDetailDTO>> orderNo = mapOrderNumber.get(dto.getProductId());
                    if (orderNo.containsKey(dto.getOrderNumber())) {
                        List<PromotionProgramDetailDTO> lst = orderNo.get(dto.getOrderNumber());
                        lst.add(dto);
                        orderNo.put(dto.getOrderNumber(), lst);
                        mapOrderNumber.put(dto.getProductId(), orderNo);
                    } else {
                        List<PromotionProgramDetailDTO> lst = new ArrayList<>();
                        lst.add(dto);
                        orderNo.put(dto.getOrderNumber(), lst);
                        mapOrderNumber.put(dto.getProductId(), orderNo);
                    }
                } else {
                    HashMap<Integer, List<PromotionProgramDetailDTO>> orderNo = new HashMap<>();
                    List<PromotionProgramDetailDTO> lst = new ArrayList<>();
                    lst.add(dto);
                    orderNo.put(dto.getOrderNumber(), lst);
                    mapOrderNumber.put(dto.getProductId(), orderNo);
                }
            }
        }

        // dừng lại khi không có sản phẩm nào được km
        if (mapOrderNumber.size() == 0)
            return null;

        int multiple = 1; // tính bội số
        Integer level = -1;
        boolean isInclusiveTax = isInclusiveTax (program.getDiscountPriceType());
        List<String> checkQty = Arrays.asList("zv01", "zv02", "zv03");
        List<String> checkAmt = Arrays.asList("zv04", "zv05", "zv06");
        String type = program.getType() == null ? "" : program.getType().trim().toLowerCase();
        double totalAmountExTax = 0;
        double totalAmountInTax = 0;
        double totalAmountSaleExTax = 0;
        double totalAmountSaleInTax = 0;
        List<SaleDiscountSaveDTO> saveInfo = new ArrayList<>();
        List<FreeProductDTO> lstProductPromotion = new ArrayList<>();
        int totalDisQty = 0;

        // get level number: mua 1 sản phẩm -> ctkm chỉ được chứa 1 sản phẩm mua -> lấy mức level thấp nhất
        for (Map.Entry<Long, HashMap<Integer, List<PromotionProgramDetailDTO>>> entry : mapOrderNumber.entrySet()){
            //lấy 1 row km
            List<Integer> lstLevel = new ArrayList(entry.getValue().keySet());
            lstLevel.sort(Comparator.nullsFirst(Comparator.naturalOrder()));
            ProductOrderDetailDataDTO productOrder = null;
            for (ProductOrderDetailDataDTO productOrder1 : orderData.getProducts()) {
                if (productOrder1.getProductId().equals(entry.getKey())){
                    productOrder = productOrder1;
                    break;
                }
            }

            for (Integer lv : lstLevel){
                // vì km trên 1 sp nên điều kiện km như nhau
                PromotionProgramDetailDTO item = entry.getValue().get(lv).get(0);
                // kiểm tra điều kiện mua
                if ((checkQty.contains(type) && productOrder.getQuantity() >= item.getSaleQty()) // Mua sản phẩm, với số lượng xác định cho 1 sp
                        || ( checkAmt.contains(type) && ((isInclusiveTax && productOrder.getTotalPrice() >= item.getSaleAmt()) ||
                        (!isInclusiveTax && productOrder.getTotalPriceNotVAT() >= item.getSaleAmt())) ) // Mua sản phẩm, với số tiền xác định cho 1 sp
                ) {
                    level = lv;
                }
            }

            if (level == -1) return null;
            //level != -1 -> đã có ít nhất 1 sp thỏa điều kiện
            // vì km trên bộ sp nên điều kiện km như nhau
            PromotionProgramDetailDTO item = entry.getValue().get(level).get(0);
            if (checkQty.contains(type)) {
                if (item.getSaleQty() > 0) {
                    multiple = productOrder.getQuantity() / item.getSaleQty();
                }
            } else {
                if (item.getSaleAmt() > 0) {
                    multiple = (int)(productOrder.getTotalPriceNotVAT() / item.getSaleAmt());
                    if (isInclusiveTax) {
                        multiple = (int)(productOrder.getTotalPrice() / item.getSaleAmt());
                    }
                }
            }

            // zv01 , zv02, zv04, zv05
            if (
                //Mua 1 sản phẩm, với số lượng xác định, giảm % tổng tiền
                    "zv01".equalsIgnoreCase(type) ||
                            //Mua 1 sản phẩm, với số lượng xác định, giảm số tiền
                            "zv02".equalsIgnoreCase(type) ||
                            //Mua 1 sản phẩm, với số tiền đạt mức nào đó, thì được giảm % tổng tiền
                            "zv04".equalsIgnoreCase(type) ||
                            //Mua 1 sản phẩm, với số tiền đạt mức nào đó, thì được giảm trừ 1 số tiền
                            "zv05".equalsIgnoreCase(type)
            ){ // KM tien
                PromotionProgramDetailDTO dto = entry.getValue().get(level).get(0);
                double amount = dto.getDiscAmt() == null ? 0 : dto.getDiscAmt();
                double percent = dto.getDisPer() == null ? 0 : dto.getDisPer();
                if (checkMulti == MR_MULTIPLE || checkMulti == MR_MULTIPLE_RECURSIVE){ // nhân lên theo số bộ
                    percent = percent * multiple;
                    amount = amount * multiple;
                }
                if (checkMulti == MR_RECURSIVE || checkMulti == MR_MULTIPLE_RECURSIVE){ // tối ưu
                    for (Map.Entry<Integer, List<PromotionProgramDetailDTO>> entry1 : entry.getValue().entrySet()){
                        if (entry1.getKey() != null && entry1.getKey() < level){
                            PromotionProgramDetailDTO dto1 = entry1.getValue().get(0);
                            if (dto1.getDisPer() != null)  percent = percent + dto1.getDisPer();
                            if (dto1.getDiscAmt() != null)  amount = amount + dto1.getDiscAmt();
                        }
                    }
                }
                double amtInTax = 0;
                double amtExTax = 0;
                if ("zv01".equalsIgnoreCase(type) || "zv04".equalsIgnoreCase(type)){
                    amtInTax = productOrder.getTotalPrice() * percent / 100;
                    amtExTax = productOrder.getTotalPriceNotVAT() * percent / 100;
                    amount = amtExTax;
                    if (isInclusiveTax){
                        amount = amtInTax;
                    }
                }else{
                    percent = calPercent(productOrder.getTotalPriceNotVAT(), amount);
                    amtInTax = productOrder.getTotalPrice() * percent / 100;
                    amtExTax = amount;
                    if (isInclusiveTax){
                        percent = calPercent(productOrder.getTotalPrice(), amount);
                        amtInTax = amount;
                        amtExTax = productOrder.getTotalPriceNotVAT() * percent / 100;
                    }
                }
                totalAmountInTax += amtInTax;
                totalAmountExTax += amtExTax;
                totalAmountSaleExTax += productOrder.getTotalPriceNotVAT();
                totalAmountSaleInTax += productOrder.getTotalPrice();

                if (amount > 0){
                    SaleDiscountSaveDTO saveDTO = new SaleDiscountSaveDTO();
                    saveDTO.setProductId(dto.getProductId());
                    saveDTO.setLevelNumber(level);
                    saveDTO.setAmount(amount);
                    saveDTO.setMaxAmount(amount);
                    saveDTO.setAmountExTax(amtExTax);
                    saveDTO.setAmountInTax(amtInTax);
                    saveInfo.add(saveDTO);
                }
            } // end KM tien
            // zv03 , zv06
            else if (program.getType() != null && (
                    //Mua 1 sản phẩm, với số lượng xác định, tặng 1 hoặc nhiều sản phẩm nào đó
                    "zv03".equalsIgnoreCase(program.getType().trim()) ||
                            //Mua 1 sản phẩm, với số tiền đạt mức nào đó, thì được tặng 1 hoặc 1 nhóm sản phẩm nào đó
                            "zv06".equalsIgnoreCase(program.getType().trim()) )
            ) { // KM san pham
                List<PromotionProgramDetailDTO> dtlProgram = entry.getValue().get(level);

                if (!dtlProgram.isEmpty()) {
                    Integer totalQty = null;
                    for ( PromotionProgramDetailDTO dto : dtlProgram){
                        FreeProductDTO freeProductDTO = productRepository.getFreeProductDTONoOrder(shopId, warehouseId, dto.getFreeProductId());
                        freeProductDTO.setLevelNumber(level);
                        if (freeProductDTO != null) {
                            int qty = dto.getFreeQty();
                            if (checkMulti == MR_MULTIPLE || checkMulti == MR_MULTIPLE_RECURSIVE){ // nhân lên theo số bộ
                                qty = qty * multiple;
                            }
                            if (level != null && checkMulti == MR_RECURSIVE || checkMulti == MR_MULTIPLE_RECURSIVE){ // tối ưu
                                for (Map.Entry<Integer, List<PromotionProgramDetailDTO>> entry1 : entry.getValue().entrySet()){
                                    if (entry1.getKey() != null && entry1.getKey() < level){
                                        for (PromotionProgramDetailDTO dto1 : entry1.getValue()){
                                            if (dto1.getFreeQty() != null)  qty = qty + dto1.getFreeQty();
                                        }
                                    }
                                }
                            }
                            if(totalQty == null)
                                totalQty = qty;

                            //lấy số tối đa
                            if (program.getRelation() == null || program.getRelation() == 0) {
                                freeProductDTO.setQuantity(qty);
                                freeProductDTO.setQuantityMax(qty);
                            }else{
                                if (totalQty > 0){
                                    freeProductDTO.setQuantity(qty);
                                    if (totalQty > freeProductDTO.getStockQuantity()){
                                        totalQty = totalQty - freeProductDTO.getStockQuantity();
                                        freeProductDTO.setQuantity(freeProductDTO.getStockQuantity());
                                    }
                                    else{
                                        totalQty = 0;
                                        freeProductDTO.setQuantityMax(qty);
                                    }
                                }
                            }
                            totalDisQty += freeProductDTO.getQuantity() == null? 0 : freeProductDTO.getQuantity();
                            lstProductPromotion.add(freeProductDTO);
                        }
                    }
                }
            }
        }

        if (totalAmountExTax > 0){
            SalePromotionDiscountDTO discountDTO = new SalePromotionDiscountDTO();
            SalePromotionDTO salePromotion = new SalePromotionDTO();
            discountDTO.setMaxAmount(totalAmountExTax);
            discountDTO.setAmount(totalAmountExTax);
            if(forSaving)
                discountDTO.setPercentage(calPercent(totalAmountSaleExTax, totalAmountExTax));
            if (isInclusiveTax) {
                discountDTO.setMaxAmount(totalAmountInTax);
                discountDTO.setAmount(totalAmountInTax);
                if(forSaving)
                    discountDTO.setPercentage(calPercent(totalAmountSaleInTax, totalAmountInTax));
            }
            salePromotion.setTotalAmtInTax(totalAmountInTax);
            salePromotion.setTotalAmtExTax(totalAmountExTax);
            salePromotion.setAmount(discountDTO);
            if(forSaving){
                discountDTO.setDiscountInfo(saveInfo);
            }

            return salePromotion;
        }else if (!lstProductPromotion.isEmpty()){
            SalePromotionDTO salePromotion = new SalePromotionDTO();
            salePromotion.setProducts(lstProductPromotion);
            salePromotion.setTotalQty(totalDisQty);

            return salePromotion;
        }

        return null;
    }

    /*
     *ZV13 to zv18
     */
    private SalePromotionDTO getZV13ToZV18(PromotionProgramDTO program, ProductOrderDataDTO orderData, Long shopId, Long warehouseId, boolean forSaving) {
        if (program == null || orderData == null || orderData.getProducts() == null || orderData.getProducts().isEmpty())
            return null;

        List<PromotionProgramDetailDTO> details = promotionClient.findPromotionProgramDetailV1(program.getId()).getData();
        if(details.isEmpty()) return null;

        List<Long> idProductOrder = new ArrayList<>();
        for (ProductOrderDetailDataDTO item : orderData.getProducts()){
            if (!idProductOrder.contains(item.getProductId()))
                idProductOrder.add(item.getProductId());
        }

        //lấy sp được km
        List<ProductOrderDetailDataDTO> lstProductHasPromotion = new ArrayList<>();
        // gộp những sản phẩm km với key là sp km
        HashMap<Long, PromotionProgramDetailDTO> mapFreeProduct = new HashMap<>();
        // key sản phẩm mua: 1 sp mua có nhiều mức, 1 mức theo 1 sản phẩm sẽ có nhiều item km
        HashMap<Long, HashMap<Integer, List<PromotionProgramDetailDTO>>> mapOrderNumber = new HashMap<>();
        int checkMulti = checkMultipleRecursive(program.getMultiple(), program.getRecursive());
        // gộp sản phẩm nếu có trùng
        for (PromotionProgramDetailDTO dto : details){
            // tất cả sp trong promotion phải được mua
            if (!idProductOrder.contains(dto.getProductId()))
                return null;
            // tất cả các row khuyến mãi phải có require = 1
            if (dto.getRequired() == null || dto.getRequired() != 1)
                return null;
            if(dto.getSaleQty() == null) dto.setSaleQty(0);
            if(dto.getSaleAmt() == null) dto.setSaleAmt(0.0);
            if(dto.getDisPer() == null) dto.setDisPer(0.0);
            if(dto.getDiscAmt() == null) dto.setDiscAmt(0.0);
            if (mapOrderNumber.containsKey(dto.getProductId())){
                HashMap<Integer, List<PromotionProgramDetailDTO>> orderNo = mapOrderNumber.get(dto.getProductId());
                if (orderNo.containsKey(dto.getOrderNumber())){
                    List<PromotionProgramDetailDTO> lst = orderNo.get(dto.getOrderNumber());
                    lst.add(dto);
                    orderNo.put(dto.getOrderNumber(), lst);
                    mapOrderNumber.put(dto.getProductId(), orderNo);
                }else{
                    List<PromotionProgramDetailDTO> lst = new ArrayList<>();
                    lst.add(dto);
                    orderNo.put(dto.getOrderNumber(), lst);
                    mapOrderNumber.put(dto.getProductId(), orderNo);
                }
            }else{
                HashMap<Integer, List<PromotionProgramDetailDTO>> orderNo = new HashMap<>();
                List<PromotionProgramDetailDTO> lst = new ArrayList<>();
                lst.add(dto);
                orderNo.put(dto.getOrderNumber(), lst);
                mapOrderNumber.put(dto.getProductId(), orderNo);
            }
        }

        double percent = 0;
        double amount = 0;
        double amountOrderInTax = 0; // tổng giá của bộ sản phẩm
        double amountOrderExTax = 0; // tổng giá của bộ sản phẩm
        int countProductQty = 0; // đếm item thỏa số lượng xem có đủ bộ
        int countProductAmt = 0; // đếm item thỏa tiền xem có đủ bộ
        int multiple = 1; // tính bội số
        Integer level = -1;
        List<Integer> lstLevel = null;
        List<Integer> lstLv = new ArrayList<>();
        boolean isInclusiveTax = isInclusiveTax (program.getDiscountPriceType());
        List<String> checkQty = Arrays.asList("zv13", "zv14", "zv15");
        List<String> checkAmt = Arrays.asList("zv16", "zv17", "zv18");
        String type = program.getType() == null ? "" : program.getType().trim().toLowerCase();

        // get level number: 1 bộ sản phẩm thì phải chung level -> lấy mức level thấp nhất
        for (ProductOrderDetailDataDTO productOrder : orderData.getProducts()) {
            if(mapOrderNumber.containsKey(productOrder.getProductId())) {
                lstProductHasPromotion.add(productOrder);
                level = -1;
                if (lstLevel == null) {
                    lstLevel = new ArrayList(mapOrderNumber.get(productOrder.getProductId()).keySet());
                    lstLevel.sort(Comparator.nullsFirst(Comparator.naturalOrder()));
                }

                for (Integer lv : lstLevel) {
                    // vì km trên bộ sp nên điều kiện km như nhau
                    PromotionProgramDetailDTO item = mapOrderNumber.get(productOrder.getProductId()).get(lv).get(0);

                    // kiểm tra điều kiện mua
                    if ((checkQty.contains(type) && productOrder.getQuantity() >= item.getSaleQty()) // Mua sản phẩm, với số lượng xác định cho 1 sp
                            || (checkAmt.contains(type) && ((isInclusiveTax && productOrder.getTotalPrice() >= item.getSaleAmt()) ||
                            (!isInclusiveTax && productOrder.getTotalPriceNotVAT() >= item.getSaleAmt()))) // Mua sản phẩm, với số tiền xác định cho 1 sp
                    ) {
                        level = lv;
                    }
                }
                if (level != -1) lstLv.add(level);
            }
        }

        if (!lstLv.isEmpty()){
            lstLv.sort(Comparator.nullsFirst(Comparator.naturalOrder()));
            level = lstLv.get(0);
        }

        if (level == -1) return null;
        //level != -1 -> đã có ít nhất 1 sp thỏa điều kiện

        // tính bội số vì số lượng và tiền kiểm tra riêng trên từng sản phẩn
        for (ProductOrderDetailDataDTO productOrder : orderData.getProducts()) {
            if(mapOrderNumber.containsKey(productOrder.getProductId())) {
                if (productOrder.getQuantity() != null && productOrder.getQuantity() > 0 && productOrder.getTotalPrice() != null && productOrder.getTotalPrice() > 0) {
                    // vì km trên bộ sp nên điều kiện km như nhau
                    PromotionProgramDetailDTO item = mapOrderNumber.get(productOrder.getProductId()).get(level).get(0);

                    if (checkQty.contains(type)) {
                        if (item.getSaleQty() > 0 && (productOrder.getQuantity() / item.getSaleQty()) >= multiple) {
                            multiple = productOrder.getQuantity() / item.getSaleQty();
                        } else if (item.getSaleQty() > 0 && (productOrder.getQuantity() / item.getSaleQty()) < 2) {
                            multiple = 1;
                        }
                    } else {
                        if (isInclusiveTax) {
                            if (item.getSaleAmt() > 0 && (productOrder.getTotalPrice() / item.getSaleAmt()) >= multiple) {
                                multiple = (int) (productOrder.getTotalPrice() / item.getSaleAmt());
                            } else if (item.getSaleAmt() != null && item.getSaleAmt() > 0 && (productOrder.getTotalPrice() / item.getSaleAmt()) < 2) {
                                multiple = 1;
                            }
                        } else {
                            if (item.getSaleAmt() > 0 && (productOrder.getTotalPriceNotVAT() / item.getSaleAmt()) >= multiple) {
                                multiple = (int) (productOrder.getTotalPriceNotVAT() / item.getSaleAmt());
                            } else if (item.getSaleAmt() != null && item.getSaleAmt() > 0 && (productOrder.getTotalPriceNotVAT() / item.getSaleAmt()) < 2) {
                                multiple = 1;
                            }
                        }
                    }
                }
            }
        }

        PromotionProgramDetailDTO newPromo = null;
        for (ProductOrderDetailDataDTO productOrder : orderData.getProducts()){
            if(mapOrderNumber.containsKey(productOrder.getProductId())) {
                for (PromotionProgramDetailDTO item : mapOrderNumber.get(productOrder.getProductId()).get(level)) {
                    newPromo = item;
                    if (!mapFreeProduct.containsKey(item.getFreeProductId())) {
                        mapFreeProduct.put(item.getFreeProductId(), item);
                    }
                }

                if (newPromo != null){
                    // Mua sản phẩm, với số lượng xác định
                    if (checkQty.contains(type) && productOrder.getQuantity() >= newPromo.getSaleQty() && newPromo.getSaleQty() > 0) {
                        countProductQty++;
                        // tính số tiền mua của bộ sản phẩm, nếu mua dư số lẻ thì bỏ ra
                        amountOrderInTax += productOrder.getPrice() * multiple * newPromo.getSaleQty();
                        amountOrderExTax += productOrder.getPriceNotVAT() * multiple * newPromo.getSaleQty();
                    }

                    // Mua sản phẩm, với số tiền xác định
                    if ( newPromo.getSaleAmt() > 0 && checkAmt.contains(type) &&
                            (!isInclusiveTax && productOrder.getTotalPriceNotVAT() >= newPromo.getSaleAmt() )
                            || (isInclusiveTax && productOrder.getTotalPrice() >= newPromo.getSaleAmt())
                    ){
                        countProductAmt++;
                        amountOrderInTax += productOrder.getTotalPrice();
                        amountOrderExTax += productOrder.getTotalPriceNotVAT();
                    }
                }
            }
        }

        if (newPromo != null){
            if (newPromo.getDisPer() != null)
                percent = newPromo.getDisPer();

            if (newPromo.getDiscAmt() != null)
                amount = newPromo.getDiscAmt();
        }

        int count = mapOrderNumber.size();// số lượng sản phẩm mua

        if (percent > 0 &&
                // Mua theo Bộ sản phẩm (nghĩa là phải đầy đủ sản phẩm, bắt buộc)- với số lượng xác định, thì sẽ được giảm % tổng tiền của nhóm này
                (("zv13".equalsIgnoreCase(type) && count == countProductQty)
                //Mua theo Bộ sản phẩm (nghĩa là phải đầy đủ sản phẩm, bắt buộc)- với số tiền xác định, thì sẽ được giảm %
                || ("zv16".equalsIgnoreCase(type) && count == countProductAmt))
        ){
            if (checkMulti == MR_MULTIPLE || checkMulti == MR_MULTIPLE_RECURSIVE){ // nhân lên theo số bộ
                percent = percent * multiple;
            }
            if (checkMulti == MR_RECURSIVE || checkMulti == MR_MULTIPLE_RECURSIVE){ // tối ưu
                for (Map.Entry<Integer, List<PromotionProgramDetailDTO>> entry : mapOrderNumber.get(newPromo.getProductId()).entrySet()){
                    if (entry.getKey() != null && entry.getKey() < level){
                        PromotionProgramDetailDTO dto = entry.getValue().get(0);
                        if (dto.getDisPer() != null)  percent = percent + dto.getDisPer();
                    }
                }
            }

            SalePromotionDiscountDTO discountDTO = new SalePromotionDiscountDTO();
            SalePromotionDTO salePromotion = new SalePromotionDTO();
            double amtInTax = amountOrderInTax * percent / 100;
            double amtExTax = amountOrderExTax * percent / 100;
            double amt = amtExTax;
            if (isInclusiveTax){ // exclusive vat
                amt = amtInTax;
            }
            discountDTO.setMaxAmount(amt);
            discountDTO.setAmount(amt);
            if(forSaving)
                discountDTO.setPercentage(percent);
            salePromotion.setTotalAmtInTax(amtInTax);
            salePromotion.setTotalAmtExTax(amtExTax);
            salePromotion.setAmount(discountDTO);

            if(forSaving) {
                List<SaleDiscountSaveDTO> saveInfo = new ArrayList<>();
                for (ProductOrderDetailDataDTO product : lstProductHasPromotion) {
                    SaleDiscountSaveDTO saveDTO = initSaleDiscountSaveDTO(product, level, percent, isInclusiveTax);
                    saveInfo.add(saveDTO);
                }
                discountDTO.setDiscountInfo(saveInfo);
            }

            return salePromotion;
        }
        else if (amount > 0 &&
                //Mua theo Bộ sản phẩm (nghĩa là phải mua đầy đủ sản phẩm, bắt buộc) - với số lượng xác định, thì sẽ được giảm trừ 1 số tiền
                (("zv14".equalsIgnoreCase(type) && count == countProductQty )
                //Mua theo Bộ sản phẩm (nghĩa là phải đầy đủ sản phẩm, bắt buộc)- với số tiền xác định, thì sẽ được trừ tiền.
                || ("zv17".equalsIgnoreCase(type) && count == countProductAmt))
        ){
            if (checkMulti == MR_MULTIPLE || checkMulti == MR_MULTIPLE_RECURSIVE){ // nhân lên theo số bộ
                amount = amount * multiple;
            }
            if (checkMulti == MR_RECURSIVE || checkMulti == MR_MULTIPLE_RECURSIVE){ // tối ưu
                for (Map.Entry<Integer, List<PromotionProgramDetailDTO>> entry : mapOrderNumber.get(newPromo.getProductId()).entrySet()){
                    if (entry.getKey() != null && entry.getKey() < level){
                        PromotionProgramDetailDTO dto = entry.getValue().get(0);
                        if (dto.getDiscAmt() != null)  amount = amount + dto.getDiscAmt();
                    }
                }
            }
            SalePromotionDiscountDTO discountDTO = new SalePromotionDiscountDTO();
            SalePromotionDTO salePromotion = new SalePromotionDTO();
            percent = calPercent(amountOrderExTax, amount);
            if (isInclusiveTax){
                percent = calPercent(amountOrderInTax, amount);
            }
            double amtInTax = amountOrderInTax * percent / 100;
            double amtExTax = amountOrderExTax * percent / 100;
            discountDTO.setMaxAmount(amount);
            discountDTO.setAmount(amount);
            if(forSaving)
                discountDTO.setPercentage(percent);
            salePromotion.setTotalAmtInTax(amtInTax);
            salePromotion.setTotalAmtExTax(amtExTax);
            salePromotion.setAmount(discountDTO);

            if(forSaving) {
                List<SaleDiscountSaveDTO> saveInfo = new ArrayList<>();
                for (ProductOrderDetailDataDTO product : lstProductHasPromotion) {
                    SaleDiscountSaveDTO saveDTO = initSaleDiscountSaveDTO(product, level, percent, isInclusiveTax);
                    saveInfo.add(saveDTO);
                }
                discountDTO.setDiscountInfo(saveInfo);
            }

            return salePromotion;
        }
        else if (//Mua theo Bộ sản phẩm (nghĩa là phải mua đầy đủ sản phẩm, bắt buộc) - với số lượng xác định, thì sẽ được tặng 1 hoặc nhóm sản phẩm nào đó với số lượng xác định
                (("zv15".equalsIgnoreCase(type) && count == countProductQty)
                //Mua theo Bộ sản phẩm (nghĩa là phải đầy đủ sản phẩm, bắt buộc)- với số tiền xác định, thì sẽ được tặng 1 hoặc nhóm sản phẩm nào đó.
                 || ("zv18".equalsIgnoreCase(type) && count == countProductAmt)
                )
        ){
            List<FreeProductDTO> lstProductPromotion = new ArrayList<>();
            List<PromotionProgramDetailDTO> dtlProgram = new ArrayList<>(mapFreeProduct.values());
            int totalDisQty = 0;
            if (!dtlProgram.isEmpty()) {
                Integer totalQty = null;
                for ( PromotionProgramDetailDTO dto : dtlProgram){
                    FreeProductDTO freeProductDTO = productRepository.getFreeProductDTONoOrder(shopId, warehouseId, dto.getFreeProductId());
                    if (freeProductDTO != null) {
                        int qty = dto.getFreeQty();
                        if (checkMulti == MR_MULTIPLE || checkMulti == MR_MULTIPLE_RECURSIVE){ // nhân lên theo số bộ
                            qty = qty * multiple;
                        }
                        if (checkMulti == MR_RECURSIVE || checkMulti == MR_MULTIPLE_RECURSIVE){ // tối ưu
                            for (Map.Entry<Integer, List<PromotionProgramDetailDTO>> entry : mapOrderNumber.get(newPromo.getProductId()).entrySet()){
                                if (entry.getKey() != null && entry.getKey() < level){
                                    for (PromotionProgramDetailDTO dto1 : entry.getValue()){
                                        if (dto1.getFreeQty() != null)  qty = qty + dto1.getFreeQty();
                                    }
                                }
                            }
                        }
                        if(totalQty == null)
                            totalQty = qty;

                        //lấy số tối đa
                        if (program.getRelation() == null || program.getRelation() == 0) {
                            freeProductDTO.setQuantity(qty);
                            freeProductDTO.setQuantityMax(qty);
                            if (qty > freeProductDTO.getStockQuantity())
                                freeProductDTO.setQuantity(freeProductDTO.getStockQuantity());
                        }else{
                            if (totalQty > 0){
                                freeProductDTO.setQuantity(qty);
                                if (totalQty > freeProductDTO.getStockQuantity()){
                                    totalQty = totalQty - freeProductDTO.getStockQuantity();
                                    freeProductDTO.setQuantity(freeProductDTO.getStockQuantity());
                                }
                                else{
                                    totalQty = 0;
                                    freeProductDTO.setQuantityMax(qty);
                                }
                            }
                        }
                        totalDisQty += freeProductDTO.getQuantity() == null? 0 : freeProductDTO.getQuantity();
                        lstProductPromotion.add(freeProductDTO);
                    }
                }
            }

            if (!lstProductPromotion.isEmpty()){
                SalePromotionDTO salePromotion = new SalePromotionDTO();
                salePromotion.setProducts(lstProductPromotion);
                salePromotion.setTotalQty(totalDisQty);
                return salePromotion;
            }
        }

        return null;
    }

    private boolean isInclusiveTax(Integer field){
        if (field == null || field == 0)
            return false;
        return true;
    }

    private int checkMultipleRecursive(Integer multiple, Integer recursive){
        if (multiple == null) multiple = 0;
        if (recursive == null) recursive = 0;
        if(multiple == 0 && recursive == 0)
            return MR_NO;
        else if(multiple == 1 && recursive == 0)
            return MR_MULTIPLE;
        else if(multiple == 0 && recursive == 1)
            return MR_RECURSIVE;
        else
            return MR_MULTIPLE_RECURSIVE;
    }

    /*
     *ZV07 zv12
     */
    private SalePromotionDTO getZV07ToZV12(PromotionProgramDTO program, ProductOrderDataDTO orderData, Long shopId, Long warehouseId, boolean forSaving) {
        if (program == null || orderData == null || orderData.getProducts() == null || orderData.getProducts().isEmpty())
            return null;

        List<PromotionProgramDetailDTO> details = promotionClient.findPromotionProgramDetailV1(program.getId()).getData();
        if(details.isEmpty()) return null;

        HashMap<Long, ProductOrderDetailDataDTO> idProductOrder = new HashMap<>();
        for (ProductOrderDetailDataDTO item : orderData.getProducts()){
            if (!idProductOrder.containsKey(item.getProductId()))
                idProductOrder.put(item.getProductId(), item);
        }

        // key là sản phẩm mua: 1 sp mua có nhiều mức, 1 mức theo sẽ có nhiều item km
        HashMap<Long, HashMap<Integer, List<PromotionProgramDetailDTO>>> mapOrderNumber = new HashMap<>();
        double totalOrderQty = 0;
        double totalOrderAmtInTax = 0;
        double totalOrderAmtExtax = 0;
        int multiple = 1; // tính bội số
        boolean isInclusiveTax = isInclusiveTax (program.getDiscountPriceType());
        int checkMulti = checkMultipleRecursive(program.getMultiple(), program.getRecursive());
        Long orderProductIdDefault = null;
        //lấy sp được km
        List<ProductOrderDetailDataDTO> lstProductHasPromotion = new ArrayList<>();

        // gộp sản phẩm nếu có trùng
        for (PromotionProgramDetailDTO dto : details){
            //băt buộc mua sản phẩm với require = 1
            if (dto.getRequired() != null && dto.getRequired() == 1 && !idProductOrder.containsKey(dto.getProductId()))
                return null;
            if(dto.getSaleQty() == null) dto.setSaleQty(0);
            if(dto.getSaleAmt() == null) dto.setSaleAmt(0.0);
            if(dto.getDisPer() == null) dto.setDisPer(0.0);
            if(dto.getDiscAmt() == null) dto.setDiscAmt(0.0);
            if (mapOrderNumber.containsKey(dto.getProductId())){
                HashMap<Integer, List<PromotionProgramDetailDTO>> orderNo = mapOrderNumber.get(dto.getProductId());
                if (orderNo.containsKey(dto.getOrderNumber())){
                    List<PromotionProgramDetailDTO> lst = orderNo.get(dto.getOrderNumber());
                    lst.add(dto);
                    orderNo.put(dto.getOrderNumber(), lst);
                    mapOrderNumber.put(dto.getProductId(), orderNo);
                }else{
                    List<PromotionProgramDetailDTO> lst = new ArrayList<>();
                    lst.add(dto);
                    orderNo.put(dto.getOrderNumber(), lst);
                    mapOrderNumber.put(dto.getProductId(), orderNo);
                }
            }else{
                lstProductHasPromotion.add(idProductOrder.get(dto.getProductId()));
                HashMap<Integer, List<PromotionProgramDetailDTO>> orderNo = new HashMap<>();
                List<PromotionProgramDetailDTO> lst = new ArrayList<>();
                lst.add(dto);
                orderNo.put(dto.getOrderNumber(), lst);
                mapOrderNumber.put(dto.getProductId(), orderNo);
                if (idProductOrder.containsKey(dto.getProductId())){
                    if (isInclusiveTax)
                        totalOrderAmtInTax += idProductOrder.get(dto.getProductId()).getTotalPrice();
                    else
                        totalOrderAmtExtax += idProductOrder.get(dto.getProductId()).getTotalPriceNotVAT();
                    totalOrderQty += idProductOrder.get(dto.getProductId()).getQuantity();
                }
                if (orderProductIdDefault == null) orderProductIdDefault = dto.getProductId();
            }
        }

        if ((totalOrderAmtInTax == 0 && totalOrderAmtExtax == 0) || totalOrderQty == 0)
            return null;

        Integer level = -1;
        List<String> checkQty = Arrays.asList("zv07", "zv08", "zv09");
        List<String> checkAmt = Arrays.asList("zv10", "zv11", "zv12");
        String type = program.getType() == null ? "" : program.getType().trim().toLowerCase();

        // get level number: so sánh trên tổng số mua -> chỉ cần lấy đại diện 1 row detail
        List<Integer> lstLevel = new ArrayList(mapOrderNumber.get(orderProductIdDefault).keySet());
        lstLevel.sort(Comparator.nullsFirst(Comparator.naturalOrder()));
        PromotionProgramDetailDTO defaultItem = null;

        for (Integer lv : lstLevel){
            // vì km trên bộ sp nên điều kiện km như nhau
            PromotionProgramDetailDTO item = mapOrderNumber.get(orderProductIdDefault).get(lv).get(0);
            // kiểm tra điều kiện mua
            if ((checkQty.contains(type) && totalOrderQty >= item.getSaleQty()) // Mua sản phẩm, với tổng số lượng xác định
                    || ( checkAmt.contains(type) && ((isInclusiveTax && totalOrderAmtInTax >= item.getSaleAmt()) ||
                    (!isInclusiveTax && totalOrderAmtExtax >= item.getSaleAmt()) ) ) // Mua sản phẩm, với tổng số tiền xác định
            ) {
                level = lv;
                defaultItem = item;
            }
        }

        if (level == -1) return null;

        // vì km trên tổng sp nên điều kiện km như nhau
        if (checkQty.contains(type))
            multiple = (int)totalOrderQty / defaultItem.getSaleQty();
        else {
            if(isInclusiveTax) multiple = (int) (totalOrderAmtInTax / defaultItem.getSaleAmt());
            else multiple = (int) (totalOrderAmtExtax / defaultItem.getSaleAmt());
        }

        if (defaultItem != null && defaultItem.getDisPer() != null && defaultItem.getDisPer() > 0 &&
                // Mua 1 nhóm sản phẩm nào đó - với số lượng xác định (tổng), thì được giảm % tổng tiền
                (("zv07".equalsIgnoreCase(type) && defaultItem.getSaleQty() <= totalOrderQty) ||
                        //Mua 1 nhóm sản phẩm nào đó – với số tiền xác định (tổng), thì được giảm % tổng tiền của nhóm này
                        ("zv10".equalsIgnoreCase(type) && ((isInclusiveTax && totalOrderAmtInTax >= defaultItem.getSaleAmt()) ||
                                (!isInclusiveTax && totalOrderAmtExtax >= defaultItem.getSaleAmt()) ) ))
        ){
            double percent = defaultItem.getDisPer();
            if (checkMulti == MR_MULTIPLE || checkMulti == MR_MULTIPLE_RECURSIVE){ // nhân lên theo số bộ
                percent = percent * multiple;
            }
            if (checkMulti == MR_RECURSIVE || checkMulti == MR_MULTIPLE_RECURSIVE){ // tối ưu
                for (Map.Entry<Integer, List<PromotionProgramDetailDTO>> entry : mapOrderNumber.get(defaultItem.getProductId()).entrySet()){
                    if (entry.getKey() != null && entry.getKey() < level){
                        PromotionProgramDetailDTO dto = entry.getValue().get(0);
                        if (dto.getDisPer() != null)  percent = percent + dto.getDisPer();
                    }
                }
            }

            SalePromotionDiscountDTO discountDTO = new SalePromotionDiscountDTO();
            SalePromotionDTO salePromotion = new SalePromotionDTO();
            double amtInTax = totalOrderAmtInTax * percent / 100;
            double amtExTax = totalOrderAmtExtax * percent / 100;
            double amt = amtExTax;
            if (isInclusiveTax){ // exclusive vat
                amt = amtInTax;
            }
            discountDTO.setMaxAmount(amt);
            discountDTO.setAmount(amt);
            if(forSaving)
                discountDTO.setPercentage(percent);
            salePromotion.setTotalAmtInTax(amtInTax);
            salePromotion.setTotalAmtExTax(amtExTax);
            salePromotion.setAmount(discountDTO);

            if(forSaving) {
                List<SaleDiscountSaveDTO> saveInfo = new ArrayList<>();
                for (ProductOrderDetailDataDTO product : lstProductHasPromotion) {
                    SaleDiscountSaveDTO saveDTO = initSaleDiscountSaveDTO(product, level, percent, isInclusiveTax);
                    saveInfo.add(saveDTO);
                }
                discountDTO.setDiscountInfo(saveInfo);
            }

            return salePromotion;
        }
        else if (defaultItem != null && defaultItem.getDiscAmt() != null && defaultItem.getDiscAmt() > 0 &&
                //Mua 1 nhóm sản phẩm nào đó – với số lượng xác định (tổng), thì được giảm trừ tiền
                (("zv08".equalsIgnoreCase(type) && defaultItem.getSaleQty() <= totalOrderQty ) ||
                        //Mua 1 nhóm sản phẩm nào đó – với số tiền xác định (tổng), thì được giảm trừ 1 khoản tiền
                        ("zv11".equalsIgnoreCase(type) && ((isInclusiveTax && totalOrderAmtInTax >= defaultItem.getSaleAmt()) ||
                                (!isInclusiveTax && totalOrderAmtExtax >= defaultItem.getSaleAmt()) )))
        ){
            double amount = defaultItem.getDiscAmt();
            if (checkMulti == MR_MULTIPLE || checkMulti == MR_MULTIPLE_RECURSIVE){ // nhân lên theo số bộ
                amount = amount * multiple;
            }
            if (checkMulti == MR_RECURSIVE || checkMulti == MR_MULTIPLE_RECURSIVE){ // tối ưu
                for (Map.Entry<Integer, List<PromotionProgramDetailDTO>> entry : mapOrderNumber.get(defaultItem.getProductId()).entrySet()){
                    if (entry.getKey() != null && entry.getKey() < level){
                        PromotionProgramDetailDTO dto = entry.getValue().get(0);
                        if (dto.getDiscAmt() != null)  amount = amount + dto.getDiscAmt();
                    }
                }
            }
            SalePromotionDiscountDTO discountDTO = new SalePromotionDiscountDTO();
            SalePromotionDTO salePromotion = new SalePromotionDTO();
            double percent = calPercent(totalOrderAmtExtax, amount);
            if (isInclusiveTax){
                percent = calPercent(totalOrderAmtInTax, amount);
            }
            double amtInTax = totalOrderAmtInTax * percent / 100;
            double amtExTax = totalOrderAmtExtax * percent / 100;
            discountDTO.setMaxAmount(amount);
            discountDTO.setAmount(amount);
            if(forSaving)
                discountDTO.setPercentage(percent);
            salePromotion.setTotalAmtInTax(amtInTax);
            salePromotion.setTotalAmtExTax(amtExTax);
            salePromotion.setAmount(discountDTO);

            if(forSaving) {
                List<SaleDiscountSaveDTO> saveInfo = new ArrayList<>();
                for (ProductOrderDetailDataDTO product : lstProductHasPromotion) {
                    SaleDiscountSaveDTO saveDTO = initSaleDiscountSaveDTO(product, level, percent, isInclusiveTax);
                    saveInfo.add(saveDTO);
                }
                discountDTO.setDiscountInfo(saveInfo);
            }

            return salePromotion;
        }
        else if (defaultItem != null &&
                //Mua 1 nhóm sản phẩm nào đó – với số lượng xác định (tổng), thì được tặng 1 hoặc 1 nhóm sản phẩm nào đó
                (("zv09".equalsIgnoreCase(type) && defaultItem.getSaleQty() <= totalOrderQty)
                        //Mua 1 nhóm sản phẩm nào đó – với số tiền xác định (tổng), thì được tặng 1 hoặc nhóm sản phẩm nào đó
                        || ("zv12".equalsIgnoreCase(type) && ((isInclusiveTax && totalOrderAmtInTax >= defaultItem.getSaleAmt()) ||
                        (!isInclusiveTax && totalOrderAmtExtax >= defaultItem.getSaleAmt()) ))
                )
        ){
            //lấy sp km
            HashMap<Long,PromotionProgramDetailDTO> lstPromotionProduct = new HashMap<>();
            for (PromotionProgramDetailDTO dto : details){
                if(!lstPromotionProduct.containsKey(dto.getFreeProductId()) && dto.getOrderNumber() == level)
                    lstPromotionProduct.put(dto.getFreeProductId(),dto);
            }
            List<FreeProductDTO> lstProductPromotion = new ArrayList<>();
            Integer totalQty = null;
            int totalDisQty = 0;
            for (Map.Entry<Long, PromotionProgramDetailDTO> entry : lstPromotionProduct.entrySet()) {
                PromotionProgramDetailDTO dto = entry.getValue();
                FreeProductDTO freeProductDTO = productRepository.getFreeProductDTONoOrder(shopId, warehouseId, dto.getFreeProductId());
                if (freeProductDTO != null) {
                    int qty = dto.getFreeQty();
                    if (checkMulti == MR_MULTIPLE || checkMulti == MR_MULTIPLE_RECURSIVE){ // nhân lên theo số bộ
                        qty = qty * multiple;
                    }
                    if (checkMulti == MR_RECURSIVE || checkMulti == MR_MULTIPLE_RECURSIVE){ // tối ưu
                        for (Map.Entry<Integer, List<PromotionProgramDetailDTO>> entry1 : mapOrderNumber.get(defaultItem.getProductId()).entrySet()){
                            if (entry1.getKey() != null && entry1.getKey() < level){
                                for (PromotionProgramDetailDTO dto1 : entry1.getValue()){
                                    if (dto1.getFreeQty() != null)  qty = qty + dto1.getFreeQty();
                                }
                            }
                        }
                    }
                    if(totalQty == null)
                        totalQty = qty;

                    //lấy số tối đa
                    if (program.getRelation() == null || program.getRelation() == 0) {
                        freeProductDTO.setQuantity(qty);
                        freeProductDTO.setQuantityMax(qty);
                        if (qty > freeProductDTO.getStockQuantity())
                            freeProductDTO.setQuantity(freeProductDTO.getStockQuantity());
                    }else{
                        if (totalQty > 0){
                            freeProductDTO.setQuantity(qty);
                            if (totalQty > freeProductDTO.getStockQuantity()){
                                totalQty = totalQty - freeProductDTO.getStockQuantity();
                                freeProductDTO.setQuantity(freeProductDTO.getStockQuantity());
                            }
                            else{
                                totalQty = 0;
                                freeProductDTO.setQuantityMax(qty);
                            }
                        }
                    }
                    totalDisQty += freeProductDTO.getQuantity() == null? 0 : freeProductDTO.getQuantity();
                    lstProductPromotion.add(freeProductDTO);
                }
            }

            if (!lstProductPromotion.isEmpty()){
                SalePromotionDTO salePromotion = new SalePromotionDTO();
                salePromotion.setProducts(lstProductPromotion);
                salePromotion.setTotalQty(totalDisQty);
                return salePromotion;
            }
        }

        return null;
    }

    private SaleDiscountSaveDTO initSaleDiscountSaveDTO(ProductOrderDetailDataDTO product, Integer level, double percent, boolean isInclusiveTax){
        if(product == null) return null;

        SaleDiscountSaveDTO saveDTO = new SaleDiscountSaveDTO();
        saveDTO.setProductId(product.getProductId());
        saveDTO.setLevelNumber(level);
        saveDTO.setAmountExTax(product.getTotalPriceNotVAT() * percent / 100);
        saveDTO.setAmountInTax(product.getTotalPrice() * percent / 100);
        saveDTO.setAmount(saveDTO.getAmountExTax());
        saveDTO.setMaxAmount(saveDTO.getAmountExTax());
        if (isInclusiveTax) {
            saveDTO.setAmount(saveDTO.getAmountInTax());
            saveDTO.setMaxAmount(saveDTO.getAmountInTax());
        }
        return saveDTO;
    }

    /*
     *ZV19 to zv21
     */
    public SalePromotionDTO getZV19ToZV21(PromotionProgramDTO program, ProductOrderDataDTO orderData, Long shopId, Long warehouseId,
                                          double totalBeforeZV23InTax, double totalBeforeZV23ExTax, double totalZV23InTax, double totalZV23ExTax, boolean forSaving) {
        if (program == null || orderData == null || orderData.getProducts() == null || orderData.getProducts().isEmpty())
            return null;

        List<PromotionProgramDetailDTO> details = promotionClient.findPromotionProgramDetailV1(program.getId()).getData();
        if(details.isEmpty()) return null;

        // 1 mức theo 1 sản phẩm sẽ có nhiều item km
        HashMap<Integer, List<PromotionProgramDetailDTO>> mapOrderNumber = new HashMap<>();
        // gộp sản phẩm nếu có trùng
        for (PromotionProgramDetailDTO dto : details){
            if(dto.getSaleQty() == null) dto.setSaleQty(0);
            if(dto.getDisPer() == null) dto.setDisPer(0.0);
            if(dto.getDiscAmt() == null) dto.setDiscAmt(0.0);
            if (mapOrderNumber.containsKey(dto.getOrderNumber())){
                List<PromotionProgramDetailDTO> lst = mapOrderNumber.get(dto.getOrderNumber());
                lst.add(dto);
                mapOrderNumber.put(dto.getOrderNumber(), lst);
            }else{
                List<PromotionProgramDetailDTO> lst = new ArrayList<>();
                lst.add(dto);
                mapOrderNumber.put(dto.getOrderNumber(), lst);
            }
        }

        int multiple = 1; // tính bội số
        boolean isInclusiveTax = isInclusiveTax (program.getDiscountPriceType());
        int checkMulti = checkMultipleRecursive(program.getMultiple(), program.getRecursive());
        //lấy KM theo mức
        Integer level = -1;
        String type = program.getType() == null ? "" : program.getType().trim().toLowerCase();

        // get level number: so sánh trên tổng số mua -> chỉ cần lấy đại diện 1 row detail
        List<Integer> lstLevel = new ArrayList(mapOrderNumber.keySet());
        lstLevel.sort(Comparator.nullsFirst(Comparator.naturalOrder()));
        PromotionProgramDetailDTO newPromo = null;

        double totalAmountInTax = orderData.getTotalPrice() == null ? 0 : orderData.getTotalPrice();
        double totalAmountExTax = orderData.getTotalPriceNotVAT() == null ? 0 : orderData.getTotalPriceNotVAT();
        if (program.getAmountOrderType() != null){
            if(program.getAmountOrderType() == 1){
                totalAmountInTax = totalAmountInTax - totalBeforeZV23InTax;
                totalAmountExTax = totalAmountExTax - totalBeforeZV23ExTax;
            }else if(program.getAmountOrderType() == 2){
                totalAmountInTax = totalAmountInTax - totalBeforeZV23InTax - totalZV23InTax;
                totalAmountExTax = totalAmountExTax - totalBeforeZV23ExTax - totalZV23ExTax;
            }
        }

        if (orderData.getTotalPriceNotVAT() != null && orderData.getTotalPriceNotVAT() > 0 &&
                orderData.getTotalPriceNotVAT() != null && orderData.getTotalPriceNotVAT() > 0
        ) {
            for (Integer lv : lstLevel) {
                // vì km trên tổng nên điều kiện km như nhau
                PromotionProgramDetailDTO item = mapOrderNumber.get(lv).get(0);
                // kiểm tra điều kiện mua
                if ( item.getSaleAmt() != null && item.getSaleAmt() > 0 && ((!isInclusiveTax && totalAmountExTax >= item.getSaleAmt())// km trước thuế
                        || (isInclusiveTax && totalAmountInTax >= item.getSaleAmt())// km sau thuế
                )
            ){
                    level = lv;
                    newPromo = item;
                }
            }
        }

        if (level == -1) return null;

        // vì km trên tổng tiền đơn hàng nên điều kiện km như nhau
        if (isInclusiveTax)
            multiple = (int)(totalAmountInTax / newPromo.getSaleAmt());
        else
            multiple = (int)(totalAmountExTax / newPromo.getSaleAmt());

        if (newPromo != null && newPromo.getSaleAmt() != null && newPromo.getSaleAmt() > 0){
            //Tính theo trị giá đơn hàng, nếu đạt tổng tiền xác định, sẽ được giảm % trên đơn hàng (ZV không bội số, tối ưu)
            if ("zv19".equalsIgnoreCase(type) && newPromo.getDisPer() != null && newPromo.getDisPer() > 0){
                double percent = newPromo.getDisPer();
                /*if (checkMulti == MR_MULTIPLE || checkMulti == MR_MULTIPLE_RECURSIVE){ // nhân lên theo số bộ
                    percent = percent * multiple;
                }
                if (checkMulti == MR_RECURSIVE || checkMulti == MR_MULTIPLE_RECURSIVE){ // tối ưu
                    for (Map.Entry<Integer, List<PromotionProgramDetailDTO>> entry : mapOrderNumber.entrySet()){
                        if (entry.getKey() != null && entry.getKey() < level){
                            PromotionProgramDetailDTO dto = entry.getValue().get(0);
                            if (dto.getDisPer() != null)  percent = percent + dto.getDisPer();
                        }
                    }
                }*/

                SalePromotionDiscountDTO discountDTO = new SalePromotionDiscountDTO();
                SalePromotionDTO salePromotion = new SalePromotionDTO();

                double amtInTax = totalAmountInTax * percent / 100;
                double amtExTax = totalAmountExTax * percent / 100;
                double amt = amtExTax;
                if (isInclusiveTax){ // exclusive vat
                    amt = amtInTax;
                }
                discountDTO.setMaxAmount(amt);
                discountDTO.setAmount(amt);
                discountDTO.setPercentage(percent);
                salePromotion.setTotalAmtInTax(amtInTax);
                salePromotion.setTotalAmtExTax(amtExTax);

                salePromotion.setAmount(discountDTO);

                if(forSaving) {
                    List<SaleDiscountSaveDTO> saveInfo = new ArrayList<>();
                    for (ProductOrderDetailDataDTO product : orderData.getProducts()) {
                        SaleDiscountSaveDTO saveDTO = initSaleDiscountSaveDTO(product, level, percent, isInclusiveTax);
                        saveInfo.add(saveDTO);
                    }
                    discountDTO.setDiscountInfo(saveInfo);
                }

                return salePromotion;
            }
            //Tính theo trị giá đơn hàng, nếu đạt tổng tiền xác định, sẽ được giảm trừ 1 số tiền xác định trước
            else if (newPromo != null && "zv20".equalsIgnoreCase(type) && newPromo.getDiscAmt() != null && newPromo.getDiscAmt() > 0){
                double amount = newPromo.getDiscAmt();
                if (checkMulti == MR_MULTIPLE || checkMulti == MR_MULTIPLE_RECURSIVE){ // nhân lên theo số bộ
                    amount = amount * multiple;
                }
                if (checkMulti == MR_RECURSIVE || checkMulti == MR_MULTIPLE_RECURSIVE){ // tối ưu
                    for (Map.Entry<Integer, List<PromotionProgramDetailDTO>> entry : mapOrderNumber.entrySet()){
                        if (entry.getKey() != null && entry.getKey() < level){
                            PromotionProgramDetailDTO dto = entry.getValue().get(0);
                            if (dto.getDiscAmt() != null)  amount = amount + dto.getDiscAmt();
                        }
                    }
                }

                SalePromotionDiscountDTO discountDTO = new SalePromotionDiscountDTO();
                SalePromotionDTO salePromotion = new SalePromotionDTO();
                double percent = calPercent(totalAmountExTax, amount);
                if (isInclusiveTax){
                    percent = calPercent(totalAmountInTax, amount);
                }
                double amtInTax = totalAmountInTax * percent / 100;
                double amtExTax = totalAmountExTax * percent / 100;
                discountDTO.setMaxAmount(amount);
                discountDTO.setAmount(amount);
                if(forSaving)
                    discountDTO.setPercentage(percent);
                salePromotion.setTotalAmtInTax(amtInTax);
                salePromotion.setTotalAmtExTax(amtExTax);
                salePromotion.setAmount(discountDTO);
                if(forSaving) {
                    List<SaleDiscountSaveDTO> saveInfo = new ArrayList<>();
                    for (ProductOrderDetailDataDTO product : orderData.getProducts()) {
                        SaleDiscountSaveDTO saveDTO = initSaleDiscountSaveDTO(product, level, percent, isInclusiveTax);
                        saveInfo.add(saveDTO);
                    }
                    discountDTO.setDiscountInfo(saveInfo);
                }
                return salePromotion;
            }
            //Tính theo trị giá đơn hàng, nếu đạt tổng tiền xác định, sẽ được tặng 1 hoặc nhóm sản phẩm
            else if (newPromo != null && "zv21".equalsIgnoreCase(type)){
                Integer totalQty = null;
                int totalDisQty = 0;
                List<FreeProductDTO> lstProductPromotion = new ArrayList<>();
                for ( PromotionProgramDetailDTO dto : mapOrderNumber.get(level)){
                    FreeProductDTO freeProductDTO = productRepository.getFreeProductDTONoOrder(shopId, warehouseId, dto.getFreeProductId());
                    if (freeProductDTO != null) {
                        int qty = dto.getFreeQty();
                        if (checkMulti == MR_MULTIPLE || checkMulti == MR_MULTIPLE_RECURSIVE){ // nhân lên theo số bộ
                            qty = qty * multiple;
                        }
                        if (checkMulti == MR_RECURSIVE || checkMulti == MR_MULTIPLE_RECURSIVE){ // tối ưu
                            for (Map.Entry<Integer, List<PromotionProgramDetailDTO>> entry : mapOrderNumber.entrySet()){
                                if (entry.getKey() != null && entry.getKey() < level){
                                    for (PromotionProgramDetailDTO dto1 : entry.getValue()){
                                        if (dto1.getFreeQty() != null)  qty = qty + dto1.getFreeQty();
                                    }
                                }
                            }
                        }
                        if(totalQty == null)
                            totalQty = qty;

                        //lấy số tối đa
                        if (program.getRelation() == null || program.getRelation() == 0) {
                            freeProductDTO.setQuantity(qty);
                            freeProductDTO.setQuantityMax(qty);
                            if (qty > freeProductDTO.getStockQuantity())
                                freeProductDTO.setQuantity(freeProductDTO.getStockQuantity());
                        }else{
                            if (totalQty > 0){
                                freeProductDTO.setQuantity(qty);
                                if (totalQty > freeProductDTO.getStockQuantity()){
                                    totalQty = totalQty - freeProductDTO.getStockQuantity();
                                    freeProductDTO.setQuantity(freeProductDTO.getStockQuantity());
                                }
                                else{
                                    totalQty = 0;
                                    freeProductDTO.setQuantityMax(qty);
                                }
                            }
                        }
                        totalDisQty += freeProductDTO.getQuantity() == null? 0 : freeProductDTO.getQuantity();
                        lstProductPromotion.add(freeProductDTO);
                    }
                }

                if (!lstProductPromotion.isEmpty()){
                    SalePromotionDTO salePromotion = new SalePromotionDTO();
                    salePromotion.setProducts(lstProductPromotion);
                    salePromotion.setTotalQty(totalDisQty);
                    return salePromotion;
                }
            }
        }

        return null;
    }

    //Kiểm tra các chwuong trình hợp lệ
    public List<PromotionProgramDTO> validPromotionProgram(OrderPromotionRequest request, Long shopId, CustomerDTO customer) {

        List<PromotionProgramDTO> programs = promotionClient.findPromotionPrograms(shopId).getData();
        // Kiểm tra loại đơn hàng tham gia & Kiểm tra thuộc tính khách hàng tham gia
        return programs.stream().filter(program -> {
            //Kiểm tra giới hạn số lần được KM của KH
            Integer numberDiscount = saleOrderDiscountRepo.countDiscount(shopId, customer.getId());
            if(program.getPromotionDateTime() != null && program.getPromotionDateTime() <= numberDiscount) return false;

            // Kiểm tra loại đơn hàng tham gia
            if(program.getObjectType() != null && program.getObjectType() != 0) {
                Set<Long> orderTypes = promotionClient.findCusCardPromotion(program.getId(), PromotionCustObjectType.ORDER_TYPE.getValue()).getData();
                if(!orderTypes.contains(Long.valueOf(request.getOrderType()))) return false;
            }
            // Kiểm tra thuộc tính khách hàng tham gia
            Set<Long> customerTypes = promotionClient.findCusCardPromotion(program.getId(), PromotionCustObjectType.CUSTOMER_TYPE.getValue()).getData();
            if(!customerTypes.isEmpty() && !customerTypes.contains(customer.getCustomerTypeId())) return false;

            Set<Long> memberCards = promotionClient.findCusCardPromotion(program.getId(), PromotionCustObjectType.MEMBER_CARD.getValue()).getData();
            MemberCardDTO memberCard = memberCardClient.getByCustomerId(customer.getId()).getData();
            if(!memberCards.isEmpty() && !memberCards.contains(memberCard!=null?memberCard.getId():null)) return false;

            Set<Long> loyalCustomers = promotionClient.findCusCardPromotion(program.getId(), PromotionCustObjectType.LOYAL_CUSTOMER.getValue()).getData();
            if(!loyalCustomers.isEmpty() && !loyalCustomers.contains(customer.getCloselyTypeId())) return false;

            Set<Long> customerCardTypes = promotionClient.findCusCardPromotion(program.getId(), PromotionCustObjectType.CUSTOMER_CARD_TYPE.getValue()).getData();
            if(!customerCardTypes.isEmpty() && !customerCardTypes.contains(customer.getCardTypeId())) return false;
            // Kiểm tra quy định tính chiết khấu của đơn hàng

            return true;
        }).collect(Collectors.toList());
    }

    public ProductOrderDataDTO getProductOrderData(OrderPromotionRequest request, CustomerDTO customer) {
        ProductOrderDataDTO orderDataDTO = new ProductOrderDataDTO(request.getOrderType());
        List<ProductOrderRequest> products = request.getProducts();

        //Gộp sản phẩm combo - nếu sản phẩm Combo có chứa các sản phẩm thỏa CTKM thì vẫn được hưởng KM
        Map<Long, ProductOrderRequest> productMaps = products.stream().collect(Collectors.toMap(ProductOrderRequest::getProductId, Function.identity()));
        List<ProductOrderRequest> productCombos = products.stream().filter(ProductOrderRequest::isCombo).collect(Collectors.toList());
        for (ProductOrderRequest combo: productCombos) {
            Product product = productRepository.findById(combo.getProductId())
                    .orElseThrow(() -> new ValidateException(ResponseMessage.PRODUCT_DOES_NOT_EXISTS));
            List<ComboProductDetail> comboDetails = comboProductDetailRepo.findByComboProductIdAndStatus(product.getComboProductId(), 1);
            for(ComboProductDetail detail: comboDetails) {
                if(productMaps.containsKey(detail.getProductId())){
                    ProductOrderRequest productOrder = productMaps.get(detail.getProductId());
                    productOrder.setQuantity(productOrder.getQuantity() + (combo.getQuantity()*detail.getFactor()));
                }else{
                    Product productDb = productRepository.findById(detail.getProductId())
                            .orElseThrow(() -> new ValidateException(ResponseMessage.PRODUCT_DOES_NOT_EXISTS));
                    ProductOrderRequest productOrder = new ProductOrderRequest();
                    productOrder.setProductId(detail.getProductId());
                    productOrder.setProductCode(productDb.getProductCode());
                    productOrder.setQuantity(combo.getQuantity()*detail.getFactor());
                    productMaps.put(detail.getProductId(), productOrder);
                }
            }
        }

        List<ProductOrderRequest> productOrders = new ArrayList<>(productMaps.values());
        for (ProductOrderRequest product: productOrders) {
            Price price = productPriceRepo.getProductPrice(product.getProductId(), customer.getCustomerTypeId());
            if(price == null) throw new ValidateException(ResponseMessage.NO_PRICE_APPLIED);
            ProductOrderDetailDataDTO detail = new ProductOrderDetailDataDTO();
            detail.setProductId(product.getProductId());
            detail.setProductCode(product.getProductCode());
            detail.setQuantity(product.getQuantity());
            detail.setPrice(price.getPrice());
            detail.setPriceNotVAT(price.getPriceNotVat());

            orderDataDTO.addProduct(detail);
            orderDataDTO.addQuantity(detail.getQuantity());
            orderDataDTO.addTotalPrice(detail.getTotalPrice());
            orderDataDTO.addTotalPriceNotVAT(detail.getTotalPriceNotVAT());
        }
        return orderDataDTO;
    }

}








