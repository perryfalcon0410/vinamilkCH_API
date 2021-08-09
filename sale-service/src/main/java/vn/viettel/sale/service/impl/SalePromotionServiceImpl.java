package vn.viettel.sale.service.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.ShopParamDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.customer.MemberCardDTO;
import vn.viettel.core.dto.promotion.*;
import vn.viettel.core.enums.PromotionCustObjectType;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.util.ValidationUtils;
import vn.viettel.sale.entities.Price;
import vn.viettel.sale.entities.Product;
import vn.viettel.sale.entities.SaleOrder;
import vn.viettel.sale.messaging.OrderPromotionRequest;
import vn.viettel.sale.messaging.ProductOrderRequest;
import vn.viettel.sale.messaging.SalePromotionCalItemRequest;
import vn.viettel.sale.messaging.SalePromotionCalculationRequest;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.SalePromotionService;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.service.enums.PromotionProgramType;
import vn.viettel.sale.service.feign.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SalePromotionServiceImpl extends BaseServiceImpl<SaleOrder, SaleOrderRepository> implements SalePromotionService {

    private final int MR_NO = 1;
    private final int MR_MULTIPLE = 2;
    private final int MR_RECURSIVE = 3;
    private final int MR_MULTIPLE_RECURSIVE = 4;
    private static final double UNINITIALIZED = 0.0;

    @Autowired
    PromotionClient promotionClient;
    @Autowired
    CustomerClient customerClient;
    @Autowired
    MemberCardClient memberCardClient;
    @Autowired
    ShopClient shopClient;
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

    /*
    Lấy danh sách các khuyến mãi cho 1 đơn hàng
     */
    @Override
    public SalePromotionCalculationDTO getSaleItemPromotions(OrderPromotionRequest request, Long shopId,
                                                             HashMap<Long,Double> mapMoneys, boolean forSaving) {
        List<SalePromotionDTO> results = new ArrayList<>();
        CustomerDTO customer = customerClient.getCustomerByIdV1(request.getCustomerId()).getData();
        if(customer == null) throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
        // get default warehouse
        Long warehouseTypeId = customerTypeClient.getWarehouseTypeByShopId(shopId);
        if (warehouseTypeId == null)
            throw new ValidateException(ResponseMessage.WARE_HOUSE_NOT_EXIST);

        // Danh sách chương trình khuyến mãi thỏa các điều kiện cửa hàng, khách hàng
        List<PromotionProgramDTO> programs = this.validPromotionProgram(request, shopId, customer);
        if(programs == null || programs.isEmpty()) return null;

        ProductOrderDataDTO orderData = this.getProductOrderData(request, customer);
        if (orderData == null || orderData.getProducts() == null || orderData.getProducts().isEmpty())
            return null;

        //key PromotionProgramDTO, value tính trước zv23 = true, sau zv23 = false
        HashMap<PromotionProgramDTO,Boolean> mapZV192021 = new HashMap<>();
        List<PromotionProgramDTO> lstZV23 = new ArrayList<>();
        HashMap<PromotionProgramDTO,Double> lstZmType3 = new HashMap<>();
        //sort to get zv19, zv20, zv21
        programs.sort(Comparator.comparing(PromotionProgramDTO::getType));

        for (PromotionProgramDTO program: programs) {
            Double amount = 0D;
            if(mapMoneys != null) amount = mapMoneys.get(program.getId());

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
                    this.addItemPromotion(results, this.getAutoItemPromotionZV01ToZV21(program, orderData, shopId, warehouseTypeId, 0,0,0,0, forSaving));
                    break;
                case ZV19:
                case ZV20:
                case ZV21:
                    if (program.getAmountOrderType() != null && program.getAmountOrderType() == 2){//sau zv23
                        mapZV192021.put(program, false);
                    }else if (program.getAmountOrderType() != null && program.getAmountOrderType() == 1)//trước zv23
                        mapZV192021.put(program, true);
                    else mapZV192021.put(program, null);
                    break;
                case ZV23:
                    lstZV23.add(program);
                    break;
                case ZM:
                    if (program.getGivenType() != null && program.getGivenType() == 3) lstZmType3.put(program,amount);
                    else this.addItemPromotion(results, this.getItemPromotionZM(program, orderData, shopId, warehouseTypeId, amount,
                            customer.getCustomerCode(), 0, 0, forSaving));
                    break;
                default:
                    // Todo
            }
        }

        double totalZV0118zmInTax = 0;
        double totalZV0118zmExTax = 0;
        double totalZV1921InTax = 0;
        double totalZV1921ExTax = 0;
        double totalZV23InTax = 0;
        double totalZV23ExTax = 0;
        double promotionAmount = 0;
        double promotionAmountExTax = 0;
        double paymentAmount = 0;
        for (SalePromotionDTO item : results){
            if(item.getIsUse() != null && item.getIsUse()) {
                totalZV0118zmInTax += item.getTotalAmtInTax() == null ? 0 : item.getTotalAmtInTax();
                totalZV0118zmExTax += item.getTotalAmtExTax() == null ? 0 : item.getTotalAmtExTax();
            }
        }
        promotionAmount = totalZV0118zmInTax;
        promotionAmountExTax = totalZV0118zmExTax;

        /*ví zv19 - 21 được tính với tổng tiền sau khi đã trừ hết các km khác nên phải kiểm tra riêng và sau tất cả các km
        Nếu có ctkm 19, 20, 21, và có khai báo gía trị tính trước / sau km 23 thì
        Tính KM   19 , 20, 21:
        - Tổng tiền đơn hàng : giữ nguyên cách tính như hiện tại, tức tính tiền khuyến mãi đơn hàng dựa trên tổng tiền mua hàng của sản phẩm trong đơn hàng.
        - Thành tiền trước ck zv23: tiền tính đạt khuyến mãi đơn hàng = tổng tiền mua đơn hàng - ( tiền khuyến mãi của các zv từ 1-> 18 + khuyến mãi tay) , sau đó mới tính tiếp tiền km của zv23
        - Thành tiền sau ck zv23: tiền tính đạt khuyến mãi đơn hàng = tổng tiền mua đơn hàng - ( tiền khuyến mãi của các zv từ 1-> 18 + khuyến mãi tay) - tiền km của zv23
         */
        //Tính zv19 - 21 trước zv23
        for(Map.Entry<PromotionProgramDTO, Boolean> entry: mapZV192021.entrySet()){
            if(entry.getValue() != null && entry.getValue()){
                SalePromotionDTO item = this.getAutoItemPromotionZV01ToZV21(entry.getKey(), orderData, shopId, warehouseTypeId,
                        totalZV0118zmInTax, totalZV0118zmExTax, totalZV23InTax, totalZV23ExTax, forSaving);
                if(item != null && item.getIsUse()) {
                    totalZV1921InTax += item.getTotalAmtInTax() == null ? 0 : item.getTotalAmtInTax();
                    totalZV1921ExTax += item.getTotalAmtExTax() == null ? 0 : item.getTotalAmtExTax();
                }
                this.addItemPromotion(results, item);
            }
        }
        promotionAmount += totalZV1921InTax;
        promotionAmountExTax += totalZV1921ExTax;

        //tính zv23
        for (PromotionProgramDTO programDTO : lstZV23){
            SalePromotionDTO item = this.getItemPromotionZV23(programDTO, orderData, shopId, warehouseTypeId, request.getCustomerId(),
                    totalZV0118zmInTax + totalZV1921InTax, totalZV0118zmExTax + totalZV1921ExTax, forSaving);
            if( item != null){
                if(item.getIsUse()) {
                    totalZV23InTax += item.getTotalAmtInTax() == null ? 0 : item.getTotalAmtInTax();
                    totalZV23ExTax += item.getTotalAmtExTax() == null ? 0 : item.getTotalAmtExTax();
                }
                this.addItemPromotion(results, item);
            }
        }
        promotionAmount += totalZV23InTax;
        promotionAmountExTax += totalZV23ExTax;

        //Tính zv19 - 21 sau zv23
        for(Map.Entry<PromotionProgramDTO, Boolean> entry: mapZV192021.entrySet()){
            if(entry.getValue() != null && !entry.getValue()){
                SalePromotionDTO item = this.getAutoItemPromotionZV01ToZV21(entry.getKey(), orderData, shopId, warehouseTypeId,
                        totalZV0118zmInTax, totalZV0118zmExTax, totalZV23InTax, totalZV23ExTax, forSaving);
                if(item != null && item.getIsUse()) {
                    if(item.getIsUse()) {
                        promotionAmount += item.getTotalAmtInTax() == null ? 0 : item.getTotalAmtInTax();
                        promotionAmountExTax += item.getTotalAmtExTax() == null ? 0 : item.getTotalAmtExTax();
                        totalZV1921InTax += item.getTotalAmtInTax() == null ? 0 : item.getTotalAmtInTax();
                        totalZV1921ExTax += item.getTotalAmtExTax() == null ? 0 : item.getTotalAmtExTax();
                    }
                    this.addItemPromotion(results, item);
                }
            }
        }

        /*Tính zv19 - 21 tính trên tổng tiên đơn hàng
        Còn nếu như   19, 20, 21 : vẫn là tính trên tổng tiền đơn hàng: thì cách tính như trước e nói, tính đạt trên tổng tiền  - km zv1->21 - km tay
         */
        for(Map.Entry<PromotionProgramDTO, Boolean> entry: mapZV192021.entrySet()){
            if(entry.getValue() == null){
                SalePromotionDTO item = this.getAutoItemPromotionZV01ToZV21(entry.getKey(), orderData, shopId, warehouseTypeId,
                        totalZV0118zmInTax + totalZV1921InTax, totalZV0118zmExTax + totalZV1921ExTax, 0, 0, forSaving);
                if(item != null && item.getIsUse()) {
                    if(item.getIsUse()) {
                        promotionAmount += item.getTotalAmtInTax() == null ? 0 : item.getTotalAmtInTax();
                        promotionAmountExTax += item.getTotalAmtExTax() == null ? 0 : item.getTotalAmtExTax();
                        totalZV1921InTax += item.getTotalAmtInTax() == null ? 0 : item.getTotalAmtInTax();
                        totalZV1921ExTax += item.getTotalAmtExTax() == null ? 0 : item.getTotalAmtExTax();
                    }
                    this.addItemPromotion(results, item);
                }
            }
        }
        /*
        zm give type 3 thì nó lại ở sau cùng, sau các tự động zv, sau cả zm type 0
         */
        for (Map.Entry<PromotionProgramDTO, Double> entry : lstZmType3.entrySet()){
            SalePromotionDTO item = this.getItemPromotionZM(entry.getKey(), orderData, shopId, warehouseTypeId, entry.getValue(),
                    customer.getCustomerCode(), promotionAmount, promotionAmountExTax, forSaving);
            if( item != null){
                if(item.getIsUse()) {
                    promotionAmount += item.getTotalAmtInTax() == null ? 0 : item.getTotalAmtInTax();
                    promotionAmountExTax += item.getTotalAmtExTax() == null ? 0 : item.getTotalAmtExTax();
                }
                this.addItemPromotion(results, item);
            }
        }

        promotionAmount = (double) Math.round(promotionAmount);
        paymentAmount = orderData.getTotalPrice() - promotionAmount;

        SalePromotionCalculationDTO calculationDTO = new SalePromotionCalculationDTO();
        calculationDTO.setLstSalePromotions(this.sortPromotions(results));
        calculationDTO.setPromotionAmount(promotionAmount);
        calculationDTO.setPromotionAmountExTax((double) Math.round(promotionAmountExTax));
        if(paymentAmount < 0) paymentAmount = 0;
        calculationDTO.setPaymentAmount(paymentAmount);
        calculationDTO.setLockVoucher(this.checkLockVoucher(shopId));

        return calculationDTO;
    }

    private void addItemPromotion(List<SalePromotionDTO> lstPromotion, SalePromotionDTO promotion){
        if (lstPromotion != null && promotion != null){
            lstPromotion.add(promotion);
        }
    }

    private Boolean checkLockVoucher(Long shopId) {
        ShopParamDTO shopParamDTO = shopClient.getShopParamV1("SALEMT_LIMITVC", "LIMITVC", shopId).getData();
        if(shopParamDTO!=null) {
            Integer maxNumber = Integer.valueOf(shopParamDTO.getName()!=null?shopParamDTO.getName():"0");
            Integer currentNumber = Integer.valueOf(shopParamDTO.getDescription()!=null?shopParamDTO.getDescription():"0");
            if(currentNumber > maxNumber) return true;
            return false;
        }
        return false;
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
            auto.setEditable(program.getIsEdited());
            if(auto.getProducts() == null || auto.getProducts().isEmpty() ||
                    (program.getRelation() != null && program.getRelation() == 0))  auto.setIsEditable(false);
            else auto.setIsEditable(true);
            if (program.getRelation() != null && program.getRelation() == 0)
                auto.setContraintType(0);
            else
                auto.setContraintType(1);
            LimitDto value = getPromotionLimit(auto, shopId);
            auto.setNumberLimited(value.getLimited());
            auto.setIsUse(value.isUsed());
            auto.setIsReturn(false);
            if(program.getIsReturn() != null && program.getIsReturn() == 1)
                auto.setIsReturn(true);
        }

        return auto;
    }

    /*
    Lấy danh sách khuyến mãi tay ZM
     */
    private SalePromotionDTO getItemPromotionZM(PromotionProgramDTO program, ProductOrderDataDTO orderData, Long shopId,  Long warehouseId,
                                                Double inputAmount, String customerCode, double amountProInTax, double amountProExTax, boolean forSaving){

        if (program == null || orderData == null || orderData.getProducts() == null || orderData.getProducts().isEmpty())
            return null;

        List<PromotionSaleProductDTO> details = promotionClient.findPromotionSaleProductByProgramIdV1(program.getId()).getData();

        double totalAmountInTax = orderData.getTotalPrice();
        double totalAmountExtax = orderData.getTotalPriceNotVAT();
        boolean flag = false;
        boolean isInclusiveTax = isInclusiveTax(program.getDiscountPriceType());

        //nếu có khai báo sp km thì kiểm tra đơn hàng mua phải có ít nhất 1 sản phẩm nằm trong tập spkm thì mới được hưởng KM/còn ko có SP thì hiểu là không quy định SP mua
        List<Long> lstProductIds = orderData.getProducts().stream().map(item -> item.getProductId()).distinct().collect(Collectors.toList());
        if(details.isEmpty()){
            flag = true;
        }else {
            for (PromotionSaleProductDTO productPromotion : details) {
                if (productPromotion.getProductId() == null ||
                        (productPromotion.getProductId() != null && lstProductIds.contains(productPromotion.getProductId()))) {
                    flag = true;
                    break;
                }
            }
        }

        if (flag){
            SalePromotionDTO salePromotion = null;

            if (program.getGivenType() != null && program.getGivenType() == 1){// tặng sản phẩm
                List<PromotionProductOpenDTO> freeProducts = promotionClient.getFreeItemV1(program.getId()).getData();
                salePromotion = new SalePromotionDTO();
                salePromotion.setProgramId(program.getId());
                salePromotion.setProgramType(program.getType());

                if(freeProducts != null) {
                    List<Long> productFreeIds = freeProducts.stream().map(i -> i.getProductId()).collect(Collectors.toList());
                    List<FreeProductDTO> products = productRepository.findFreeProductDTONoOrders(shopId, warehouseId, productFreeIds);
                    int totalQty = 0;
                    if (products != null) {
                        for (FreeProductDTO freeProductDTO : products) {
                            freeProductDTO.setQuantityMax(salePromotion.getNumberLimited()!=null?salePromotion.getNumberLimited().intValue():freeProductDTO.getStockQuantity());
                            totalQty += freeProductDTO.getQuantity();
                        }
                    }
                    salePromotion.setProducts(products);
                    salePromotion.setTotalQty(totalQty);
                }
                LimitDto value = getPromotionLimit(salePromotion, shopId);
                salePromotion.setNumberLimited(value.getLimited());
                salePromotion.setIsUse(false);
                salePromotion.setIsEditable(true);

            }else { //tặng tiền + % chỉ có discountAmount hoặc discountPercent
                List<PromotionProgramDiscountDTO> programDiscount = promotionClient.findPromotionDiscountByPromotion(program.getId()).getData();
                if (programDiscount == null || programDiscount.isEmpty())
                    return null;

                PromotionProgramDiscountDTO discountDTO = programDiscount.get(0);
                discountDTO.setProgram(program);
                /*
                Nếu given_type = 3 thì: bắt buộc KH mua hàng đơn hàng phải có mã customer_code giống với customer_code
                trong bảng promotion_discount thì mới đc hưởng ctkm tay này
                 */
                if(program.getGivenType() != null && program.getGivenType() == 3){
                    if(discountDTO.getIsUsed() == 1 || customerCode == null || discountDTO.getCustomerCode() == null || discountDTO.getCustomerCode().isEmpty() ||
                        !discountDTO.getCustomerCode().trim().equalsIgnoreCase(customerCode.trim())){
                        return null;
                    }
                    salePromotion = calZMAmount(orderData, shopId, discountDTO, totalAmountInTax - amountProInTax, totalAmountExtax - amountProExTax, isInclusiveTax,
                            inputAmount, forSaving, false);
                    if (salePromotion != null && forSaving) salePromotion.setDiscountDTO(discountDTO);
                }else {
                    salePromotion = calZMAmount(orderData, shopId, discountDTO, totalAmountInTax, totalAmountExtax, isInclusiveTax,
                            inputAmount, forSaving, false);
                }
            }

            if (salePromotion != null) {
                salePromotion.setReCalculated(true);
                salePromotion.setProgramId(program.getId());
                if(program.getGivenType() != null && program.getGivenType() == 3) salePromotion.setAffected(true);
                salePromotion.setLstProductHasPromtion(lstProductIds);
                salePromotion.setPromotionType(1);
                salePromotion.setProgramType(program.getType());
                salePromotion.setPromotionProgramCode(program.getPromotionProgramCode());
                salePromotion.setPromotionProgramName(program.getPromotionProgramName());
                if (program.getRelation() != null && program.getRelation() == 0)
                    salePromotion.setContraintType(0);
                else
                    salePromotion.setContraintType(1);

                salePromotion.setIsReturn(false);
                if(program.getIsReturn() != null && program.getIsReturn() == 1)
                    salePromotion.setIsReturn(true);
                return salePromotion;
            }
        }

        return null;
    }

    private SalePromotionDTO calZMAmount(ProductOrderDataDTO orderData, Long shopId, PromotionProgramDiscountDTO discountDTO, double totalAmountInTax,
                                         double totalAmountExtax, boolean isInclusiveTax, Double inputAmount, boolean forSaving, boolean forDiscountCode){
        double compareAmt = totalAmountInTax;
        if(!isInclusiveTax) compareAmt = totalAmountExtax;
        //đạt số tiền tối thiểu đơn hàng cần đạt
        if ( discountDTO.getMinSaleAmount() == null || discountDTO.getMinSaleAmount() >= compareAmt){
            return null;
        }
        PromotionProgramDTO program = discountDTO.getProgram();

        SalePromotionDTO salePromotion = null;

        if (discountDTO.getDiscountAmount() != null && discountDTO.getDiscountAmount() > 0) {
            SalePromotionDiscountDTO spDto = new SalePromotionDiscountDTO();
            salePromotion = new SalePromotionDTO();
            salePromotion.setProgramId(program.getId());
            salePromotion.setProgramType(program.getType());
            Double amount = discountDTO.getDiscountAmount();
            if(inputAmount != null && inputAmount > 0){
                amount = inputAmount;
            }
            double percent = (totalAmountInTax - totalAmountExtax )/ totalAmountExtax * 100;
            salePromotion.setTotalAmtInTax(amount);
            salePromotion.setTotalAmtExTax(amount / (( 100 + percent ) / 100));
            if(!isInclusiveTax){
                salePromotion.setTotalAmtInTax(amount * (( 100 + percent ) / 100));
                salePromotion.setTotalAmtExTax(amount);
            }
            spDto.setAmount(salePromotion.getTotalAmtInTax());
            if(!isInclusiveTax && forDiscountCode == false){
                spDto.setAmount(salePromotion.getTotalAmtExTax());
            }
            salePromotion.setIsEditable(false);

            if(forSaving) {
                spDto.setPercentage(percent);
                double percentTx = calPercent(totalAmountInTax, amount);
                spDto.setMaxAmount(amount);
                List<SaleDiscountSaveDTO> saveInfo = initSaleDiscountSaveDTO(orderData.getProducts(), 1, percentTx,
                        isInclusiveTax, salePromotion.getTotalAmtInTax(), salePromotion.getTotalAmtExTax());
                spDto.setDiscountInfo(saveInfo);
            }
            salePromotion.setAmount(spDto);
            LimitDto value = getPromotionLimit(salePromotion, shopId);
            salePromotion.setNumberLimited(value.getLimited());
            salePromotion.setIsUse(value.isUsed());
        } else if (discountDTO.getDiscountPercent() != null && discountDTO.getDiscountPercent() > 0) { // KM %
            SalePromotionDiscountDTO spDto = new SalePromotionDiscountDTO();
            double p = (totalAmountInTax - totalAmountExtax )/ totalAmountExtax * 100;
            double amtSaleInTax = totalAmountInTax;
            double amtSaleExTax = totalAmountExtax;
            compareAmt = amtSaleInTax;
            if(!isInclusiveTax) compareAmt = amtSaleExTax;
            //nếu có khai báo số tiền tính km tối đa và < số tiền mua
            if(discountDTO.getMaxSaleAmount() != null && discountDTO.getMaxSaleAmount() > 0 && discountDTO.getMaxSaleAmount() < compareAmt){
                amtSaleInTax = discountDTO.getMaxSaleAmount();
                amtSaleExTax = amtSaleInTax / (( 100 + p ) / 100);
                if(!isInclusiveTax){
                    amtSaleExTax = discountDTO.getMaxSaleAmount();
                    amtSaleInTax = amtSaleExTax * (( 100 + p ) / 100);
                }
            }
            double amtInTax = amtSaleInTax * discountDTO.getDiscountPercent() / 100;
            double amtExTax = amtSaleExTax * discountDTO.getDiscountPercent() / 100;
            compareAmt = amtInTax;
            if(!isInclusiveTax) compareAmt = amtExTax;
            //nếu khai báo số tiền tối đa && nếu tiền hưởng > tiền tối đa thì chỉ được lấy số tối đa
            if (discountDTO.getMaxDiscountAmount() != null && discountDTO.getMaxDiscountAmount() > 0 && compareAmt > discountDTO.getMaxDiscountAmount()) {
                amtInTax = discountDTO.getMaxDiscountAmount();
                amtExTax = amtInTax / (( 100 + p ) / 100);
                if(!isInclusiveTax){
                    amtExTax = discountDTO.getMaxDiscountAmount();
                    amtInTax = amtExTax * (( 100 + p ) / 100);
                }
            }
            double amount = amtExTax;
            if(isInclusiveTax){
                amount = amtInTax;
            }
            if(forDiscountCode) amount = amtInTax;
            spDto.setAmount(amount);
            salePromotion = new SalePromotionDTO();
            salePromotion.setProgramId(program.getId());
            salePromotion.setProgramType(program.getType());
            salePromotion.setTotalAmtInTax(amtInTax);
            salePromotion.setTotalAmtExTax(amtExTax);
            if(forSaving) {
                spDto.setPercentage(discountDTO.getDiscountPercent());
                double percent = calPercent(totalAmountInTax, amount);
                spDto.setMaxAmount(amount);
                List<SaleDiscountSaveDTO> saveInfo = initSaleDiscountSaveDTO(orderData.getProducts(), 1, percent,
                        isInclusiveTax(program.getDiscountPriceType()), salePromotion.getTotalAmtInTax(), salePromotion.getTotalAmtExTax());
                spDto.setDiscountInfo(saveInfo);
            }
            salePromotion.setAmount(spDto);
            salePromotion.setIsEditable(false);
            LimitDto value = getPromotionLimit(salePromotion, shopId);
            salePromotion.setNumberLimited(value.getLimited());
            salePromotion.setIsUse(value.isUsed());
        }else if(discountDTO.getMaxDiscountAmount() != null && discountDTO.getMaxDiscountAmount() > 0 && forDiscountCode == false){
            SalePromotionDiscountDTO spDto = new SalePromotionDiscountDTO();
            Double amount = 0D;
            salePromotion = new SalePromotionDTO();
            salePromotion.setProgramId(program.getId());
            salePromotion.setProgramType(program.getType());
            if(inputAmount != null && inputAmount > 0){
                amount = inputAmount;
            }
            double percent = (totalAmountInTax - totalAmountExtax )/ totalAmountExtax * 100;
            salePromotion.setTotalAmtInTax(amount);
            salePromotion.setTotalAmtExTax(amount / (( 100 + percent ) / 100));
            if(!isInclusiveTax){
                salePromotion.setTotalAmtInTax(amount * (( 100 + percent ) / 100));
                salePromotion.setTotalAmtExTax(amount);
            }
            spDto.setAmount(salePromotion.getTotalAmtInTax());
            if(!isInclusiveTax){
                spDto.setAmount(salePromotion.getTotalAmtExTax());
            }
            salePromotion.setIsEditable(true);
            spDto.setMaxAmount(discountDTO.getMaxDiscountAmount());

            if(forSaving) {
                spDto.setPercentage(percent);
                double percentTx = calPercent(totalAmountInTax, amount);
                List<SaleDiscountSaveDTO> saveInfo = initSaleDiscountSaveDTO(orderData.getProducts(), 1, percentTx,
                        isInclusiveTax, salePromotion.getTotalAmtInTax(), salePromotion.getTotalAmtExTax());
                spDto.setDiscountInfo(saveInfo);
            }
            salePromotion.setAmount(spDto);
            LimitDto value = getPromotionLimit(salePromotion, shopId);
            salePromotion.setNumberLimited(value.getLimited());
            salePromotion.setIsUse(false);
        }

        return salePromotion;
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
    private SalePromotionDTO getItemPromotionZV23(PromotionProgramDTO program, ProductOrderDataDTO orderData, Long shopId, Long warehouseId,
                                                  Long customerId, double totalBeforeZV23InTax, double totalBeforeZV23ExTax, boolean forSaving){
        if (program == null || orderData == null || orderData.getProducts() == null || orderData.getProducts().isEmpty())
            return null;
        /* lấy amount từ promotion service: lấy doanh số RPT_ZV23.TOTAL_AMOUNT của khách hàng
         Doanh số tại thời điểm mua = doanh số tổng hợp đồng bộ đầu ngày + doanh số phát sinh trong ngày */
        RPT_ZV23DTO rpt_zv23DTO = promotionClient.checkZV23RequireV1(program.getPromotionProgramCode(),customerId,shopId).getData();

        //phuongkim: comment do lúc lưu đơn hàng đã cập nhật vào bảng zv23
        /*Date now = new Date();
        List<SaleOrder> customerSOList = saleOrderRepository.findAll(Specification.where(
                SaleOderSpecification.hasFromDateToDate(DateUtils.convertFromDate(now),DateUtils.convertToDate(now)))
                .and(SaleOderSpecification.hasCustomerId(customerId)));
        double totalInDay = 0F;
        for(SaleOrder customerSO:customerSOList) {
            totalInDay = totalInDay + customerSO.getTotal();
        }*/
        Double totalCusAmount = 0.0;
        if(rpt_zv23DTO != null) totalCusAmount = rpt_zv23DTO.getTotalAmount()!=null?rpt_zv23DTO.getTotalAmount():0.0;
        // danh sách sản phẩm loại trừ theo id ctkm
        List<Long> promotionIds = new ArrayList<>();
        promotionIds.add(program.getId());
        List<PromotionProgramDetailDTO> details = promotionClient.findPromotionProgramDetailV1(program.getId()).getData();
        if(details == null || details.isEmpty()) return null;

        double amountExTax = 0;
        double amountInTax = 0;
        double amountZV23 = 0;
        double percent = 0;

        HashMap<Long,ProductOrderDetailDataDTO> lstProductHasPromotion = new HashMap<>();
        for (PromotionProgramDetailDTO pItem : details){
            if (pItem.getSaleAmt() != null)
                amountZV23 = pItem.getSaleAmt();
            if (pItem.getDisPer() != null)
                percent = pItem.getDisPer();
        }

        //	Nếu RPT_ZV23.TOTAL_AMOUNT <= PROMOTION_PROGRAM_DETAIL.SALE_AMT thì đạt CTKM ZV23
        if(totalCusAmount > amountZV23) return null;
        // lấy tổng tiền theo những sản phẩm quy định
        List<PromotionProgramProductDTO> programProduct = promotionClient.findByPromotionIdsV1(promotionIds).getData();
        List<PromotionProgramProductDTO> programProduct1 = new ArrayList<>();

        for (PromotionProgramProductDTO pProduct : programProduct) {
            if(pProduct.getType() != null && pProduct.getType() == 1) programProduct1.add(pProduct);
        }

        if (programProduct1 != null && !programProduct1.isEmpty()){
            for (PromotionProgramProductDTO exItem : programProduct1){
                if(exItem.getType() != null && exItem.getType() == 1) {
                    for (ProductOrderDetailDataDTO oItem : orderData.getProducts()) {
                        if (oItem.getProductId().equals(exItem.getProductId())) {
                            if (oItem.getTotalPrice() == null) oItem.setTotalPrice(0.0);
                            if (oItem.getTotalPriceNotVAT() == null) oItem.setTotalPriceNotVAT(0.0);

                            amountExTax += oItem.getTotalPriceNotVAT();
                            amountInTax += oItem.getTotalPrice();
                            if (!lstProductHasPromotion.containsKey(exItem.getProductId())) {
                                lstProductHasPromotion.put(exItem.getProductId(), oItem);
                            }
                        }
                    }
                }
            }
        }else { //nếu không quy định sản phẩm
            if (orderData.getTotalPrice() == null) orderData.setTotalPrice(0.0);
            if (orderData.getTotalPriceNotVAT() == null) orderData.setTotalPriceNotVAT(0.0);
            amountExTax = orderData.getTotalPriceNotVAT();
            amountInTax = orderData.getTotalPrice();

            for (ProductOrderDetailDataDTO oItem : orderData.getProducts()){
                if(!lstProductHasPromotion.containsKey(oItem.getProductId())){
                    lstProductHasPromotion.put(oItem.getProductId(), oItem);
                }
            }
            // loại trừ sản phẩm
            if (programProduct != null && !programProduct.isEmpty()){
                for (PromotionProgramProductDTO exItem : programProduct){
                    if(exItem.getType() != null && exItem.getType() == 2) {
                        for (ProductOrderDetailDataDTO oItem : orderData.getProducts()) {
                            if (oItem.getProductId().equals(exItem.getProductId())) {
                                if (oItem.getTotalPrice() == null) oItem.setTotalPrice(0.0);
                                if (oItem.getTotalPriceNotVAT() == null) oItem.setTotalPriceNotVAT(0.0);

                                amountInTax -= oItem.getTotalPrice();
                                amountExTax -= oItem.getTotalPriceNotVAT();
                                if (lstProductHasPromotion.containsKey(exItem.getProductId())) {
                                    lstProductHasPromotion.remove(exItem.getProductId());
                                }
                            }
                        }
                    }
                }
            }
        }
      /*  l.x. huong
        Chỗ này c Linh có trừ đó
        Mà do đang km trên phần tiền còn lại,
        Tiền đơn hàng là 7tr,  tiền đã tích luỹ là 70k,  tiền km 19 là 700k , lấy 7tr - 700k thì lớn hơn 3tr quy địh -70k
        Nên số tiền đc hưởng, là chit còn đc hưởng 3tr - số tiền kh đã tích luỹ
        Tí c Linh c ấy noted chi tiết hơn
        */

        //-	Số tiền còn lại có thể hưởng CTKM ZV23 = [số tiền quy định của ZV23] – [doanh số tính tới thời điểm mua]
        double amountRemain = amountZV23 - totalCusAmount;
        // -	Nếu [số tiền còn lại có thể hưởng CTKM ZV23] ≥ [giá trị mua hàng của đơn] (lưu ý cách tính giá chiết khấu, xem mục 6):
        //	Số tiền có thể hưởng CTKM ZV23 = [giá trị mua hàng của đơn]
        //-	Nếu [số tiền còn lại có thể hưởng CTKM ZV23] < [giá trị mua hàng của đơn] (lưu ý cách tính giá chiết khấu, xem mục 6):
        //	Số tiền có thể hưởng CTKM ZV23 = [số tiền còn lại có thể hưởng CTKM ZV23]
        //Đối với CTKM ZV23 thì sale_amt khi khai báo thì luôn mặc định được hiểu là số tiền sau thuế

        Double saveAmount = amountInTax;
        double p = (amountInTax-amountExTax)/amountExTax*100;
        if(amountRemain < amountInTax - totalBeforeZV23InTax) {
            amountInTax = amountRemain;
            amountExTax = amountInTax / (( 100 + p ) / 100);
        }else {
            //trừ đi km chiết khấu zv01 - zv18 và zm
            amountInTax = amountInTax - totalBeforeZV23InTax;
            amountExTax = amountExTax - totalBeforeZV23ExTax;
        }

        if (percent > 0 && amountInTax > 0){
            SalePromotionDiscountDTO discountDTO = new SalePromotionDiscountDTO();
            SalePromotionDTO salePromotion = new SalePromotionDTO();
            double amtInTax = amountInTax * percent / 100;
            double amtExTax = amountExTax * percent / 100;
            salePromotion.setTotalAmtInTax(amtInTax);
            salePromotion.setTotalAmtExTax(amtExTax);
            discountDTO.setAmount(salePromotion.getTotalAmtExTax());
            if (isInclusiveTax(program.getDiscountPriceType())){
                discountDTO.setAmount(salePromotion.getTotalAmtInTax());
            }
            salePromotion.setAmount(discountDTO);

            if(forSaving) {
                discountDTO.setPercentage(percent );
                discountDTO.setMaxAmount(discountDTO.getAmount());
                double pp = calPercent(saveAmount, amtInTax);
                List<SaleDiscountSaveDTO> saveInfo = initSaleDiscountSaveDTO(new ArrayList<>(lstProductHasPromotion.values()), 1,
                        pp, isInclusiveTax(program.getDiscountPriceType()), salePromotion.getTotalAmtInTax(), salePromotion.getTotalAmtExTax());
                discountDTO.setDiscountInfo(saveInfo);
            }
            salePromotion.setLstProductHasPromtion(new ArrayList<>(lstProductHasPromotion.keySet()));
            salePromotion.setAffected(true);
            salePromotion.setReCalculated(true);
            salePromotion.setPromotionType(0);
            salePromotion.setProgramId(program.getId());
            salePromotion.setProgramType(program.getType());
            salePromotion.setPromotionProgramCode(program.getPromotionProgramCode());
            salePromotion.setPromotionProgramName(program.getPromotionProgramName());
            salePromotion.setIsEditable(false);
            salePromotion.setEditable(program.getIsEdited());
            salePromotion.setContraintType(1);
            if (program.getRelation() != null && program.getRelation() == 0) salePromotion.setContraintType(0);
            salePromotion.setZv23Amount(saveAmount);
            LimitDto value = getPromotionLimit(salePromotion, shopId);
            salePromotion.setNumberLimited(value.getLimited());
            salePromotion.setIsUse(value.isUsed());
            salePromotion.setIsReturn(false);
            if(program.getIsReturn() != null && program.getIsReturn() == 1)
                salePromotion.setIsReturn(true);

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
        double promotionAmountExTax = 0;
        double paymentAmount = 0;

        if (calculationRequest.getTotalOrderAmount() != null && calculationRequest.getTotalOrderAmount() != 0){
            paymentAmount = calculationRequest.getTotalOrderAmount();

            //tính tiền khuyến mãi
            if (calculationRequest.getPromotionInfo() != null && calculationRequest.getPromotionInfo().size() > 0){
                List<String> lstType = Arrays.asList("zv19", "zv20", "zv21");

                double totalZV0118zmInTax = 0;
                double totalZV0118zmExTax = 0;
                double totalZV1921InTax = 0;
                double totalZV1921ExTax = 0;
                double totalZV23InTax = 0;
                double totalZV23ExTax = 0;
                // value tính km trước zv23 = true, sau = false
                HashMap<PromotionProgramDTO,Boolean> lstZV1921 = new HashMap<>();
                HashMap<PromotionProgramDTO,Double> lstZmType3 = new HashMap<>();
                List<PromotionProgramDTO> lstZV23 = new ArrayList<>();
                List<PromotionProgramDTO> programDTOS = promotionClient.getByIdsV1(calculationRequest.getPromotionInfo().stream().map(item ->
                        item.getProgramId()).distinct().filter(Objects::nonNull).collect(Collectors.toList())).getData();
                // get default warehouse
                Long wareHouseTypeId = customerTypeClient.getWarehouseTypeByShopId(shopId);
                if (wareHouseTypeId == null) throw new ValidateException(ResponseMessage.WARE_HOUSE_NOT_EXIST);
                CustomerDTO customer = customerClient.getCustomerByIdV1(calculationRequest.getOrderRequest().getCustomerId()).getData();
                if(customer == null) throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
                ProductOrderDataDTO orderData = this.getProductOrderData(calculationRequest.getOrderRequest(), customer);
                if (orderData == null || orderData.getProducts() == null || orderData.getProducts().isEmpty())
                    throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);

                for (SalePromotionCalItemRequest item : calculationRequest.getPromotionInfo()){
                    if (item.getProgramId() != null){
                        PromotionProgramDTO programDTO = null;
                        if(programDTOS != null){
                            for(PromotionProgramDTO program : programDTOS){
                                if(program.getId().equals(item.getProgramId())){
                                    programDTO = program; break;
                                }
                            }
                        }
                        if (programDTO != null){
                            if("zv23".equalsIgnoreCase(programDTO.getType().trim())){
                                lstZV23.add(programDTO);
                            }else if (lstType.contains(programDTO.getType().trim().toLowerCase())){
                                if(programDTO.getAmountOrderType() != null && programDTO.getAmountOrderType() == 2)
                                    lstZV1921.put(programDTO, false);
                                else lstZV1921.put(programDTO, true);
                            }else if(item.getAmount() != null && item.getAmount().getAmount() != null){
                                SalePromotionDTO salePromotionDTO = null;
                                if("ZM".equalsIgnoreCase(programDTO.getType().trim())){
                                    if(programDTO.getGivenType() != null && programDTO.getGivenType() == 3)
                                        lstZmType3.put(programDTO, item.getAmount().getAmount());
                                    else
                                        salePromotionDTO = this.getItemPromotionZM(programDTO, orderData, shopId, wareHouseTypeId,
                                                item.getAmount().getAmount(), customer.getCustomerCode(), 0, 0, true);
                                }else{
                                     salePromotionDTO = this.getAutoItemPromotionZV01ToZV21(programDTO, orderData, shopId, wareHouseTypeId,
                                            totalZV0118zmInTax, totalZV0118zmExTax, totalZV23InTax, totalZV23ExTax, true);
                                }
                                if(salePromotionDTO!=null) {
                                    promotionAmount += salePromotionDTO.getTotalAmtInTax();
                                    promotionAmountExTax += salePromotionDTO.getTotalAmtExTax();
                                    totalZV0118zmInTax += salePromotionDTO.getTotalAmtInTax();
                                    totalZV0118zmExTax += salePromotionDTO.getTotalAmtExTax();
                                }
                            }
                        }
                    }
                }

                if((!lstZV1921.isEmpty() || !lstZV23.isEmpty()) && calculationRequest.getOrderRequest() != null){
                    List<SalePromotionDTO> resultZV1921 = new ArrayList<>();

                    // tính zv19 20 21 trước zv23
                    for (Map.Entry<PromotionProgramDTO, Boolean> entry : lstZV1921.entrySet()){
                        if(entry.getValue()) {
                            PromotionProgramDTO programItem = entry.getKey();
                            SalePromotionDTO salePromotionDTO = this.getAutoItemPromotionZV01ToZV21(programItem, orderData, shopId, wareHouseTypeId,
                                    totalZV0118zmInTax, totalZV0118zmExTax, totalZV23InTax, totalZV23ExTax, true);
                            if (salePromotionDTO != null) {
                                if ("zv19".equalsIgnoreCase(programItem.getType().trim()) || "zv20".equalsIgnoreCase(programItem.getType().trim())) {
                                    if(salePromotionDTO.getIsUse()) {
                                        promotionAmount += salePromotionDTO.getTotalAmtInTax();
                                        promotionAmountExTax += salePromotionDTO.getTotalAmtExTax();
                                        totalZV1921InTax += salePromotionDTO.getTotalAmtInTax();
                                        totalZV1921ExTax += salePromotionDTO.getTotalAmtExTax();
                                    }
                                    if ("zv20".equalsIgnoreCase(programItem.getType().trim())) {
                                        salePromotionDTO.getAmount().setPercentage(null);
                                    }
                                    salePromotionDTO.getAmount().setDiscountInfo(null);
                                }
                                resultZV1921.add(salePromotionDTO);
                            }
                        }
                    }

                    //tính zv23
                    for (PromotionProgramDTO programDTO : lstZV23){
                        SalePromotionDTO item = this.getItemPromotionZV23(programDTO, orderData, shopId, wareHouseTypeId, calculationRequest.getOrderRequest().getCustomerId(),
                                totalZV0118zmInTax + totalZV1921InTax, totalZV0118zmExTax + totalZV1921ExTax, false);
                        resultZV1921.add(item);
                        if(item.getIsUse()) {
                            totalZV23InTax += item.getTotalAmtInTax() == null ? 0 : item.getTotalAmtInTax();
                            totalZV23ExTax += item.getTotalAmtExTax() == null ? 0 : item.getTotalAmtExTax();
                            promotionAmount += item.getTotalAmtInTax() == null ? 0 : item.getTotalAmtInTax();
                            promotionAmountExTax += item.getTotalAmtExTax() == null ? 0 : item.getTotalAmtExTax();
                        }
                    }

                    // tính zv19 20 21 sau zv23
                    for (Map.Entry<PromotionProgramDTO, Boolean> entry : lstZV1921.entrySet()){
                        if(!entry.getValue()) {
                            PromotionProgramDTO programItem = entry.getKey();
                            SalePromotionDTO salePromotionDTO = this.getAutoItemPromotionZV01ToZV21(programItem, orderData, shopId, wareHouseTypeId,
                                    totalZV0118zmInTax, totalZV0118zmExTax, totalZV23InTax, totalZV23ExTax, true);
                            if (salePromotionDTO != null) {
                                if ("zv19".equalsIgnoreCase(programItem.getType().trim()) || "zv20".equalsIgnoreCase(programItem.getType().trim())) {
                                    if (salePromotionDTO.getIsUse()) { //inclusive tax
                                        promotionAmount += salePromotionDTO.getTotalAmtInTax() == null ? 0 : salePromotionDTO.getTotalAmtInTax();
                                        promotionAmountExTax += salePromotionDTO.getTotalAmtExTax() == null ? 0 : salePromotionDTO.getTotalAmtExTax();
                                    }
                                    if ("zv20".equalsIgnoreCase(programItem.getType().trim())) {
                                        salePromotionDTO.getAmount().setPercentage(null);
                                    }
                                    salePromotionDTO.getAmount().setDiscountInfo(null);
                                }
                                resultZV1921.add(salePromotionDTO);
                            }
                        }
                    }

                    // tính zm give type 3 thì nó lại ở sau cùng, sau các tự động zv, sau cả zm type 0
                    for (Map.Entry<PromotionProgramDTO, Double> entry : lstZmType3.entrySet()){
                        PromotionProgramDTO programItem = entry.getKey();
                        SalePromotionDTO salePromotionDTO = this.getItemPromotionZM(programItem, orderData, shopId, wareHouseTypeId,
                                entry.getValue(), customer.getCustomerCode(), promotionAmount, promotionAmountExTax, true);
                        if (salePromotionDTO != null) {
                            if (salePromotionDTO.getIsUse()) {
                                promotionAmount += salePromotionDTO.getTotalAmtInTax() == null ? 0 : salePromotionDTO.getTotalAmtInTax();
                                promotionAmountExTax += salePromotionDTO.getTotalAmtExTax() == null ? 0 : salePromotionDTO.getTotalAmtExTax();
                            }
                            resultZV1921.add(salePromotionDTO);
                        }
                    }

                    if(!resultZV1921.isEmpty()){
                        result.setLstSalePromotions(resultZV1921);
                    }
                }
            }

            promotionAmount = (double) Math.round(promotionAmount);
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
        result.setPromotionAmountExTax((double) Math.round(promotionAmountExTax));
        if(paymentAmount < 0) paymentAmount = 0;
        result.setPaymentAmount(paymentAmount);

        //kiểm tra xem mã giảm giá còn dùng được
        if(calculationRequest.getDiscountCode() != null && !calculationRequest.getDiscountCode().equals("")){
            OrderPromotionRequest request = new OrderPromotionRequest();
            request.setCustomerId(calculationRequest.getCustomerId());
            request.setOrderType(calculationRequest.getOrderType());
            request.setProducts(calculationRequest.getOrderRequest().getProducts());
            request.setPromotionAmount(result.getPromotionAmount());
            request.setPromotionAmountExTax(result.getPromotionAmountExTax());
            try {
                getDiscountCode(calculationRequest.getDiscountCode(),shopId, request);
            }catch (Exception ex){
                result.setResetDiscountCode(true);
            }
        }

        return result;
    }

    @Override
    public boolean checkPromotionLimit(SalePromotionDTO salePromotion, Long shopId) {
        LimitDto value = getPromotionLimit(salePromotion, shopId);

        return value.isUsed();
    }

    /*
    Lấy số xuất theo ctkm, return null = vô hạn , = 0 hết suất, > 0 giới hạn số suât
     */
    private LimitDto getPromotionLimit(SalePromotionDTO salePromotion, Long shopId) {
        if(salePromotion != null && salePromotion.getProgramId() != null && shopId != null) {
            PromotionShopMapDTO promotionShopMap = promotionClient.getPromotionShopMapV1(salePromotion.getProgramId(), shopId).getData();
            double receiving = 0;
            if(salePromotion.getTotalQty() != null) receiving = salePromotion.getTotalQty();
            else if(salePromotion.getAmount() != null && salePromotion.getAmount().getAmount() != null) receiving = salePromotion.getAmount().getAmount();

            if (promotionShopMap.getQuantityMax() == null) return new LimitDto(true, null);
            else{
                double quantityReceive = promotionShopMap.getQuantityReceived() != null ? promotionShopMap.getQuantityReceived() : 0;
                if (promotionShopMap.getQuantityMax() >= (quantityReceive + receiving))
                    return new LimitDto(true, promotionShopMap.getQuantityMax() - quantityReceive);
                else return new LimitDto(false, promotionShopMap.getQuantityMax() - quantityReceive);
            }
        }

        return new LimitDto(false, 0.0);
    }

    /*
     *ZV01 to zv06
     */
    private SalePromotionDTO getZV01ToZV06(PromotionProgramDTO program, ProductOrderDataDTO orderData, Long shopId, Long warehouseId, boolean forSaving) {

        if (program == null || orderData == null || orderData.getProducts() == null || orderData.getProducts().isEmpty())
            return null;

        List<PromotionProgramDetailDTO> details = promotionClient.findPromotionProgramDetailV1(program.getId()).getData();
        if(details.isEmpty()) return null;
        updateUomProduct(details);
        List<Long> idProductOrder = orderData.getProducts().stream().map(item -> item.getProductId()).distinct().collect(Collectors.toList());

        // key sản phẩm mua: 1 sp mua có nhiều mức, 1 mức theo 1 sản phẩm sẽ có nhiều item km
        HashMap<Long, HashMap<Integer, List<PromotionProgramDetailDTO>>> mapOrderNumber = new HashMap<>();
        int checkMulti = checkMultipleRecursive(program.getMultiple(), program.getRecursive());
        // gộp sản phẩm nếu có trùng
        for (PromotionProgramDetailDTO dto : details){
            // chỉ lấy những sản phẩm được mua có km
            if (idProductOrder.contains(dto.getProductId())) {
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
        if (mapOrderNumber.size() == 0) return null;

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
        List<Long> lstProductHasPromotion = new ArrayList<>();

        // get level number: mua 1 sản phẩm -> ctkm chỉ được chứa 1 sản phẩm mua
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
            if(productOrder == null) continue;

            int qtyRemain = productOrder.getQuantity();
            double amtRemain = productOrder.getTotalPriceNotVAT();
            if(isInclusiveTax) amtRemain = productOrder.getTotalPrice();
            // key = level, value = multiple
            HashMap<Integer, Integer> lstLv = new HashMap<>();
            for (Integer lv : lstLevel) {
                // vì km trên 1 sp nên điều kiện km như nhau
                PromotionProgramDetailDTO item = entry.getValue().get(lv).get(0);
                int multi = 0;
                // kiểm tra điều kiện mua
                if ((checkQty.contains(type) && qtyRemain >= item.getSaleQty()) ) {// Mua sản phẩm, với số lượng xác định
                    multi = 1;
                    if (checkMulti == MR_MULTIPLE || checkMulti == MR_MULTIPLE_RECURSIVE) { // nhân lên theo số bộ
                        multi = qtyRemain / item.getSaleQty();
                    }
                    qtyRemain = qtyRemain - (item.getSaleQty() * multi);

                }else if (checkAmt.contains(type) && amtRemain >= item.getSaleAmt()) {// Mua sản phẩm, với số tiền xác định cho 1 sp
                    multi = 1;
                    if (checkMulti == MR_MULTIPLE || checkMulti == MR_MULTIPLE_RECURSIVE) { // nhân lên theo số bộ
                        multi = (int) (amtRemain / item.getSaleAmt());
                    }
                    amtRemain = amtRemain - (item.getSaleAmt() * multi);
                }
                if(multi > 0){
                    if(!lstLv.containsKey(lv) || lstLv.containsKey(lv) && lstLv.get(lv) > multi){ // lấy bội số thấp nhất
                        lstLv.put(lv, multi);
                    }
                }
            }

            if (!lstLv.isEmpty()) {
                lstProductHasPromotion.add(entry.getKey());
                Map<Integer, Integer> sortedLstLv = new TreeMap<>(lstLv);
                List<Integer> levels = new ArrayList<>(sortedLstLv.keySet());
                levels.sort(Comparator.nullsFirst(Comparator.naturalOrder()));
                level = levels.get(0);

                //riêng chiết khấu % thì : các ctkm zv khác ( trừ bundle) thì ko có bội số, tối ưu
                if ( "zv01".equalsIgnoreCase(type) //Mua 1 sản phẩm, với số lượng xác định, giảm % tổng tiền
                        || "zv04".equalsIgnoreCase(type) //Mua 1 sản phẩm, với số tiền đạt mức nào đó, thì được giảm % tổng tiền
                ) {
                    PromotionProgramDetailDTO discountItem = entry.getValue().get(level).get(0);
                    if(discountItem.getDisPer() != null && discountItem.getDisPer() > 0) {
                        totalAmountSaleExTax += productOrder.getTotalPriceNotVAT();
                        totalAmountSaleInTax += productOrder.getTotalPrice();
                        double amtInTax = productOrder.getTotalPrice() * discountItem.getDisPer() / 100;
                        double amtExTax = productOrder.getTotalPriceNotVAT() * discountItem.getDisPer() / 100;
                        if(forSaving)
                            saveInfo.add(initSaleDiscount(discountItem.getProductId(), level, amtInTax, amtExTax, isInclusiveTax));

                        totalAmountInTax += amtInTax;
                        totalAmountExTax += amtExTax;
                    }
                }
                else if ( "zv02".equalsIgnoreCase(type) //Mua 1 sản phẩm, với số lượng xác định, giảm số tiền
                        || "zv05".equalsIgnoreCase(type) //Mua 1 sản phẩm, với số tiền đạt mức nào đó, thì được giảm trừ 1 số tiền
                ) {//Còn riêng chiết khấu % thì : các ctkm zv khác ( trừ bundle) thì ko có bội số, tối ưu
                    double discountInTax = 0;
                    double discountExTax = 0;
                    for (Map.Entry<Integer, Integer> entry1 : sortedLstLv.entrySet()){
                        int multi = 1;
                        if (checkMulti == MR_MULTIPLE || checkMulti == MR_MULTIPLE_RECURSIVE){ // nhân lên theo số bộ
                            multi = entry1.getValue();
                        }
                        PromotionProgramDetailDTO discountItem = entry.getValue().get(entry1.getKey()).get(0);
                        if(discountItem.getDiscAmt() != null && discountItem.getDiscAmt() > 0) {
                            double productPercent = (productOrder.getPrice() - productOrder.getPriceNotVAT() )/ productOrder.getPriceNotVAT() * 100;
                            double amtInTax = discountItem.getDiscAmt() * multi;
                            double amtExTax = (discountItem.getDiscAmt() / (( 100 + productPercent ) / 100)) * multi;
                            if(!isInclusiveTax){
                                amtExTax = discountItem.getDiscAmt() * multi;
                                amtInTax = (discountItem.getDiscAmt() * (( 100 + productPercent ) / 100)) * multi;
                            }
                            discountInTax += amtInTax;
                            discountExTax += amtExTax;
                            if(forSaving)
                                saveInfo.add(initSaleDiscount(productOrder.getProductId(), entry1.getKey(), amtInTax, amtExTax, isInclusiveTax));
                        }
                        if (checkMulti == MR_RECURSIVE || checkMulti == MR_MULTIPLE_RECURSIVE) { // có tối ưu thì tính tiếp
                        }else break; // không tính tối ưu thì dừng lại
                    }
                    if(discountInTax > 0) {
                        totalAmountSaleExTax += productOrder.getTotalPriceNotVAT();
                        totalAmountSaleInTax += productOrder.getTotalPrice();
                        totalAmountInTax += discountInTax;
                        totalAmountExTax += discountExTax;
                    }
                }
                // zv03 , zv06
                else if (program.getType() != null && (
                        "zv03".equalsIgnoreCase(program.getType().trim()) //Mua 1 sản phẩm, với số lượng xác định, tặng 1 hoặc nhiều sản phẩm nào đó
                                || "zv06".equalsIgnoreCase(program.getType().trim())) //Mua 1 sản phẩm, với số tiền đạt mức nào đó, thì được tặng 1 hoặc 1 nhóm sản phẩm nào đó
                ) { // KM san pham
                    for (Map.Entry<Integer, Integer> entry1 : sortedLstLv.entrySet()){
                        int multi = 1;
                        if (checkMulti == MR_MULTIPLE || checkMulti == MR_MULTIPLE_RECURSIVE){ // nhân lên theo số bộ
                            multi = entry1.getValue();
                        }
                        List<PromotionProgramDetailDTO> programDetails = mapOrderNumber.get(productOrder.getProductId()).get(entry1.getKey());
                        List<FreeProductDTO> lstFreeProduct = productRepository.findFreeProductDTONoOrders(shopId, warehouseId,
                                programDetails.stream().map(i -> i.getFreeProductId()).collect(Collectors.toList()));
                        initFreeProduct(lstFreeProduct, programDetails,
                                program, multi, entry1.getKey()).stream().forEachOrdered(lstProductPromotion::add);

                        if (checkMulti == MR_RECURSIVE || checkMulti == MR_MULTIPLE_RECURSIVE) { // có tối ưu thì tính tiếp
                        }else break; // không tính tối ưu thì dừng lại
                    }
                }
            }
        }

        if (totalAmountExTax > 0){
            SalePromotionDTO salePromotion = new SalePromotionDTO();
            salePromotion.setTotalAmtInTax(roundValue(totalAmountInTax));
            salePromotion.setTotalAmtExTax(roundValue(totalAmountExTax));

            SalePromotionDiscountDTO discountDTO = new SalePromotionDiscountDTO();
            discountDTO.setAmount(salePromotion.getTotalAmtInTax());

            if(!isInclusiveTax) discountDTO.setAmount(salePromotion.getTotalAmtExTax());
            if(forSaving){
                discountDTO.setMaxAmount(discountDTO.getAmount());
                discountDTO.setPercentage(roundValue(calPercent(totalAmountSaleInTax, totalAmountInTax)));
                discountDTO.setDiscountInfo(saveInfo);
            }
            salePromotion.setAmount(discountDTO);
            salePromotion.setLstProductHasPromtion(lstProductHasPromotion);
            salePromotion.setReCalculated(true);

            return salePromotion;
        }else if (!lstProductPromotion.isEmpty()){
            return initSalePromotion(lstProductPromotion, lstProductHasPromotion);
        }

        return null;
    }

    private double roundValue(Double value){
        if(value == null) return 0;
        return Math.round(value);
    }

    private HashMap<Integer, Integer> getNextLevel(List<ProductOrderDetailDataDTO> productOrders, HashMap<Long, HashMap<Integer, List<PromotionProgramDetailDTO>>> mapOrderNumber,
                    boolean isInclusiveTax, Integer level, int checkMulti, String type, HashMap<Integer, Integer> lstLv){
        List<String> checkQty = Arrays.asList("zv13", "zv14", "zv15");
        List<String> checkAmt = Arrays.asList("zv16", "zv17", "zv18");
        HashMap<Integer, Integer> newLv = new HashMap<>();
        int count = 0;

        for (ProductOrderDetailDataDTO productOrder : productOrders) {
            if(mapOrderNumber.containsKey(productOrder.getProductId())) {
                List<Integer> lstLevel = new ArrayList(mapOrderNumber.get(productOrder.getProductId()).keySet());
                lstLevel.sort(Comparator.nullsFirst(Comparator.naturalOrder()));
                int qtyRemain = productOrder.getQuantity();
                double amtRemain = productOrder.getTotalPriceNotVAT();
                if(isInclusiveTax) amtRemain = productOrder.getTotalPrice();

                for (Integer lv : lstLevel) { // số thấp nhất là mức cao nhất
                    if(level == null || level <= lv) {
                        // vì km trên bộ sp nên điều kiện km như nhau
                        PromotionProgramDetailDTO item = mapOrderNumber.get(productOrder.getProductId()).get(lv).get(0);
                        int multi = 0;
                        if(lstLv != null && lstLv.containsKey(lv)) multi = lstLv.get(lv);
                        // kiểm tra điều kiện mua
                        if ((checkQty.contains(type) && qtyRemain >= item.getSaleQty())) {// Mua sản phẩm, với số lượng xác định
                            if (multi == 0) {
                                multi = 1;
                                if (checkMulti == MR_MULTIPLE || checkMulti == MR_MULTIPLE_RECURSIVE) { // nhân lên theo số bộ
                                    multi = qtyRemain / item.getSaleQty();
                                }
                                if (newLv.isEmpty() || newLv.get(lv) > multi) { // lấy bội số thấp nhất
                                    newLv.put(lv, multi);
                                }
                                count++;
                                break;
                            }
                            qtyRemain = qtyRemain - (item.getSaleQty() * multi);

                        } else if (checkAmt.contains(type) && amtRemain >= item.getSaleAmt()) {// Mua sản phẩm, với số tiền xác định cho 1 sp
                            if (multi == 0) {
                                multi = 1;
                                if (checkMulti == MR_MULTIPLE || checkMulti == MR_MULTIPLE_RECURSIVE) { // nhân lên theo số bộ
                                    multi = (int) (amtRemain / item.getSaleAmt());
                                }
                                if (newLv.isEmpty() || newLv.get(lv) > multi) { // lấy bội số thấp nhất
                                    newLv.put(lv, multi);
                                }
                                count++;
                                break;
                            }
                            amtRemain = amtRemain - (item.getSaleAmt() * multi);
                        }else{
                            break;
                        }
                    }
                }
            }
        }

        if(count == mapOrderNumber.size() && !newLv.isEmpty()) return newLv;

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
        updateUomProduct(details);
        List<Long> idProductOrder = new ArrayList<>();
        for (ProductOrderDetailDataDTO item : orderData.getProducts()){
            if (!idProductOrder.contains(item.getProductId()))
                idProductOrder.add(item.getProductId());
        }

        //lấy sp được km và giá trị km có thuế và sau thuế
        List<FreeProductDTO> lstProductPromotion = new ArrayList<>();
        // key sản phẩm mua: 1 sp mua có nhiều mức, 1 mức theo 1 sản phẩm sẽ có nhiều item km
        HashMap<Long, HashMap<Integer, List<PromotionProgramDetailDTO>>> mapOrderNumber = new HashMap<>();
        int checkMulti = checkMultipleRecursive(program.getMultiple(), program.getRecursive());
        // gộp sản phẩm nếu có trùng
        for (PromotionProgramDetailDTO dto : details){
            // tất cả sp trong promotion phải được mua
            if (!idProductOrder.contains(dto.getProductId()))
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

        List<Integer> lstLevel = null;
        // key = level, value = multiple
        HashMap<Integer, Integer> lstLv = new HashMap<>();
        boolean isInclusiveTax = isInclusiveTax (program.getDiscountPriceType());
        List<String> checkQty = Arrays.asList("zv13", "zv14", "zv15");
        List<String> checkAmt = Arrays.asList("zv16", "zv17", "zv18");
        String type = program.getType() == null ? "" : program.getType().trim().toLowerCase();
        Integer level = null;
        int count = 0;
        Long fristProductId = null;

        // lấy mức phù hợp cho tất cả sp
        for (ProductOrderDetailDataDTO productOrder : orderData.getProducts()) {
            if(mapOrderNumber.containsKey(productOrder.getProductId())) {
                fristProductId = productOrder.getProductId();
                count += 1;
                lstLevel = new ArrayList(mapOrderNumber.get(productOrder.getProductId()).keySet());
                lstLevel.sort(Comparator.nullsFirst(Comparator.naturalOrder()));
                int qtyRemain = productOrder.getQuantity();
                double amtRemain = productOrder.getTotalPriceNotVAT();
                if(isInclusiveTax) amtRemain = productOrder.getTotalPrice();
                for (Integer lv : lstLevel) {
                    // vì km trên bộ sp nên điều kiện km như nhau
                    PromotionProgramDetailDTO item = mapOrderNumber.get(productOrder.getProductId()).get(lv).get(0);
                    // kiểm tra điều kiện mua
                    if ((checkQty.contains(type) && qtyRemain >= item.getSaleQty()) // Mua sản phẩm, với số lượng xác định
                            || (checkAmt.contains(type) && amtRemain >= item.getSaleAmt())) {// Mua sản phẩm, với số tiền xác định cho 1 sp
                        if(level == null || level < lv) level = lv;
                        count -= 1;
                        break;
                    }
                }
            }
        }

        if (count > 0 || lstLevel == null) return null; // 1 hoặc nhiều sản phẩm không thỏa điều kiện thì dừng

        //lấy các mức thỏa
        for (Integer lv : lstLevel) {
            if(level <= lv) {
                HashMap<Integer, Integer> newLv = getNextLevel(orderData.getProducts(),mapOrderNumber,isInclusiveTax, level, checkMulti, type, lstLv);
                if(newLv != null) {
                    for (Map.Entry<Integer, Integer> entry : newLv.entrySet()) lstLv.put(entry.getKey(), entry.getValue());
                }
            }
        }

        if (lstLv.isEmpty()) return null; // ko thỏa điều kiện

        double totalAmountDiscountInTax = 0;
        double totalAmountDiscountExTax = 0;
        double totalAmountOrderInTax = 0;
        Map<Integer, Integer> sortedLstLv = new TreeMap<>(lstLv);
        List<SaleDiscountSaveDTO> saveInfo = new ArrayList<>();

        //Còn riêng chiết khấu % thì : các ctkm zv khác ( trừ bundle) thì ko có bội số, tối ưu
        for (Map.Entry<Integer, Integer> entry : sortedLstLv.entrySet()){
            int multi = 1;
            if (checkMulti == MR_MULTIPLE || checkMulti == MR_MULTIPLE_RECURSIVE){ // nhân lên theo số bộ
                multi = entry.getValue();
            }
            if(("zv15".equalsIgnoreCase(type) || "zv18".equalsIgnoreCase(type)) ) { // km sp
                List<PromotionProgramDetailDTO> programDetails = mapOrderNumber.get(fristProductId).get(entry.getKey());
                List<FreeProductDTO> lstFreeProduct = productRepository.findFreeProductDTONoOrders(shopId, warehouseId,
                        programDetails.stream().map(it -> it.getFreeProductId()).filter(Objects::nonNull).collect(Collectors.toList()));
                initFreeProduct(lstFreeProduct, programDetails,
                        program, multi, entry.getKey()).stream().forEachOrdered(lstProductPromotion::add);
            }else{
                double amountOrderInTax = 0;
                double amountOrderExTax = 0;
                double discountPercent = 0;
                double amountDiscountInTax = 0;
                double amountDiscountExTax = 0;
                double allOrderInTax = 0;
                double allOrderExTax = 0;

                for (ProductOrderDetailDataDTO productOrder : orderData.getProducts()){
                    if(mapOrderNumber.containsKey(productOrder.getProductId())) {
                        allOrderInTax += productOrder.getTotalPrice();
                        allOrderExTax += productOrder.getTotalPriceNotVAT();
                        PromotionProgramDetailDTO discountItem = mapOrderNumber.get(productOrder.getProductId()).get(entry.getKey()).get(0);
                        if("zv13".equalsIgnoreCase(type) || "zv14".equalsIgnoreCase(type)){ // km %
                            amountOrderInTax += discountItem.getSaleQty() * productOrder.getPrice() * multi;
                            amountOrderExTax += discountItem.getSaleQty() * productOrder.getPriceNotVAT() * multi;
                        }else if("zv16".equalsIgnoreCase(type) || "zv17".equalsIgnoreCase(type)) { // km tiền
                            double productPercent = (productOrder.getPrice() - productOrder.getPriceNotVAT() )/ productOrder.getPriceNotVAT() * 100;
                            if(isInclusiveTax) {
                                amountOrderInTax += discountItem.getSaleAmt() * multi;
                                amountOrderExTax += discountItem.getSaleAmt() / (( 100 + productPercent ) / 100) * multi;
                            }else{
                                amountOrderInTax += discountItem.getSaleAmt() * (( 100 + productPercent ) / 100) * multi;
                                amountOrderExTax += discountItem.getSaleAmt() * multi;
                            }
                        }

                        if("zv13".equalsIgnoreCase(type) || "zv16".equalsIgnoreCase(type)) {
                            discountPercent = discountItem.getDisPer();
                        }else{
                            amountDiscountInTax = discountItem.getDiscAmt();
                            if(isInclusiveTax){
                                amountDiscountExTax = discountItem.getDiscAmt();
                            }
                        }
                    }
                }

                if(discountPercent > 0){
                    amountDiscountInTax = amountOrderInTax * (discountPercent/100);
                    amountDiscountExTax = amountOrderExTax * (discountPercent/100);
                }else{
                    if(isInclusiveTax){
                        discountPercent = calPercent(amountOrderInTax, amountDiscountInTax);
                        amountDiscountExTax = amountOrderExTax * (discountPercent/100);
                    }else{
                        discountPercent = calPercent(amountOrderExTax, amountDiscountExTax);
                        amountDiscountInTax = amountOrderInTax * (discountPercent/100);
                    }
                }
                totalAmountDiscountInTax += amountDiscountInTax;
                totalAmountDiscountExTax += amountDiscountExTax;
                totalAmountOrderInTax += amountOrderInTax;

                if(forSaving) {
                    double percentInTax = calPercent(allOrderInTax,amountDiscountInTax);
                    double percentExTax = calPercent(allOrderExTax,amountDiscountExTax);
                    allOrderInTax = 0;
                    allOrderExTax = 0;
                    int cnt = 0;
                    for (ProductOrderDetailDataDTO productOrder : orderData.getProducts()) {
                        if (mapOrderNumber.containsKey(productOrder.getProductId())) {
                            cnt += 1;
                            amountOrderInTax = Math.round(productOrder.getTotalPrice() * percentInTax/100);
                            amountOrderExTax = productOrder.getTotalPriceNotVAT() * percentExTax/100;
                            if(cnt == mapOrderNumber.size()){
                                amountOrderInTax = amountDiscountInTax - allOrderInTax;
                                amountOrderExTax = allOrderExTax - amountDiscountExTax;
                            }
                            allOrderInTax += amountOrderInTax;
                            allOrderExTax += amountOrderExTax;
                            saveInfo.add(initSaleDiscount(productOrder.getProductId(), entry.getKey(), amountDiscountInTax,
                                    amountDiscountExTax, isInclusiveTax));
                        }
                    }
                }
            }

            if (checkMulti == MR_RECURSIVE || checkMulti == MR_MULTIPLE_RECURSIVE) { // có tối ưu thì tính tiếp
            }else break; // không tính tối ưu thì dừng lại
        }

        if ("zv13".equalsIgnoreCase(type) // Mua theo Bộ sản phẩm (nghĩa là phải đầy đủ sản phẩm, bắt buộc)- với số lượng xác định, thì sẽ được giảm % tổng tiền của nhóm này
                || "zv16".equalsIgnoreCase(type) //Mua theo Bộ sản phẩm (nghĩa là phải đầy đủ sản phẩm, bắt buộc)- với số tiền xác định, thì sẽ được giảm %
                || "zv14".equalsIgnoreCase(type) //Mua theo Bộ sản phẩm (nghĩa là phải mua đầy đủ sản phẩm, bắt buộc) - với số lượng xác định, thì sẽ được giảm trừ 1 số tiền
                || "zv17".equalsIgnoreCase(type) //Mua theo Bộ sản phẩm (nghĩa là phải đầy đủ sản phẩm, bắt buộc)- với số tiền xác định, thì sẽ được trừ tiền.
        ){
            double percent = calPercent(totalAmountOrderInTax, totalAmountDiscountInTax);
            SalePromotionDiscountDTO discountDTO = new SalePromotionDiscountDTO();
            SalePromotionDTO salePromotion = new SalePromotionDTO();
            salePromotion.setTotalAmtInTax(roundValue(totalAmountDiscountInTax));
            salePromotion.setTotalAmtExTax(roundValue(totalAmountDiscountExTax));
            discountDTO.setAmount(salePromotion.getTotalAmtExTax());
            if (isInclusiveTax){ // exclusive vat
                discountDTO.setAmount(salePromotion.getTotalAmtInTax());
            }

            salePromotion.setAmount(discountDTO);
            if(forSaving) {
                discountDTO.setPercentage(percent);
                discountDTO.setMaxAmount(discountDTO.getAmount());
                discountDTO.setDiscountInfo(saveInfo);
            }
            salePromotion.setLstProductHasPromtion(new ArrayList<>(mapOrderNumber.keySet()));
            salePromotion.setReCalculated(true);
            return salePromotion;
        }
        else if ("zv15".equalsIgnoreCase(type) //Mua theo Bộ sản phẩm (nghĩa là phải mua đầy đủ sản phẩm, bắt buộc) - với số lượng xác định, thì sẽ được tặng 1 hoặc nhóm sản phẩm nào đó với số lượng xác định
                || "zv18".equalsIgnoreCase(type) //Mua theo Bộ sản phẩm (nghĩa là phải đầy đủ sản phẩm, bắt buộc)- với số tiền xác định, thì sẽ được tặng 1 hoặc nhóm sản phẩm nào đó.
        ){
            if (!lstProductPromotion.isEmpty()){
                return initSalePromotion(lstProductPromotion, new ArrayList<>(mapOrderNumber.keySet()));
            }
        }

        return null;
    }

    private SalePromotionDTO initSalePromotion(List<FreeProductDTO> lstProductPromotion, List<Long> lstProductIds){
        SalePromotionDTO salePromotion = new SalePromotionDTO();
        salePromotion.setProducts(lstProductPromotion);
        int totalDisQty = 0;
        for(FreeProductDTO item : lstProductPromotion){
            if(item.getQuantity() != null)
                totalDisQty += item.getQuantity();
        }
        salePromotion.setTotalQty(totalDisQty);
        salePromotion.setLstProductHasPromtion(lstProductIds);
        return salePromotion;
    }

    private SaleDiscountSaveDTO initSaleDiscount(Long productId, int level, double amountDiscountInTax, double amountDiscountExTax, boolean isInclusiveTax){
        SaleDiscountSaveDTO saveDTO = new SaleDiscountSaveDTO();
        saveDTO.setProductId(productId);
        saveDTO.setLevelNumber(level);
        saveDTO.setAmountExTax(roundValue(amountDiscountExTax));
        saveDTO.setAmountInTax(roundValue(amountDiscountInTax));
        saveDTO.setAmount(saveDTO.getAmountExTax());
        saveDTO.setMaxAmount(saveDTO.getAmountExTax());
        if (isInclusiveTax) {
            saveDTO.setAmount(saveDTO.getAmountInTax());
            saveDTO.setMaxAmount(saveDTO.getAmountInTax());
        }

        return saveDTO;
    }

    private boolean isInclusiveTax(Integer field){
        if (field == null || field == 1)
            return true;
        return false;
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
        updateUomProduct(details);
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
                HashMap<Integer, List<PromotionProgramDetailDTO>> orderNo = new HashMap<>();
                List<PromotionProgramDetailDTO> lst = new ArrayList<>();
                lst.add(dto);
                orderNo.put(dto.getOrderNumber(), lst);
                mapOrderNumber.put(dto.getProductId(), orderNo);
                if (idProductOrder.containsKey(dto.getProductId())){
                    lstProductHasPromotion.add(idProductOrder.get(dto.getProductId()));
                    totalOrderAmtInTax += idProductOrder.get(dto.getProductId()).getTotalPrice();
                    totalOrderAmtExtax += idProductOrder.get(dto.getProductId()).getTotalPriceNotVAT();
                    totalOrderQty += idProductOrder.get(dto.getProductId()).getQuantity();
                    if (orderProductIdDefault == null) orderProductIdDefault = dto.getProductId();
                }
            }
        }

        if ((ValidationUtils.equalDouble(totalOrderAmtInTax, UNINITIALIZED) && ValidationUtils.equalDouble(totalOrderAmtExtax, UNINITIALIZED)
        ) || ValidationUtils.equalDouble(totalOrderQty, UNINITIALIZED))
            return null;

        Integer level = null;
        List<String> checkQty = Arrays.asList("zv07", "zv08", "zv09");
        List<String> checkAmt = Arrays.asList("zv10", "zv11", "zv12");
        String type = program.getType() == null ? "" : program.getType().trim().toLowerCase();

        // get level number: so sánh trên tổng số mua -> chỉ cần lấy đại diện 1 row detail
        List<Integer> lstLevel = new ArrayList(mapOrderNumber.get(orderProductIdDefault).keySet());
        lstLevel.sort(Comparator.nullsFirst(Comparator.naturalOrder()));
        double qtyRemain = totalOrderQty;
        double amtRemain = totalOrderAmtExtax;
        if(isInclusiveTax) amtRemain = totalOrderAmtInTax;
        // key = level, value = multiple
        HashMap<Integer, Integer> lstLv = new HashMap<>();

        for (Integer lv : lstLevel){
            // vì km trên bộ sp nên điều kiện km như nhau
            PromotionProgramDetailDTO item = mapOrderNumber.get(orderProductIdDefault).get(lv).get(0);
            int multi = 0;
            // kiểm tra điều kiện mua
            if ((checkQty.contains(type) && qtyRemain >= item.getSaleQty()) ) {// Mua sản phẩm, với số lượng xác định
                multi = 1;
                if (checkMulti == MR_MULTIPLE || checkMulti == MR_MULTIPLE_RECURSIVE) { // nhân lên theo số bộ
                    multi = (int) (qtyRemain / item.getSaleQty());
                }
                qtyRemain = qtyRemain - (item.getSaleQty() * multi);

            }else if (checkAmt.contains(type) && amtRemain >= item.getSaleAmt()) {// Mua sản phẩm, với số tiền xác định cho 1 sp
                multi = 1;
                if (checkMulti == MR_MULTIPLE || checkMulti == MR_MULTIPLE_RECURSIVE) { // nhân lên theo số bộ
                    multi = (int) (amtRemain / item.getSaleAmt());
                }
                amtRemain = amtRemain - (item.getSaleAmt() * multi);
            }
            if(multi > 0){
                if(!lstLv.containsKey(lv) || lstLv.containsKey(lv) && lstLv.get(lv) > multi){ // lấy bội số thấp nhất
                    lstLv.put(lv, multi);
                }
                if(level == null) level = lv;
            }
        }

        if (lstLv.isEmpty()) return null;

        if ("zv07".equalsIgnoreCase(type) // Mua 1 nhóm sản phẩm nào đó - với số lượng xác định (tổng), thì được giảm % tổng tiền
                || "zv10".equalsIgnoreCase(type) //Mua 1 nhóm sản phẩm nào đó – với số tiền xác định (tổng), thì được giảm % tổng tiền của nhóm này
        ){
            //riêng chiết khấu % thì : các ctkm zv khác ( trừ bundle) thì ko có bội số, tối ưu
            PromotionProgramDetailDTO defaultItem = mapOrderNumber.get(orderProductIdDefault).get(level).get(0);
            if(defaultItem.getDisPer() != null && defaultItem.getDisPer() > 0) {
                double percent = defaultItem.getDisPer();

                SalePromotionDiscountDTO discountDTO = new SalePromotionDiscountDTO();
                SalePromotionDTO salePromotion = new SalePromotionDTO();
                double amtInTax = totalOrderAmtInTax * percent / 100;
                double amtExTax = totalOrderAmtExtax * percent / 100;
                salePromotion.setTotalAmtInTax(roundValue(amtInTax));
                salePromotion.setTotalAmtExTax(roundValue(amtExTax));
                discountDTO.setAmount(salePromotion.getTotalAmtExTax());
                if (isInclusiveTax) {
                    discountDTO.setAmount(salePromotion.getTotalAmtInTax());
                }
                salePromotion.setAmount(discountDTO);
                if (forSaving) {
                    discountDTO.setPercentage(percent);
                    discountDTO.setMaxAmount(discountDTO.getAmount());
                    List<SaleDiscountSaveDTO> saveInfo = initSaleDiscountSaveDTO(lstProductHasPromotion, level, percent, isInclusiveTax,
                            salePromotion.getTotalAmtInTax(), salePromotion.getTotalAmtExTax());
                    discountDTO.setDiscountInfo(saveInfo);
                }
                salePromotion.setLstProductHasPromtion(new ArrayList<>(mapOrderNumber.keySet()));
                salePromotion.setReCalculated(true);
                return salePromotion;
            }
        }
        else if ("zv08".equalsIgnoreCase(type) //Mua 1 nhóm sản phẩm nào đó – với số lượng xác định (tổng), thì được giảm trừ tiền
                || "zv11".equalsIgnoreCase(type) //Mua 1 nhóm sản phẩm nào đó – với số tiền xác định (tổng), thì được giảm trừ 1 khoản tiền
        ){// giảm giá tiền luôn gồm thuế
            PromotionProgramDetailDTO defaultItem = mapOrderNumber.get(orderProductIdDefault).get(level).get(0);
            if(defaultItem.getDiscAmt() != null && defaultItem.getDiscAmt() > 0) {
                double discountInTax = 0;
                double discountExTax = 0;
                Map<Integer, Integer> sortedLstLv = new TreeMap<>(lstLv);
                List<SaleDiscountSaveDTO> saveInfo = new ArrayList<>();

                //Còn riêng chiết khấu % thì : các ctkm zv khác ( trừ bundle) thì ko có bội số, tối ưu
                for (Map.Entry<Integer, Integer> entry : sortedLstLv.entrySet()) {
                    int multi = 1;
                    if (checkMulti == MR_MULTIPLE || checkMulti == MR_MULTIPLE_RECURSIVE) { // nhân lên theo số bộ
                        multi = entry.getValue();
                    }
                    PromotionProgramDetailDTO discountItem = mapOrderNumber.get(defaultItem.getProductId()).get(entry.getKey()).get(0);
                    double percentProduct = (totalOrderAmtInTax - totalOrderAmtExtax )/ totalOrderAmtExtax * 100;
                    double amtInTax = discountItem.getDiscAmt() * multi;
                    double amtExTax = (discountItem.getDiscAmt() / (( 100 + percentProduct ) / 100)) * multi;
                    if(!isInclusiveTax){
                        amtExTax = discountItem.getDiscAmt() * multi;
                        amtInTax = (discountItem.getDiscAmt() * (( 100 +percentProduct ) / 100)) * multi;
                    }
                    discountInTax += amtInTax;
                    discountExTax += amtExTax;
                    if (forSaving) {
                        double percent = calPercent(totalOrderAmtInTax, amtInTax);
                        List<SaleDiscountSaveDTO> infos = initSaleDiscountSaveDTO(lstProductHasPromotion, entry.getKey(), percent, isInclusiveTax,
                                amtInTax, amtExTax);
                        infos.stream().forEachOrdered(saveInfo::add);
                    }

                    if (checkMulti == MR_RECURSIVE || checkMulti == MR_MULTIPLE_RECURSIVE) { // có tối ưu thì tính tiếp
                    } else break; // không tính tối ưu thì dừng lại
                }

                SalePromotionDiscountDTO discountDTO = new SalePromotionDiscountDTO();
                SalePromotionDTO salePromotion = new SalePromotionDTO();
                salePromotion.setTotalAmtInTax(roundValue(discountInTax));
                salePromotion.setTotalAmtExTax(roundValue(discountExTax));
                discountDTO.setAmount(salePromotion.getTotalAmtInTax());
                if(!isInclusiveTax) discountDTO.setAmount(salePromotion.getTotalAmtExTax());
                if (forSaving) {
                    discountDTO.setMaxAmount(discountDTO.getAmount());
                    double percent = calPercent(totalOrderAmtInTax, discountInTax);
                    discountDTO.setPercentage(percent);
                    discountDTO.setDiscountInfo(saveInfo);
                }

                salePromotion.setAmount(discountDTO);
                salePromotion.setLstProductHasPromtion(new ArrayList<>(mapOrderNumber.keySet()));
                salePromotion.setReCalculated(true);
                return salePromotion;
            }
        }
        else if ("zv09".equalsIgnoreCase(type) //Mua 1 nhóm sản phẩm nào đó – với số lượng xác định (tổng), thì được tặng 1 hoặc 1 nhóm sản phẩm nào đó
                || "zv12".equalsIgnoreCase(type) //Mua 1 nhóm sản phẩm nào đó – với số tiền xác định (tổng), thì được tặng 1 hoặc nhóm sản phẩm nào đó
        ){
            //lấy sp km
            List<FreeProductDTO> lstProductPromotion = new ArrayList<>();
            Map<Integer, Integer> sortedLstLv = new TreeMap<>(lstLv);

            for (Map.Entry<Integer, Integer> entry : sortedLstLv.entrySet()){
                int multi = 1;
                if (checkMulti == MR_MULTIPLE || checkMulti == MR_MULTIPLE_RECURSIVE){ // nhân lên theo số bộ
                    multi = entry.getValue();
                }

                List<Long> freeProductIs = new ArrayList<>();
                for (PromotionProgramDetailDTO dto : details){
                    if(!freeProductIs.contains(dto.getFreeProductId()) && dto.getOrderNumber() == entry.getKey())
                        freeProductIs.add(dto.getFreeProductId());
                }
                List<FreeProductDTO> lstFreeProduct = productRepository.findFreeProductDTONoOrders(shopId, warehouseId,freeProductIs);
                initFreeProduct(lstFreeProduct, mapOrderNumber.get(orderProductIdDefault).get(entry.getKey()),
                        program, multi, entry.getKey()).stream().forEachOrdered(lstProductPromotion::add);

                if (checkMulti == MR_RECURSIVE || checkMulti == MR_MULTIPLE_RECURSIVE) { // có tối ưu thì tính tiếp
                }else break; // không tính tối ưu thì dừng lại
            }

            if (!lstProductPromotion.isEmpty()){
                return initSalePromotion(lstProductPromotion, new ArrayList<>(mapOrderNumber.keySet()));
            }
        }

        return null;
    }

    private List<SaleDiscountSaveDTO> initSaleDiscountSaveDTO(List<ProductOrderDetailDataDTO> products, Integer level, double percent,
                                                              boolean isInclusiveTax, double amountInTax, double amountExTax){
        if(products == null) return null;

        List<SaleDiscountSaveDTO> saveInfo = new ArrayList<>();
        int count = 0;
        double inTax = 0;
        double exTax = 0;
        for (ProductOrderDetailDataDTO product : products) {
            count++;
            SaleDiscountSaveDTO saveDTO = initSaleDiscount(product.getProductId(), level, product.getTotalPrice() * percent / 100,
                    product.getTotalPriceNotVAT() * percent / 100,
                     isInclusiveTax);
            if(count == products.size()) {
                saveDTO.setAmountInTax(roundValue(amountInTax) - inTax);
                saveDTO.setAmountExTax(roundValue(amountExTax) - exTax);
                saveDTO.setAmount(saveDTO.getAmountExTax());
                if (isInclusiveTax) {
                    saveDTO.setAmount(saveDTO.getAmountInTax());
                }
                saveDTO.setMaxAmount(saveDTO.getAmount());
            }
            inTax+=saveDTO.getAmountInTax();
            exTax+=saveDTO.getAmountExTax();
            saveInfo.add(saveDTO);
        }

        return saveInfo;
    }

    /*
    cập nhật lại số lượng nếu đơn vị uom khác nhau và set về 0 nếu sp km không còn hoạt động
     */
    private void updateUomProduct(List<PromotionProgramDetailDTO> details){
        if(details == null) return;
        List<Long> productIds = details.stream().map(item -> item.getProductId()).distinct().collect(Collectors.toList());
        details.stream().map(item -> item.getFreeProductId()).distinct().filter(Objects::nonNull).forEachOrdered(productIds::add);
        productIds.stream().distinct();
        List<Product> products = productRepository.getProducts(productIds, 1);
        List<Long> productActive = products.stream().map(item -> item.getId()).collect(Collectors.toList());
        for(PromotionProgramDetailDTO promotion : details){
            for(Product product : products){
                //set lại số lượng cần mua
                if(product.getId().equals(promotion.getProductId()) && product.getUom2() != null && promotion.getSaleUom() != null
                        && product.getUom2().trim().equalsIgnoreCase(promotion.getSaleUom().trim()) && promotion.getSaleQty() != null && product.getConvFact() != null){
                    promotion.setSaleQty(promotion.getSaleQty() * product.getConvFact());
                }
                //set lại số lượng km
                if(product.getId().equals(promotion.getFreeProductId()) && product.getUom2() != null && promotion.getFreeUom() != null
                        && product.getUom2().trim().equalsIgnoreCase(promotion.getFreeUom().trim()) && promotion.getFreeQty() != null && product.getConvFact() != null){
                    promotion.setFreeQty(promotion.getFreeQty() * product.getConvFact());
                }
            }
            //nếu sp km ko tồn tại
            if(!productActive.contains(promotion.getFreeProductId())) promotion.setFreeQty(0);
        }
    }

    /*
     *ZV19 to zv21
     */
    private SalePromotionDTO getZV19ToZV21(PromotionProgramDTO program, ProductOrderDataDTO orderData, Long shopId, Long warehouseId,
                                           double totalBeforeZV23InTax, double totalBeforeZV23ExTax, double totalZV23InTax, double totalZV23ExTax, boolean forSaving) {
        if (program == null || orderData == null || orderData.getProducts() == null || orderData.getProducts().isEmpty())
            return null;

        List<PromotionProgramDetailDTO> details = promotionClient.findPromotionProgramDetailV1(program.getId()).getData();
        if(details.isEmpty()) return null;
        updateUomProduct(details);

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

        boolean isInclusiveTax = isInclusiveTax (program.getDiscountPriceType());
        int checkMulti = checkMultipleRecursive(program.getMultiple(), program.getRecursive());
        //lấy KM theo mức
        Integer level = -1;
        String type = program.getType() == null ? "" : program.getType().trim().toLowerCase();

        // get level number: so sánh trên tổng số mua -> chỉ cần lấy đại diện 1 row detail
        List<Integer> lstLevel = new ArrayList(mapOrderNumber.keySet());
        lstLevel.sort(Comparator.nullsFirst(Comparator.naturalOrder()));
        // key = level, value = multiple
        HashMap<Integer, Integer> lstLv = new HashMap<>();
        if(orderData.getTotalPrice() == null) orderData.setTotalPrice(0.0);
        if(orderData.getTotalPriceNotVAT() == null) orderData.setTotalPriceNotVAT(0.0);
        double totalAmountInTax = orderData.getTotalPrice();
        double totalAmountExTax = orderData.getTotalPriceNotVAT();
        if (program.getAmountOrderType() != null){
            //tổng tiền đơn hàng - zv01To18 - zm (với given type = 0 hoặc 1)
            if(program.getAmountOrderType() == 1){ // trước zv23
                totalAmountInTax = totalAmountInTax - totalBeforeZV23InTax;
                totalAmountExTax = totalAmountExTax - totalBeforeZV23ExTax;
            }else if(program.getAmountOrderType() == 2){ //sau zv23
                //tổng tiền đơn hàng - zv01To18 - zm (với given type = 0 hoặc 1) - zv23
                totalAmountInTax = totalAmountInTax - totalBeforeZV23InTax - totalZV23InTax;
                totalAmountExTax = totalAmountExTax - totalBeforeZV23ExTax - totalZV23ExTax;
            }else{ //AmountOrderType = 0
                //tổng tiền đơn hàng - zv01To21 - zm (với given type = 0 hoặc 1)
                totalAmountInTax = totalAmountInTax - totalBeforeZV23InTax - totalZV23InTax;
                totalAmountExTax = totalAmountExTax - totalBeforeZV23ExTax - totalZV23ExTax;
            }
        }

        lstLevel.sort(Comparator.nullsFirst(Comparator.naturalOrder()));
        double amtRemain = totalAmountExTax;
        if(isInclusiveTax) amtRemain = totalAmountInTax;

        for (Integer lv : lstLevel){
            // vì km trên bộ sp nên điều kiện km như nhau
            PromotionProgramDetailDTO item = mapOrderNumber.get(lv).get(0);
            if(item.getSaleAmt() == null) item.setSaleAmt(0.0);
            int multi = 1;
            // kiểm tra điều kiện mua
            if (amtRemain >= item.getSaleAmt()) {// Mua sản phẩm, với số tiền xác định cho 1 sp
                if (checkMulti == MR_MULTIPLE || checkMulti == MR_MULTIPLE_RECURSIVE) { // nhân lên theo số bộ
                    multi = (int) (amtRemain / item.getSaleAmt());
                }
                amtRemain = amtRemain - (item.getSaleAmt() * multi);
                lstLv.put(lv, multi);
                if(level == -1) level = lv;
            }
        }

        if (level == -1) return null; // ko thỏa điều kiện thì dừng

        // vì km trên tổng tiền đơn hàng nên điều kiện km như nhau
        //Tính theo trị giá đơn hàng, nếu đạt tổng tiền xác định, sẽ được giảm % trên đơn hàng (ZV không bội số, tối ưu)
        if ("zv19".equalsIgnoreCase(type) ){
            PromotionProgramDetailDTO newPromo = mapOrderNumber.get(level).get(0);
            if(newPromo.getDisPer() != null && newPromo.getDisPer() > 0) {
                double percent = newPromo.getDisPer();
                SalePromotionDiscountDTO discountDTO = new SalePromotionDiscountDTO();
                SalePromotionDTO salePromotion = new SalePromotionDTO();

                double amtInTax = totalAmountInTax * percent / 100;
                double amtExTax = totalAmountExTax * percent / 100;

                salePromotion.setTotalAmtInTax(amtInTax);
                salePromotion.setTotalAmtExTax(amtExTax);
                discountDTO.setAmount(salePromotion.getTotalAmtExTax());
                if (isInclusiveTax) { // exclusive vat
                    discountDTO.setAmount(salePromotion.getTotalAmtInTax());
                }
                salePromotion.setAmount(discountDTO);
                if (forSaving) {
                    discountDTO.setPercentage(percent);
                    discountDTO.setMaxAmount(discountDTO.getAmount());
                    double pc = calPercent(orderData.getTotalPrice(), amtInTax);
                    List<SaleDiscountSaveDTO> saveInfo = initSaleDiscountSaveDTO(orderData.getProducts(), level, pc, isInclusiveTax,
                            amtInTax, amtExTax);
                    discountDTO.setDiscountInfo(saveInfo);
                }
                salePromotion.setLstProductHasPromtion(orderData.getProducts().stream().map(i -> i.getProductId()).collect(Collectors.toList()));
                salePromotion.setAffected(true);
                salePromotion.setReCalculated(true);
                return salePromotion;
            }
        }
        //Tính theo trị giá đơn hàng, nếu đạt tổng tiền xác định, sẽ được giảm trừ 1 số tiền xác định trước
        else if ("zv20".equalsIgnoreCase(type)  ){
            PromotionProgramDetailDTO newPromo = mapOrderNumber.get(level).get(0);
            if(newPromo.getDiscAmt() != null && newPromo.getDiscAmt() > 0) {
                double discountInTax = 0;
                double discountExTax = 0;
                Map<Integer, Integer> sortedLstLv = new TreeMap<>(lstLv);
                double productPercent = (totalAmountInTax - totalAmountExTax )/ totalAmountExTax * 100;
                List<SaleDiscountSaveDTO> saveInfo = new ArrayList<>();
                //Còn riêng chiết khấu % thì : các ctkm zv khác ( trừ bundle) thì ko có bội số, tối ưu
                for (Map.Entry<Integer, Integer> entry : sortedLstLv.entrySet()) {
                    int multi = 1;
                    if (checkMulti == MR_MULTIPLE || checkMulti == MR_MULTIPLE_RECURSIVE) { // nhân lên theo số bộ
                        multi = entry.getValue();
                    }
                    PromotionProgramDetailDTO discountItem = mapOrderNumber.get(entry.getKey()).get(0);
                    if(discountItem.getDiscAmt() == null) discountItem.setDiscAmt(0.0);
                    double amtInTax = discountItem.getDiscAmt() * multi;
                    double amtExTax = (discountItem.getDiscAmt() / (( 100 + productPercent ) / 100)) * multi;
                    if(!isInclusiveTax){
                        amtExTax = discountItem.getDiscAmt() * multi;
                        amtInTax = (discountItem.getDiscAmt() * (( 100 + productPercent ) / 100)) * multi;
                    }
                    discountInTax += amtInTax;
                    discountExTax += amtExTax;
                    double pc = calPercent(orderData.getTotalPrice(), amtInTax);
                    List<SaleDiscountSaveDTO> infos = initSaleDiscountSaveDTO(orderData.getProducts(), entry.getKey(), pc, isInclusiveTax,
                            amtInTax, amtExTax);
                    infos.stream().forEachOrdered(saveInfo::add);

                    if (checkMulti == MR_RECURSIVE || checkMulti == MR_MULTIPLE_RECURSIVE) { // có tối ưu thì tính tiếp
                    } else break; // không tính tối ưu thì dừng lại
                }

                SalePromotionDiscountDTO discountDTO = new SalePromotionDiscountDTO();
                SalePromotionDTO salePromotion = new SalePromotionDTO();
                salePromotion.setTotalAmtInTax(roundValue(discountInTax));
                salePromotion.setTotalAmtExTax(roundValue(discountExTax));
                discountDTO.setAmount(salePromotion.getTotalAmtInTax());
                if(!isInclusiveTax) discountDTO.setAmount(salePromotion.getTotalAmtExTax());
                if (forSaving) {
                    discountDTO.setMaxAmount(discountDTO.getAmount());
                    discountDTO.setPercentage(calPercent(totalAmountInTax, discountInTax));
                    discountDTO.setDiscountInfo(saveInfo);
                }

                salePromotion.setAmount(discountDTO);
                salePromotion.setAffected(true);
                salePromotion.setLstProductHasPromtion(orderData.getProducts().stream().map(i -> i.getProductId()).collect(Collectors.toList()));
                salePromotion.setReCalculated(true);
                return salePromotion;
            }
        }
        //Tính theo trị giá đơn hàng, nếu đạt tổng tiền xác định, sẽ được tặng 1 hoặc nhóm sản phẩm
        else if ("zv21".equalsIgnoreCase(type)){
            List<FreeProductDTO> lstProductPromotion = new ArrayList<>();
            Map<Integer, Integer> sortedLstLv = new TreeMap<>(lstLv);

            for (Map.Entry<Integer, Integer> entry : sortedLstLv.entrySet()){
                int multi = 1;
                if (checkMulti == MR_MULTIPLE || checkMulti == MR_MULTIPLE_RECURSIVE){ // nhân lên theo số bộ
                    multi = entry.getValue();
                }
                List<FreeProductDTO> lstFreeProduct = productRepository.findFreeProductDTONoOrders(shopId, warehouseId,
                        mapOrderNumber.get(entry.getKey()).stream().map(i -> i.getFreeProductId()).collect(Collectors.toList()));
                initFreeProduct(lstFreeProduct, mapOrderNumber.get(entry.getKey()),
                        program, multi, entry.getKey()).stream().forEachOrdered(lstProductPromotion::add);

                if (checkMulti == MR_RECURSIVE || checkMulti == MR_MULTIPLE_RECURSIVE) { // có tối ưu thì tính tiếp
                }else break; // không tính tối ưu thì dừng lại
            }

            if (!lstProductPromotion.isEmpty()){
                SalePromotionDTO salePromotion = initSalePromotion(lstProductPromotion, orderData.getProducts().stream().map(i -> i.getProductId()).collect(Collectors.toList()));
                salePromotion.setAffected(true);
                salePromotion.setReCalculated(true);
                return salePromotion;
            }
        }

        return null;
    }

    private List<FreeProductDTO> initFreeProduct(List<FreeProductDTO> lstFreeProduct, List<PromotionProgramDetailDTO> lstPromtionDtl,
                                                 PromotionProgramDTO program, int multi, int level){
        List<FreeProductDTO> lstProductPromotion = new ArrayList<>();
        int index = 0;
        for (FreeProductDTO freeProductDTO : lstFreeProduct) {
            double qty = 0;
            String group = "G";
            for (PromotionProgramDetailDTO item : lstPromtionDtl) {

                if(item.getFreeProductId().equals(freeProductDTO.getProductId())){
                    if("zv03".equalsIgnoreCase(program.getType().trim()) || "zv06".equalsIgnoreCase(program.getType().trim())) {
                        group = item.getProductId().toString();
                    }
                    qty = item.getFreeQty() * multi; break;
                }
            }

            //lấy số tối đa
            if (program.getRelation() != null && program.getRelation() == 0) {//all free item
                freeProductDTO.setQuantity((int) qty);
            }else{ // one free item
                if(index == 0 && qty <= freeProductDTO.getStockQuantity()) {
                    freeProductDTO.setQuantity((int) qty);
                    index += 1;
                }else freeProductDTO.setQuantity(0);
            }
            freeProductDTO.setQuantityMax((int) qty);
            freeProductDTO.setLevelNumber(level);
            if("zv03".equalsIgnoreCase(program.getType().trim()) || "zv06".equalsIgnoreCase(program.getType().trim()))
                freeProductDTO.setGroupOneFreeItem(group + level);
            else freeProductDTO.setGroupOneFreeItem("G" + level);
            lstProductPromotion.add(freeProductDTO);
        }

        return lstProductPromotion;
    }

    //Kiểm tra các chương trình hợp lệ
    private List<PromotionProgramDTO> validPromotionProgram(OrderPromotionRequest request, Long shopId, CustomerDTO customer) {
        /*
    Lấy km cho đơn hàng
    - 1. Kiểm tra các CTKM đang hoạt động //Đã lọc dk shop có dc tham gia chương trình KM hay ko promotion shop map
    - 3. Kiểm tra loại đơn hàng tham gia
    - 4. Kiểm tra thuộc tính khách hàng tham gia
     */
        MemberCardDTO memberCard = memberCardClient.getByCustomerId(customer.getId()).getData();
        List<PromotionProgramDTO> programs = promotionClient.findPromotionPrograms(shopId, Long.valueOf(request.getOrderType())
                , customer.getCustomerTypeId(), memberCard!=null?memberCard.getId():null, customer.getCloselyTypeId(), customer.getCardTypeId()).getData();
        if(programs == null || programs.isEmpty()) return null;
        List<Long> promtionIds = programs.stream().map(item -> {
            if(!item.getType().equalsIgnoreCase("ZM") && item.getPromotionDateTime() != null) return item.getId();
            return null;
        }).distinct().filter(Objects::nonNull).collect(Collectors.toList());
        List<String> programCodes = programs.stream().map(item -> {
            if(!item.getType().equalsIgnoreCase("ZM") && item.getPromotionDateTime() != null) return item.getPromotionProgramCode();
            return null;
        }).distinct().filter(Objects::nonNull).collect(Collectors.toList());

        List<PromotionProgramDTO> zvUsedInDay = new ArrayList<>();
        List<PromotionProgramDTO> zvFreeItemUsedInDay = new ArrayList<>();
        if(!promtionIds.isEmpty()) zvUsedInDay = saleOrderDiscountRepo.countDiscountUsed(shopId, customer.getId(), promtionIds);
        if(!programCodes.isEmpty()) zvFreeItemUsedInDay = saleOrderDiscountRepo.countDiscountUsedFreeItem(shopId, customer.getId(), programCodes);

        Map<Long, Integer> numberOfZVUsedInDay = zvUsedInDay.stream().collect(Collectors.toMap(PromotionProgramDTO::getPromotionGroupId, PromotionProgramDTO::getPromotionDateTime));
        Map<String, Integer> numberOfZVFreeItemUsedInDay = zvFreeItemUsedInDay.stream().collect(Collectors.toMap(PromotionProgramDTO::getPromotionProgramCode, PromotionProgramDTO::getPromotionDateTime));

        // Kiểm tra loại đơn hàng tham gia & Kiểm tra thuộc tính khách hàng tham gia
//        return programs.stream().filter(program ->  commonValidPromotionProgram(request, program, shopId, customer)).collect(Collectors.toList());
        return programs.stream().map(item -> {
            if(!item.getType().equalsIgnoreCase("ZM") && item.getPromotionDateTime() != null && (
                    (numberOfZVUsedInDay.containsKey(item.getId()) && item.getPromotionDateTime() != null && item.getPromotionDateTime() <= numberOfZVUsedInDay.get(item.getId()))
                            ||
                    (numberOfZVFreeItemUsedInDay.containsKey(item.getPromotionProgramCode()) && item.getPromotionDateTime() != null && item.getPromotionDateTime() <= numberOfZVFreeItemUsedInDay.get(item.getPromotionProgramCode()))
            )
            )
                return null;
            return item;
        }).distinct().filter(Objects::nonNull).collect(Collectors.toList());
    }

    private ProductOrderDataDTO getProductOrderData(OrderPromotionRequest request, CustomerDTO customer) {
        ProductOrderDataDTO orderDataDTO = new ProductOrderDataDTO(request.getOrderType());

        // Gộp những sp trùng
        Map<Long, ProductOrderRequest> productsMap = new HashMap<>();
        for(ProductOrderRequest product: request.getProducts()) {
            if(productsMap.containsKey(product.getProductId())){
                ProductOrderRequest pRequest = productsMap.get(product.getProductId());
                pRequest.setQuantity(pRequest.getQuantity() + product.getQuantity());
                productsMap.put(product.getProductId(), pRequest);
            }else {
                productsMap.put(product.getProductId(), product);
            }
        }

        List<ProductOrderRequest> productOrders = new ArrayList<>(productsMap.values());
        List<Price> prices = productPriceRepo.findProductPriceWithType(new ArrayList<>(productsMap.keySet()), customer.getCustomerTypeId(), DateUtils.convertToDate(LocalDateTime.now()));
        for (ProductOrderRequest product: productOrders) {
            Price price = null;
            for(Price p : prices){
                if(product.getProductId().equals(p.getProductId())){
                    price = p; break;
                }
            }
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

    /*
       Api lấy mã giảm giá
    */
    @Override
    public SalePromotionDTO getDiscountCode(String discountCode, Long shopId, OrderPromotionRequest request) {
        CustomerDTO customer = customerClient.getCustomerByIdV1(request.getCustomerId()).getData();
        if(customer == null) throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);

        //Đã lọc dk shop có dc tham gia chương trình KM hay ko promotion shop map
        PromotionProgramDiscountDTO discountDTO = promotionClient.getPromotionDiscount(discountCode, shopId).getData();

        if(discountDTO == null || !this.commonValidPromotionProgram(request, discountDTO.getProgram(), shopId, customer))
            throw new ValidateException(ResponseMessage.DISCOUNT_CODE_NOT_EXISTS);

        ProductOrderDataDTO orderData = this.getProductOrderData(request, customer);
        if (orderData == null || orderData.getProducts() == null || orderData.getProducts().isEmpty())
            return null;

        // ko chứa bất ky sp nào
        List<PromotionSaleProductDTO> saleProducts = promotionClient.findPromotionSaleProductByProgramIdV1(discountDTO.getPromotionProgramId()).getData();
        List<Long> productRequires = saleProducts.stream().map(PromotionSaleProductDTO::getProductId).collect(Collectors.toList());
        if(!saleProducts.isEmpty()) {
            boolean exits = false;
            for (ProductOrderDetailDataDTO productRequest : orderData.getProducts()) {
                if(productRequires.contains(productRequest.getProductId())) {
                    exits = true;
                    break;
                }
            }
            if (!exits) throw new ValidateException(ResponseMessage.PROMOTION_SALE_PRODUCT_REJECT);
        }

        // sai khách hàng
        if(discountDTO.getCustomerCode()!=null && !discountDTO.getCustomerCode().equals(customer.getCustomerCode()))
            throw new ValidateException(ResponseMessage.CUSTOMER_REJECT);

        double totalAmountInTax = orderData.getTotalPrice();
        if(request.getPromotionAmount() != null) totalAmountInTax -= request.getPromotionAmount();
        double totalAmountExtax = orderData.getTotalPriceNotVAT();
        if(request.getPromotionAmountExTax() != null) totalAmountExtax -= request.getPromotionAmountExTax();
        boolean isInclusiveTax = isInclusiveTax(discountDTO.getProgram().getDiscountPriceType());
        SalePromotionDTO salePromotion = calZMAmount(orderData, shopId, discountDTO, totalAmountInTax, totalAmountExtax,
                isInclusiveTax, null, false, true);

        if ( salePromotion == null){
            throw new ValidateException(ResponseMessage.MGG_SALE_AMOUNT_REJECT, discountCode);
        }
        if( salePromotion.getIsUse() == false)
            throw new ValidateException(ResponseMessage.PROMOTION_CODE_NOT_ENOUGH_VALUE, discountCode);

        return salePromotion;
    }

    /*
       Bắt điều kiện 2,3,4,5 trong doc
    */
    private boolean commonValidPromotionProgram(OrderPromotionRequest request, PromotionProgramDTO program, Long shopId, CustomerDTO customer) {
        //Kiểm tra giới hạn số lần được KM của KH chỉ các ZV ms tính
        if(!program.getType().equalsIgnoreCase("ZM") && program.getPromotionDateTime() != null) {
            Integer saleOrder = saleOrderDiscountRepo.countDiscount(shopId,  customer.getId(), program.getId(), 1);
            Integer saleOrderReturn = saleOrderDiscountRepo.countDiscount(shopId,  customer.getId(), program.getId(), 2);
            if(program.getPromotionDateTime() <= (saleOrder - saleOrderReturn)) return false;
        }

        // Kiểm tra loại đơn hàng tham gia
        if(program.getObjectType() != null && program.getObjectType() != 0) {
            Set<Long> orderTypes = promotionClient.findCusCardPromotion(program.getId(), PromotionCustObjectType.ORDER_TYPE.getValue()).getData();
            if(orderTypes == null || orderTypes.isEmpty() || !orderTypes.contains(Long.valueOf(request.getOrderType()))) return false;
        }

        // Kiểm tra thuộc tính khách hàng tham gia
        Set<Long> customerTypes = promotionClient.findCusCardPromotion(program.getId(), PromotionCustObjectType.CUSTOMER_TYPE.getValue()).getData();
        if(customerTypes != null && !customerTypes.isEmpty() && !customerTypes.contains(customer.getCustomerTypeId())) return false;

        Set<Long> memberCards = promotionClient.findCusCardPromotion(program.getId(), PromotionCustObjectType.MEMBER_CARD.getValue()).getData();
        MemberCardDTO memberCard = memberCardClient.getByCustomerId(customer.getId()).getData();
        if(memberCards != null && !memberCards.isEmpty() && !memberCards.contains(memberCard!=null?memberCard.getId():null)) return false;

        Set<Long> loyalCustomers = promotionClient.findCusCardPromotion(program.getId(), PromotionCustObjectType.LOYAL_CUSTOMER.getValue()).getData();
        if(loyalCustomers != null && !loyalCustomers.isEmpty() && !loyalCustomers.contains(customer.getCloselyTypeId())) return false;

        Set<Long> customerCardTypes = promotionClient.findCusCardPromotion(program.getId(), PromotionCustObjectType.CUSTOMER_CARD_TYPE.getValue()).getData();
        if(customerCardTypes != null && !customerCardTypes.isEmpty() && !customerCardTypes.contains(customer.getCardTypeId())) return false;

        return true;
    }

    private List<SalePromotionDTO> sortPromotions( List<SalePromotionDTO> results) {
        //isEditable = true - relation = 1
        List<SalePromotionDTO> zvFreeItems = results.stream().filter(p -> p.getProducts()!=null && !p.getProgramType().equalsIgnoreCase("ZM")).collect(Collectors.toList());
        Collections.sort(zvFreeItems,  Comparator.comparing(SalePromotionDTO::getIsEditable)
                .thenComparing(SalePromotionDTO::getPromotionType).thenComparing(SalePromotionDTO::getPromotionProgramCode));

        List<SalePromotionDTO> zvAmounts =  results.stream().filter(p -> p.getAmount()!=null && !p.getProgramType().equalsIgnoreCase("ZM")).collect(Collectors.toList());
        Collections.sort(zvAmounts, Comparator.comparing(SalePromotionDTO::getPromotionType));

        //p.getProducts() && p.getAmount() == null tặng thoải mái, p.getProducts()!=null tặng sp trong danh sách đó
        List<SalePromotionDTO> zmFreeItems = results.stream().filter(p -> p.getAmount()==null && p.getProgramType().equalsIgnoreCase("ZM")).collect(Collectors.toList());
        Collections.sort(zmFreeItems, Comparator.comparing(SalePromotionDTO::getPromotionType));

        List<SalePromotionDTO> zmAmounts =  results.stream().filter(p -> p.getAmount()!=null && p.getProgramType().equalsIgnoreCase("ZM")).collect(Collectors.toList());
        Collections.sort(zmAmounts, Comparator.comparing(SalePromotionDTO::getPromotionType));

        zvFreeItems.addAll(zvAmounts);
        zvFreeItems.addAll(zmFreeItems);
        zvFreeItems.addAll(zmAmounts);

        return zvFreeItems;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class LimitDto{
        private boolean isUsed;
        private Double limited;
    }
}