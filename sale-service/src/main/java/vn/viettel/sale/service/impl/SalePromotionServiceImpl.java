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
import vn.viettel.sale.service.enums.PriceType;
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

    private final int P_ZV01TOZV21 = 1;
    private final int P_ZM = 2;
    private final int P_ZV23 = 3;
    private final String PC_ZV = "zv";
    private final String PC_ZM = "zm";
    private final int MR_NO = 1;
    private final int MR_MULTIPLE = 2;
    private final int MR_RECURSIVE = 3;
    private final int MR_MULTIPLE_RECURSIVE = 4;

    /*
    Lấy danh sách các khuyến mãi cho 1 đơn hàng
     */
    @Override
    public List<SalePromotionDTO> getSaleItemPromotions(OrderPromotionRequest request, Long shopId) {
        List<SalePromotionDTO> results = new ArrayList<>();

        /////////////
        SalePromotionDTO itemPromotion1 = new SalePromotionDTO();
        itemPromotion1.setIsUse(true);
        itemPromotion1.setProgramType("ZV12");
        itemPromotion1.setPromotionType(0);
        itemPromotion1.setProgramId(1L);
        itemPromotion1.setPromotionProgramName("Khuyến mãi tự động 1");
        itemPromotion1.setIsEditable(false);
        itemPromotion1.setContraintType(0);

        FreeProductDTO freeProductDTO1 = new FreeProductDTO();
        freeProductDTO1.setProductId(1L);
        freeProductDTO1.setProductCode("SP0001");
        freeProductDTO1.setProductName("Sản phẩm SP0001");
        freeProductDTO1.setQuantity(1);
        freeProductDTO1.setStockQuantity(10);

        FreeProductDTO freeProductDTO2 = new FreeProductDTO();
        freeProductDTO2.setProductId(2L);
        freeProductDTO2.setProductCode("SP0002");
        freeProductDTO2.setProductName("Sản phẩm SP0002");
        freeProductDTO2.setQuantity(2);
        freeProductDTO2.setStockQuantity(1);

        List<FreeProductDTO> lstProduct1 = new ArrayList<>();
        lstProduct1.add(freeProductDTO1);
        lstProduct1.add(freeProductDTO2);

        itemPromotion1.setProducts(lstProduct1);
        results.add(itemPromotion1);

        /////////////

        SalePromotionDTO itemPromotion10 = new SalePromotionDTO();
        itemPromotion10.setIsUse(true);
        itemPromotion10.setProgramType("ZV15");
        itemPromotion10.setPromotionType(0);
        itemPromotion10.setProgramId(10L);
        itemPromotion10.setPromotionProgramName("Khuyến mãi tự động 10");
        itemPromotion10.setIsEditable(true);
        itemPromotion10.setContraintType(1);

        FreeProductDTO freeProductDTO10 = new FreeProductDTO();
        freeProductDTO10.setProductId(1L);
        freeProductDTO10.setProductCode("SP0001");
        freeProductDTO10.setProductName("Sản phẩm SP0001");
        freeProductDTO10.setQuantity(0);
        freeProductDTO10.setQuantityMax(2);
        freeProductDTO10.setStockQuantity(10);

        FreeProductDTO freeProductDTO20 = new FreeProductDTO();
        freeProductDTO20.setProductId(2L);
        freeProductDTO20.setProductCode("SP0002");
        freeProductDTO20.setProductName("Sản phẩm SP0002");
        freeProductDTO20.setQuantity(0);
        freeProductDTO10.setQuantityMax(2);
        freeProductDTO20.setStockQuantity(1);

        List<FreeProductDTO> lstProduct10 = new ArrayList<>();
        lstProduct10.add(freeProductDTO10);
        lstProduct10.add(freeProductDTO20);

        itemPromotion10.setProducts(lstProduct10);
        results.add(itemPromotion10);
        /////////

        SalePromotionDTO itemPromotion2 = new SalePromotionDTO();
        itemPromotion2.setIsUse(true);
        itemPromotion2.setProgramType("ZV15");
        itemPromotion2.setPromotionType(0);
        itemPromotion2.setProgramId(2L);
        itemPromotion2.setPromotionProgramName("Khuyến mãi tự động 2");
        itemPromotion2.setIsEditable(true);
        itemPromotion2.setContraintType(0);

        FreeProductDTO freeProductDTO3 = new FreeProductDTO();
        freeProductDTO3.setProductId(3L);
        freeProductDTO3.setProductCode("SP0003");
        freeProductDTO3.setProductName("Sản phẩm SP0003");
        freeProductDTO3.setQuantity(0);
        freeProductDTO3.setStockQuantity(2);
        freeProductDTO3.setQuantityMax(2);

        FreeProductDTO freeProductDTO4 = new FreeProductDTO();
        freeProductDTO4.setProductId(4L);
        freeProductDTO4.setProductCode("SP0004");
        freeProductDTO4.setProductName("Sản phẩm SP0004");
        freeProductDTO4.setQuantity(0);
        freeProductDTO4.setStockQuantity(4);
        freeProductDTO4.setQuantityMax(2);

        List<FreeProductDTO> lstProduct2 = new ArrayList<>();
        lstProduct2.add(freeProductDTO3);
        lstProduct2.add(freeProductDTO4);

        itemPromotion2.setProducts(lstProduct2);
        results.add(itemPromotion2);
        ///////////////////
        SalePromotionDTO itemPromotion5 = new SalePromotionDTO();
        itemPromotion5.setIsUse(true);
        itemPromotion5.setProgramType("ZV05");
        itemPromotion5.setPromotionType(0);
        itemPromotion5.setProgramId(5L);
        itemPromotion5.setPromotionProgramName("Khuyến mãi tự dộng 3");
        itemPromotion5.setIsEditable(false);
        itemPromotion5.setContraintType(0);

        SalePromotionDiscountDTO salePromotionDiscountDTO1 = new SalePromotionDiscountDTO();
        salePromotionDiscountDTO1.setAmount(20000.0);

        itemPromotion5.setAmount(salePromotionDiscountDTO1);
        results.add(itemPromotion5);

        ///////////////
        SalePromotionDTO itemPromotion50 = new SalePromotionDTO();
        itemPromotion50.setIsUse(true);
        itemPromotion50.setProgramType("ZV05");
        itemPromotion50.setPromotionType(0);
        itemPromotion50.setProgramId(50L);
        itemPromotion50.setPromotionProgramName("Khuyến mãi tự dộng 30");
        itemPromotion50.setIsEditable(false);
        itemPromotion50.setContraintType(0);

        SalePromotionDiscountDTO salePromotionDiscountDTO10 = new SalePromotionDiscountDTO();
        salePromotionDiscountDTO10.setPercentage(10.0);

        itemPromotion50.setAmount(salePromotionDiscountDTO10);
        results.add(itemPromotion50);

        /////////////////////
        SalePromotionDTO itemPromotion3 = new SalePromotionDTO();
        itemPromotion3.setIsUse(true);
        itemPromotion3.setProgramType("ZV");
        itemPromotion3.setPromotionType(1);
        itemPromotion3.setProgramId(3L);
        itemPromotion3.setPromotionProgramName("Khuyến mãi tay 1");
        itemPromotion3.setIsEditable(true);
        itemPromotion3.setContraintType(0);

        results.add(itemPromotion3);
        /////////////////////

        SalePromotionDTO itemPromotion4 = new SalePromotionDTO();
        itemPromotion4.setIsUse(true);
        itemPromotion4.setProgramType("ZV");
        itemPromotion4.setPromotionType(1);
        itemPromotion4.setProgramId(4L);
        itemPromotion4.setPromotionProgramName("Khuyến mãi tay 2");
        itemPromotion4.setIsEditable(false);
        itemPromotion4.setContraintType(0);

        FreeProductDTO freeProductDTO5 = new FreeProductDTO();
        freeProductDTO5.setProductId(3L);
        freeProductDTO5.setProductCode("SP0003");
        freeProductDTO5.setProductName("Sản phẩm SP0003");
        freeProductDTO5.setQuantity(0);
        freeProductDTO5.setStockQuantity(2);
        freeProductDTO5.setQuantityMax(2);

        FreeProductDTO freeProductDTO6 = new FreeProductDTO();
        freeProductDTO6.setProductId(4L);
        freeProductDTO6.setProductCode("SP0004");
        freeProductDTO6.setProductName("Sản phẩm SP0004");
        freeProductDTO6.setQuantity(0);
        freeProductDTO6.setStockQuantity(4);
        freeProductDTO6.setQuantityMax(2);

        List<FreeProductDTO> lstProduct3 = new ArrayList<>();
        lstProduct3.add(freeProductDTO5);
        lstProduct3.add(freeProductDTO6);

        itemPromotion4.setProducts(lstProduct3);
        results.add(itemPromotion4);
        /////////////////////

        SalePromotionDTO itemPromotion6 = new SalePromotionDTO();
        itemPromotion6.setIsUse(true);
        itemPromotion6.setProgramType("ZV");
        itemPromotion6.setPromotionType(1);
        itemPromotion6.setProgramId(6L);
        itemPromotion6.setPromotionProgramName("Khuyến mãi tay 3");
        itemPromotion6.setIsEditable(true);
        itemPromotion6.setContraintType(0);

        SalePromotionDiscountDTO salePromotionDiscountDTO2 = new SalePromotionDiscountDTO();
        salePromotionDiscountDTO2.setAmount(0.0);
        salePromotionDiscountDTO2.setMaxAmount(30000.0);

        itemPromotion6.setAmount(salePromotionDiscountDTO2);
        results.add(itemPromotion6);

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
                    this.addItemPromotion(zv01zv18, this.getAutoItemPromotionZV01ToZV21(program, orderData, shopId, warehouseId, 0,0,0,0));
                    break;
                case ZV23:
                    this.addItemPromotion(zv23, this.getItemPromotionZV23(program, orderData, shopId, warehouseId, request.getCustomerId()));
                    break;
                case ZM:
                    this.addItemPromotion(zm, this.getPromotionZM(program, orderData, shopId, warehouseId));
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
        for (PromotionProgramDTO program: programs) {
            switch (PromotionProgramType.valueOf(program.getType())) {
                case ZV19:
                case ZV20:
                case ZV21:
                    this.addItemPromotion(zv19zv21, this.getAutoItemPromotionZV01ToZV21(program, orderData, shopId, warehouseId, totalBeforeZV23InTax, totalBeforeZV23ExTax, totalZV23InTax, totalZV23ExTax));
                    break;
                default:
                    // Todo
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
                          double totalBeforeZV23InTax, double totalBeforeZV23ExTax, double totalZV23InTax, double totalZV23ExTax){
        SalePromotionDTO auto = null;
        switch (PromotionProgramType.valueOf(program.getType())) {
            case ZV01:
            case ZV02:
            case ZV03:
            case ZV04:
            case ZV05:
            case ZV06:
                auto = this.getZV01ToZV06(program, orderData, shopId, warehouseId);
                break;
            case ZV07:
            case ZV08:
            case ZV09:
            case ZV10:
            case ZV11:
            case ZV12:
                auto = this.getZV07ToZV12(program, orderData, shopId, warehouseId);
                break;
            case ZV13:
            case ZV14:
            case ZV15:
            case ZV16:
            case ZV17:
            case ZV18:
                auto = this.getZV13ToZV18(program, orderData, shopId, warehouseId);
                break;
            case ZV19:
            case ZV20:
            case ZV21:
                auto = this.getZV19ToZV21(program, orderData, shopId, warehouseId, totalBeforeZV23InTax, totalBeforeZV23ExTax, totalZV23InTax, totalZV23ExTax);
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


            //Todo tính lại isUse
            auto.setIsUse(true);
//            PromotionShopMapDTO promotionShopMap = promotionClient.getPromotionShopMapV1(program.getId(), shopId).getData();
//            if(promotionShopMap.getQuantityMax() == null) auto.setIsUse(true);
//            if(auto.getAmount() != null && promotionShopMap.getQuantityMax() != null) {
//                double quantityReceive = promotionShopMap.getQuantityReceived()!=null?promotionShopMap.getQuantityReceived():0;
//                if(promotionShopMap.getQuantityMax() >= (quantityReceive + auto.getAmount().getAmount())) auto.setIsUse(true);
//                else auto.setIsUse(false);
//            }


        }

        return auto;
    }



    private SalePromotionDTO getPromotionZM(PromotionProgramDTO program, ProductOrderDataDTO orderData, Long shopId, Long warehouseId){
        SalePromotionDTO zm = this.getItemPromotionZM(program, orderData, shopId, warehouseId);
        if(zm != null){
            zm.setPromotionType(1);
            zm.setProgramId(program.getId());
            zm.setProgramType(program.getType());
            zm.setPromotionProgramName(program.getPromotionProgramName());
            zm.setPromotionProgramCode(program.getPromotionProgramCode());
            if (program.getIsEdited() != null && program.getIsEdited() == 0)
                zm.setIsEditable(false);
            else
                zm.setIsEditable(true);
            if (program.getRelation() == null || program.getRelation() == 0)
                zm.setContraintType(0);
            else
                zm.setContraintType(program.getRelation());

            //Todo tính lại isUse
            zm.setIsUse(true);

        }

        return zm;
    }




    /*
    Lấy danh sách khuyến mãi tay ZM
     */
    private SalePromotionDTO getItemPromotionZM(PromotionProgramDTO program, ProductOrderDataDTO orderData, Long shopId, Long warehouseId){

        if (program == null || orderData == null || orderData.getProducts() == null || orderData.getProducts().isEmpty())
            return null;

        List<PromotionSaleProductDTO> details = promotionClient.findPromotionSaleProductByProgramIdV1(program.getId()).getData();
        if(details.isEmpty()) return null;

        HashMap<Long, PromotionSaleProductDTO> mapProductPro = new HashMap<>();
        double totalAmount = 0;
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
        boolean flag = false;
        // lấy tổng giá trị mua
        // Mua 1 sản phẩm/bộ sản phẩm đạt số lượng - giảm giá khách hàng, tặng hàng
        for (PromotionSaleProductDTO productPromotion : programDetails) {
            if (productPromotion.getProductId() == null) {
                if (!isInclusiveTax(program.getDiscountPriceType()) ){
                    totalAmount += orderData.getTotalPriceNotVAT();
                }else {
                    totalAmount += orderData.getTotalPrice();
                }
                if(orderData.getQuantity() >= productPromotion.getQuantity())
                    flag = true;
                break;
            }else{
                for (ProductOrderDetailDataDTO productOrder : orderData.getProducts()){
                    if (productOrder.getTotalPrice() == null) productOrder.setTotalPrice(0.0);
                    if (productOrder.getTotalPriceNotVAT() == null) productOrder.setTotalPriceNotVAT(0.0);

                    if (productPromotion.getProductId().equals(productOrder.getProductId()) && productOrder.getQuantity() >= productPromotion.getQuantity()) {
                        if (!isInclusiveTax(program.getDiscountPriceType()) ){
                            totalAmount += productOrder.getTotalPriceNotVAT();
                        }else {
                            totalAmount += productOrder.getTotalPrice();
                        }
                        flag = true;
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
            if ((discountDTO.getMinSaleAmount() != null && discountDTO.getMinSaleAmount() > totalAmount)
                    || (discountDTO.getMaxSaleAmount() != null && discountDTO.getMaxSaleAmount() < totalAmount)) {
                return null;
            }

            if (program.getGivenType() != null && program.getGivenType() == 1){// tặng sản phẩm
                // todo

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
                    salePromotion.setAmount(spDto);
                    if(isInclusiveTax(program.getDiscountPriceType())) {
                        salePromotion.setTotalAmtInTax(amount);
                        salePromotion.setTotalAmtExTax(calAmountExInTax(amount, false));
                    }
                    else {
                        salePromotion.setTotalAmtExTax(amount);
                        salePromotion.setTotalAmtInTax(calAmountExInTax(amount, true));
                    }

                    return salePromotion;
                } else if (discountDTO.getType() != null && discountDTO.getType() == 1) { // KM %
                    if (discountDTO.getDiscountPercent() == null || discountDTO.getDiscountPercent() == 0) {
                        return null;
                    }

                    SalePromotionDiscountDTO spDto = new SalePromotionDiscountDTO();
                    double amount = totalAmount * discountDTO.getDiscountPercent() / 100;
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
                    spDto.setPercentage(discountDTO.getDiscountPercent());
                    salePromotion = new SalePromotionDTO();
                    salePromotion.setAmount(spDto);
                    if(isInclusiveTax(program.getDiscountPriceType())) {
                        salePromotion.setTotalAmtInTax(amount);
                        salePromotion.setTotalAmtExTax(calAmountExInTax(amount, false));
                    }
                    else {
                        salePromotion.setTotalAmtExTax(amount);
                        salePromotion.setTotalAmtInTax(calAmountExInTax(amount, true));
                    }
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

//                PromotionShopMapDTO promotionShopMap = promotionClient.getPromotionShopMapV1(program.getId(), shopId).getData();
//                if (promotionShopMap.getQuantityMax() == null) salePromotion.setIsUse(true);
//                if (salePromotion.getAmount() != null && promotionShopMap.getQuantityMax() != null) {
//                    double quantityReceive = promotionShopMap.getQuantityReceived() != null ? promotionShopMap.getQuantityReceived() : 0;
//                    if (promotionShopMap.getQuantityMax() >= (quantityReceive + salePromotion.getAmount().getAmount()))
//                        salePromotion.setIsUse(true);
//                    else salePromotion.setIsUse(false);
//                }

                return salePromotion;
            }
        }

        return null;
    }

    private Double calAmountExInTax(Double value, boolean resultInTax){
        if(value == null)
            return null;
        if(resultInTax)
            return value + (value * 10/100);
        else
            return value / (1 + 10/100);
    }

    /*
    Lấy danh sách khuyến mãi ZV23
     */
    private SalePromotionDTO getItemPromotionZV23(PromotionProgramDTO program, ProductOrderDataDTO orderData, Long shopId, Long warehouseId, Long customerId){
        if (program == null || orderData == null || orderData.getProducts() == null || orderData.getProducts().isEmpty())
            return null;
        /* lấy amount từ promotion service: lấy doanh số RPT_ZV23.TOTAL_AMOUNT của khách hàng
         Doanh số tại thời điểm mua = doanh số tổng hợp đồng bộ đầu ngày + doanh số phát sinh trong ngày */
        Date in = new Date();
        LocalDateTime now = LocalDateTime.ofInstant(in.toInstant(), ZoneId.systemDefault());
        RPT_ZV23DTO rpt_zv23DTO = promotionClient.checkZV23Require(program.getId(), customerId, now).getData();
        List<SaleOrder> customerSOList = saleOrderRepository.findAll(Specification.where(
                SaleOderSpecification.hasFromDateToDate(DateUtils.convertFromDate(now),DateUtils.convertToDate(now)))
                .and(SaleOderSpecification.hasCustomerId(customerId)));
        double totalInDay = 0F;
        for(SaleOrder customerSO:customerSOList) {
            totalInDay = totalInDay + customerSO.getTotal();
        }
        double totalNow = rpt_zv23DTO.getTotalAmount() + totalInDay;  //Doanh số tại thời điểm mua

        Double totalCusAmount = 0.0;
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
                }
            }
        }

        //nếu không quy định sản phẩm
        if (amountExTax == 0){
            if (orderData.getTotalPrice() == null) orderData.setTotalPrice(0.0);
            if (orderData.getTotalPriceNotVAT() == null) orderData.setTotalPriceNotVAT(0.0);
            amountExTax = orderData.getTotalPriceNotVAT();
            amountInTax = orderData.getTotalPrice();
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
            }
        }

        //-	Số tiền còn lại có thể hưởng CTKM ZV23 = [số tiền quy định của ZV23] – [doanh số tính tới thời điểm mua]
        double amountRemain = amountZV23 - totalCusAmount;
        double amount = amountRemain;
        // -	Nếu [số tiền còn lại có thể hưởng CTKM ZV23] ≥ [giá trị mua hàng của đơn] (lưu ý cách tính giá chiết khấu, xem mục 6):
        //	Số tiền có thể hưởng CTKM ZV23 = [giá trị mua hàng của đơn]
        //-	Nếu [số tiền còn lại có thể hưởng CTKM ZV23] < [giá trị mua hàng của đơn] (lưu ý cách tính giá chiết khấu, xem mục 6):
        //	Số tiền có thể hưởng CTKM ZV23 = [số tiền còn lại có thể hưởng CTKM ZV23]
        if (program.getDiscountPriceType() == null || program.getDiscountPriceType() == 0){ // exclusive vat
            if(amountRemain >= amountExTax)
                amount = amountExTax;
        }else { // inclusive vat
            if(amountRemain >= amountExTax)
                amount = amountInTax;
        }

        if (percent > 0 && amount > 0){
            SalePromotionDiscountDTO discountDTO = new SalePromotionDiscountDTO();
            discountDTO.setMaxAmount(amount * percent / 100);
            discountDTO.setAmount(amount * percent / 100);
            SalePromotionDTO salePromotion = new SalePromotionDTO();
            salePromotion.setAmount(discountDTO);
            if(isInclusiveTax(program.getDiscountPriceType())) {
                salePromotion.setTotalAmtInTax(amount * percent / 100);
                salePromotion.setTotalAmtExTax(calAmountExInTax(amount * percent / 100, false));
            }
            else {
                salePromotion.setTotalAmtExTax(amount * percent / 100);
                salePromotion.setTotalAmtInTax(calAmountExInTax(amount * percent / 100, true));
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

            PromotionShopMapDTO promotionShopMap = promotionClient.getPromotionShopMapV1(program.getId(), shopId).getData();
            if (promotionShopMap.getQuantityMax() == null) salePromotion.setIsUse(true);
            if (salePromotion.getAmount() != null && promotionShopMap.getQuantityMax() != null) {
                double quantityReceive = promotionShopMap.getQuantityReceived() != null ? promotionShopMap.getQuantityReceived() : 0;
                if (promotionShopMap.getQuantityMax() >= (quantityReceive + salePromotion.getAmount().getAmount()))
                    salePromotion.setIsUse(true);
                else salePromotion.setIsUse(false);
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

        if (calculationRequest.getTotalAmount() != null && calculationRequest.getTotalAmount() != 0){
            paymentAmount = calculationRequest.getTotalAmount();

            //tính tiền khuyến mãi
            if (calculationRequest.getPromotionInfo() != null && calculationRequest.getPromotionInfo().size() > 0){
                for (SalePromotionCalItemRequest item : calculationRequest.getPromotionInfo()){
                    if (item.getProgramId() != null){
                        PromotionProgramDTO programDTO = promotionClient.getByIdV1(item.getProgramId()).getData();
                        if (programDTO != null){
                            if (item.getAmount() != null){ // tất cả các km đều tính ra tiền
                                // chiết khấu tiền
                                if (item.getAmount().getAmount() != null && item.getAmount().getAmount() != null){
                                    // giảm giá đã gồm vat
                                    if (isInclusiveTax(programDTO.getDiscountPriceType())){
                                        if (programDTO.getAmountOrderType() == 0)
                                            promotionAmount += item.getAmount().getAmount();
                                    }
                                    // giảm giá chưa gồm vat
                                    else{
                                        promotionAmount += item.getAmount().getAmount() * 10/100;
                                    }
                                }
                            }
                        }
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
            if (calculationRequest.getSaleOffAmount() != null){
                if (calculationRequest.getSaleOffAmount() > paymentAmount){
                    paymentAmount = 0;
                }else{
                    paymentAmount = paymentAmount - calculationRequest.getSaleOffAmount();
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
            if (calculationRequest.getSaveAmount() != null){
                if (calculationRequest.getSaveAmount() > paymentAmount){
                    paymentAmount = 0;
                }else{
                    paymentAmount = paymentAmount - calculationRequest.getSaveAmount();
                }
            }
        }

        result.setPromotionAmount(promotionAmount);
        result.setPaymentAmount(paymentAmount);

        return result;
    }

    /*
    kiểm tra KM là ZM hay ZV01-ZV21 hay ZV23
     */
    private Integer checkPromotionType(String type){
        if (type != null && !type.trim().equals("")){
            type = type.trim().toLowerCase();
            if (type.startsWith(PC_ZM))
                return P_ZM;
            if (type.startsWith(PC_ZV)){
                String strNo = type.replaceAll("[a-zA-Z]","");
                if(!strNo.trim().equals("")){
                    Integer number = Integer.parseInt(strNo);
                    if (number != null && number > 0 && number < 22){
                        return P_ZV01TOZV21;
                    }
                    if (number != null && number == 23){
                        return P_ZV23;
                    }
                }
            }
        }

        return 0;
    }

    /*
     *ZV01 to zv16
     */
    public SalePromotionDTO getZV01ToZV06(PromotionProgramDTO program, ProductOrderDataDTO orderData, Long shopId, Long warehouseId) {

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
            // tất cả sp trong promotion phải được mua
            if (!idProductOrder.contains(dto.getProductId()))
                return null;
            // tất cả các row khuyến mãi phải có require = 1
            if (dto.getRequired() == null || dto.getRequired() != 1)
                return null;
            productId = dto.getProductId();

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

        // mua 1 sản phẩm -> ctkm chỉ được chứa 1 sản phẩm mua
        if (mapOrderNumber.size() == 0 || mapOrderNumber.size() > 1)
            return null;

        int multiple = 1; // tính bội số
        Integer level = -1;
        boolean isInclusiveTax = isInclusiveTax (program.getDiscountPriceType());
        List<String> checkQty = Arrays.asList("zv01", "zv02", "zv03");
        List<String> checkAmt = Arrays.asList("zv04", "zv05", "zv06");
        String type = program.getType() == null ? "" : program.getType().trim().toLowerCase();

        // get level number: mua 1 sản phẩm -> ctkm chỉ được chứa 1 sản phẩm mua -> lấy mức level thấp nhất
        List<Integer> lstLevel = new ArrayList(mapOrderNumber.get(productId).keySet());
        lstLevel.sort(Comparator.nullsFirst(Comparator.naturalOrder()));
        ProductOrderDetailDataDTO productOrder = null;
        for (ProductOrderDetailDataDTO productOrder1 : orderData.getProducts()) {
            if (productOrder1.getProductId().equals(mapOrderNumber.keySet().toArray()[0])){
                productOrder = productOrder1;
                break;
            }
        }

        for (Integer lv : lstLevel){
            // vì km trên 1 sp nên điều kiện km như nhau
            PromotionProgramDetailDTO item = mapOrderNumber.get(productId).get(lv).get(0);

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
                    multiple = (int)(productOrder.getTotalPrice() / item.getSaleAmt());
                } else if (item.getSaleAmt() != null && item.getSaleAmt() > 0 && (productOrder.getTotalPrice() / item.getSaleAmt()) < 2) {
                    multiple = 1;
                }
            }else{
                if (item.getSaleAmt() > 0 && (productOrder.getTotalPriceNotVAT() / item.getSaleAmt()) >= multiple) {
                    multiple = (int)(productOrder.getTotalPriceNotVAT() / item.getSaleAmt());
                } else if (item.getSaleAmt() != null && item.getSaleAmt() > 0 && (productOrder.getTotalPriceNotVAT() / item.getSaleAmt()) < 2) {
                    multiple = 1;
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
            PromotionProgramDetailDTO dto = mapOrderNumber.get(productId).get(level).get(0);
            double amount = dto.getDiscAmt() == null ? 0 : dto.getDiscAmt();
            double percent = dto.getDisPer() == null ? 0 : dto.getDisPer();
            if (checkMulti == MR_MULTIPLE || checkMulti == MR_MULTIPLE_RECURSIVE){ // nhân lên theo số bộ
                percent = percent * multiple;
                amount = amount * multiple;
            }
            if (checkMulti == MR_RECURSIVE || checkMulti == MR_MULTIPLE_RECURSIVE){ // tối ưu
                for (Map.Entry<Integer, List<PromotionProgramDetailDTO>> entry : mapOrderNumber.get(productId).entrySet()){
                    if (entry.getKey() != null && entry.getKey() < level){
                        PromotionProgramDetailDTO dto1 = entry.getValue().get(0);
                        if (dto1.getDisPer() != null)  percent = percent + dto1.getDisPer();
                        if (dto1.getDiscAmt() != null)  amount = amount + dto1.getDiscAmt();
                    }
                }
            }

            if (amount > 0 && ("zv02".equalsIgnoreCase(type) || "zv05".equalsIgnoreCase(type))){
                SalePromotionDiscountDTO discountDTO = new SalePromotionDiscountDTO();
                SalePromotionDTO salePromotion = new SalePromotionDTO();
                discountDTO.setMaxAmount(amount);
                discountDTO.setAmount(amount);
                salePromotion.setAmount(discountDTO);
                if (isInclusiveTax) {
                    salePromotion.setTotalAmtInTax(amount);
                    salePromotion.setTotalAmtExTax(calAmountExInTax(amount, false));
                }
                else {
                    salePromotion.setTotalAmtExTax(amount);
                    salePromotion.setTotalAmtInTax(calAmountExInTax(amount, true));
                }
                return salePromotion;
            }else if (percent > 0){
                SalePromotionDiscountDTO discountDTO = new SalePromotionDiscountDTO();
                SalePromotionDTO salePromotion = new SalePromotionDTO();
                if(isInclusiveTax){
                    discountDTO.setMaxAmount(productOrder.getTotalPrice() * percent / 100);
                    discountDTO.setAmount(productOrder.getTotalPrice() * percent / 100);
                }else{
                    discountDTO.setMaxAmount(productOrder.getTotalPriceNotVAT() * percent / 100);
                    discountDTO.setAmount(productOrder.getTotalPriceNotVAT() * percent / 100);
                }
                salePromotion.setTotalAmtExTax(productOrder.getTotalPriceNotVAT() * percent / 100);
                salePromotion.setTotalAmtInTax(productOrder.getTotalPrice() * percent / 100);
                salePromotion.setAmount(discountDTO);
                return salePromotion;
            }
        } // end KM tien
        // zv03 , zv06
        else if (program.getType() != null && (
                //Mua 1 sản phẩm, với số lượng xác định, tặng 1 hoặc nhiều sản phẩm nào đó
                "zv03".equalsIgnoreCase(program.getType().trim()) ||
                        //Mua 1 sản phẩm, với số tiền đạt mức nào đó, thì được tặng 1 hoặc 1 nhóm sản phẩm nào đó
                        "zv06".equalsIgnoreCase(program.getType().trim()) )
        ) { // KM san pham
            List<FreeProductDTO> lstProductPromotion = new ArrayList<>();
            List<PromotionProgramDetailDTO> dtlProgram = mapOrderNumber.get(productOrder.getProductId()).get(level);
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
                            for (Map.Entry<Integer, List<PromotionProgramDetailDTO>> entry : mapOrderNumber.get(productOrder.getProductId()).entrySet()){
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

    /*
     *ZV13 to zv18
     */
    private SalePromotionDTO getZV13ToZV18(PromotionProgramDTO program, ProductOrderDataDTO orderData, Long shopId, Long warehouseId) {
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
        double amountOrder = 0; // tổng giá của bộ sản phẩm
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
            level = -1;
            if (lstLevel == null) {
                lstLevel = new ArrayList(mapOrderNumber.get(productOrder.getProductId()).keySet());
                lstLevel.sort(Comparator.nullsFirst(Comparator.naturalOrder()));
            }

            for (Integer lv : lstLevel){
                // vì km trên bộ sp nên điều kiện km như nhau
                PromotionProgramDetailDTO item = mapOrderNumber.get(productOrder.getProductId()).get(lv).get(0);

                // kiểm tra điều kiện mua
                if ((checkQty.contains(type) && productOrder.getQuantity() >= item.getSaleQty()) // Mua sản phẩm, với số lượng xác định cho 1 sp
                        || ( checkAmt.contains(type) && ((isInclusiveTax && productOrder.getTotalPrice() >= item.getSaleAmt()) ||
                        (!isInclusiveTax && productOrder.getTotalPriceNotVAT() >= item.getSaleAmt())) ) // Mua sản phẩm, với số tiền xác định cho 1 sp
                ) {
                    level = lv;
                }
            }
            if (level != -1) lstLv.add(level);
        }

        if (!lstLv.isEmpty()){
            lstLv.sort(Comparator.nullsFirst(Comparator.naturalOrder()));
            level = lstLv.get(0);
        }

        if (level == -1) return null;
        //level != -1 -> đã có ít nhất 1 sp thỏa điều kiện

        // tính bội số vì số lượng và tiền kiểm tra riêng trên từng sản phẩn
        for (ProductOrderDetailDataDTO productOrder : orderData.getProducts()) {
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
                            multiple = (int)(productOrder.getTotalPrice() / item.getSaleAmt());
                        } else if (item.getSaleAmt() != null && item.getSaleAmt() > 0 && (productOrder.getTotalPrice() / item.getSaleAmt()) < 2) {
                            multiple = 1;
                        }
                    }else{
                        if (item.getSaleAmt() > 0 && (productOrder.getTotalPriceNotVAT() / item.getSaleAmt()) >= multiple) {
                            multiple = (int)(productOrder.getTotalPriceNotVAT() / item.getSaleAmt());
                        } else if (item.getSaleAmt() != null && item.getSaleAmt() > 0 && (productOrder.getTotalPriceNotVAT() / item.getSaleAmt()) < 2) {
                            multiple = 1;
                        }
                    }
                }
            }
        }

        PromotionProgramDetailDTO newPromo = null;
        for (ProductOrderDetailDataDTO productOrder : orderData.getProducts()){
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
                    if (!isInclusiveTax){
                        amountOrder += productOrder.getPriceNotVAT() * multiple * newPromo.getSaleQty();
                    }else{
                        amountOrder += productOrder.getPrice() * multiple * newPromo.getSaleQty();
                    }
                }

                // Mua sản phẩm, với số tiền xác định
                if ( newPromo.getSaleAmt() > 0 && checkAmt.contains(type) &&
                        (!isInclusiveTax && productOrder.getTotalPriceNotVAT() >= newPromo.getSaleAmt() )
                        || (isInclusiveTax && productOrder.getTotalPrice() >= newPromo.getSaleAmt())
                ){
                    countProductAmt++;
                    if (!isInclusiveTax){
                        amountOrder += productOrder.getTotalPriceNotVAT();
                    }else{
                        amountOrder += productOrder.getTotalPrice();
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
            discountDTO.setMaxAmount(amountOrder * percent / 100);
            discountDTO.setAmount(amountOrder * percent / 100);
            SalePromotionDTO salePromotion = new SalePromotionDTO();
            if (isInclusiveTax) {
                salePromotion.setTotalAmtInTax(amountOrder * percent / 100);
                salePromotion.setTotalAmtExTax(calAmountExInTax(amountOrder * percent / 100, false));
            }
            else {
                salePromotion.setTotalAmtExTax(amountOrder * percent / 100);
                salePromotion.setTotalAmtInTax(calAmountExInTax(amountOrder * percent / 100, true));
            }
            salePromotion.setAmount(discountDTO);

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
            discountDTO.setMaxAmount(amount);
            discountDTO.setAmount(amount);
            SalePromotionDTO salePromotion = new SalePromotionDTO();
            salePromotion.setAmount(discountDTO);

            if (isInclusiveTax) {
                salePromotion.setTotalAmtInTax(amount);
                salePromotion.setTotalAmtExTax(calAmountExInTax(amount, false));
            }
            else {
                salePromotion.setTotalAmtExTax(amount);
                salePromotion.setTotalAmtInTax(calAmountExInTax(amount, true));
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
    private SalePromotionDTO getZV07ToZV12(PromotionProgramDTO program, ProductOrderDataDTO orderData, Long shopId, Long warehouseId) {
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
        double totalOrderAmt = 0;
        int multiple = 1; // tính bội số
        boolean isInclusiveTax = isInclusiveTax (program.getDiscountPriceType());
        int checkMulti = checkMultipleRecursive(program.getMultiple(), program.getRecursive());
        Long orderProductIdDefault = null;

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
                    if (isInclusiveTax)
                        totalOrderAmt += idProductOrder.get(dto.getProductId()).getTotalPrice();
                    else
                        totalOrderAmt += idProductOrder.get(dto.getProductId()).getTotalPriceNotVAT();
                    totalOrderQty += idProductOrder.get(dto.getProductId()).getQuantity();
                }
                if (orderProductIdDefault == null) orderProductIdDefault = dto.getProductId();
            }
        }

        if (totalOrderAmt == 0 || totalOrderQty == 0)
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
                    || ( checkAmt.contains(type) && totalOrderAmt >= item.getSaleAmt() ) // Mua sản phẩm, với tổng số tiền xác định
            ) {
                level = lv;
                defaultItem = item;
            }
        }

        if (level == -1) return null;

        // vì km trên tổng sp nên điều kiện km như nhau
        if (checkQty.contains(type))
            multiple = (int)totalOrderQty / defaultItem.getSaleQty();
        else
            multiple = (int)(totalOrderAmt / defaultItem.getSaleAmt());

        if (defaultItem != null && defaultItem.getDisPer() != null && defaultItem.getDisPer() > 0 &&
                // Mua 1 nhóm sản phẩm nào đó - với số lượng xác định (tổng), thì được giảm % tổng tiền
                (("zv07".equalsIgnoreCase(type) && defaultItem.getSaleQty() <= totalOrderQty) ||
                        //Mua 1 nhóm sản phẩm nào đó – với số tiền xác định (tổng), thì được giảm % tổng tiền của nhóm này
                        ("zv10".equalsIgnoreCase(type) && defaultItem.getSaleAmt() <= totalOrderAmt))
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
            discountDTO.setMaxAmount(totalOrderAmt * percent / 100);
            discountDTO.setAmount(totalOrderAmt * percent / 100);
            SalePromotionDTO salePromotion = new SalePromotionDTO();
            salePromotion.setAmount(discountDTO);
            if (isInclusiveTax) {
                salePromotion.setTotalAmtInTax(totalOrderAmt * percent / 100);
                salePromotion.setTotalAmtExTax(calAmountExInTax(totalOrderAmt * percent / 100, false));
            }
            else {
                salePromotion.setTotalAmtExTax(totalOrderAmt * percent / 100);
                salePromotion.setTotalAmtInTax(calAmountExInTax(totalOrderAmt * percent / 100, true));
            }
            return salePromotion;
        }
        else if (defaultItem != null && defaultItem.getDiscAmt() != null && defaultItem.getDiscAmt() > 0 &&
                //Mua 1 nhóm sản phẩm nào đó – với số lượng xác định (tổng), thì được giảm trừ tiền
                (("zv08".equalsIgnoreCase(type) && defaultItem.getSaleQty() <= totalOrderQty ) ||
                        //Mua 1 nhóm sản phẩm nào đó – với số tiền xác định (tổng), thì được giảm trừ 1 khoản tiền
                        ("zv11".equalsIgnoreCase(type) && defaultItem.getSaleAmt() <= totalOrderAmt))
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
            discountDTO.setMaxAmount(amount);
            discountDTO.setAmount(amount);
            SalePromotionDTO salePromotion = new SalePromotionDTO();
            salePromotion.setAmount(discountDTO);
            if (isInclusiveTax) {
                salePromotion.setTotalAmtInTax(amount);
                salePromotion.setTotalAmtExTax(calAmountExInTax(amount, false));
            }
            else {
                salePromotion.setTotalAmtExTax(amount);
                salePromotion.setTotalAmtInTax(calAmountExInTax(amount, true));
            }
            return salePromotion;
        }
        else if (defaultItem != null &&
                //Mua 1 nhóm sản phẩm nào đó – với số lượng xác định (tổng), thì được tặng 1 hoặc 1 nhóm sản phẩm nào đó
                (("zv09".equalsIgnoreCase(type) && defaultItem.getSaleQty() <= totalOrderQty)
                        //Mua 1 nhóm sản phẩm nào đó – với số tiền xác định (tổng), thì được tặng 1 hoặc nhóm sản phẩm nào đó
                        || ("zv12".equalsIgnoreCase(type) && defaultItem.getSaleAmt() <= totalOrderAmt)
                )
        ){
            List<FreeProductDTO> lstProductPromotion = new ArrayList<>();
            Integer totalQty = null;
            int totalDisQty = 0;
            for (PromotionProgramDetailDTO dto : mapOrderNumber.get(defaultItem.getProductId()).get(level)) {
                FreeProductDTO freeProductDTO = productRepository.getFreeProductDTONoOrder(shopId, warehouseId, dto.getFreeProductId());
                if (freeProductDTO != null) {
                    int qty = dto.getFreeQty();
                    if (checkMulti == MR_MULTIPLE || checkMulti == MR_MULTIPLE_RECURSIVE){ // nhân lên theo số bộ
                        qty = qty * multiple;
                    }
                    if (checkMulti == MR_RECURSIVE || checkMulti == MR_MULTIPLE_RECURSIVE){ // tối ưu
                        for (Map.Entry<Integer, List<PromotionProgramDetailDTO>> entry : mapOrderNumber.get(defaultItem.getProductId()).entrySet()){
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

        return null;
    }

    /*
     *ZV19 to zv21
     */
    public SalePromotionDTO getZV19ToZV21(PromotionProgramDTO program, ProductOrderDataDTO orderData, Long shopId, Long warehouseId,
                                          double totalBeforeZV23InTax, double totalBeforeZV23ExTax, double totalZV23InTax, double totalZV23ExTax) {
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
                if (checkMulti == MR_MULTIPLE || checkMulti == MR_MULTIPLE_RECURSIVE){ // nhân lên theo số bộ
                    percent = percent * multiple;
                }
                if (checkMulti == MR_RECURSIVE || checkMulti == MR_MULTIPLE_RECURSIVE){ // tối ưu
                    for (Map.Entry<Integer, List<PromotionProgramDetailDTO>> entry : mapOrderNumber.entrySet()){
                        if (entry.getKey() != null && entry.getKey() < level){
                            PromotionProgramDetailDTO dto = entry.getValue().get(0);
                            if (dto.getDisPer() != null)  percent = percent + dto.getDisPer();
                        }
                    }
                }

                SalePromotionDiscountDTO discountDTO = new SalePromotionDiscountDTO();
                SalePromotionDTO salePromotion = new SalePromotionDTO();
                if (!isInclusiveTax){ // exclusive vat
                    discountDTO.setMaxAmount(totalAmountExTax * percent / 100);
                    discountDTO.setAmount(totalAmountExTax * percent / 100);
                    salePromotion.setTotalAmtExTax(totalAmountExTax * percent / 100);
                    salePromotion.setTotalAmtInTax(calAmountExInTax(totalAmountExTax * percent / 100, true));
                }else{ // inclusive vat
                    discountDTO.setMaxAmount(totalAmountInTax * percent / 100);
                    discountDTO.setAmount(totalAmountInTax * percent / 100);
                    salePromotion.setTotalAmtInTax(totalAmountExTax * percent / 100);
                    salePromotion.setTotalAmtExTax(calAmountExInTax(totalAmountExTax * percent / 100, false));
                }

                discountDTO.setPercentage(newPromo.getDisPer());

                salePromotion.setAmount(discountDTO);

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
                discountDTO.setMaxAmount(amount);
                discountDTO.setAmount(amount);
                SalePromotionDTO salePromotion = new SalePromotionDTO();
                salePromotion.setAmount(discountDTO);
                if (isInclusiveTax){
                    salePromotion.setTotalAmtInTax(amount);
                    salePromotion.setTotalAmtExTax(calAmountExInTax(amount, false));
                }else{
                    salePromotion.setTotalAmtExTax(amount);
                    salePromotion.setTotalAmtInTax(calAmountExInTax(amount, true));
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








