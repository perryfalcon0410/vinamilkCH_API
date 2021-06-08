package vn.viettel.sale.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.dto.customer.MemberCardDTO;
import vn.viettel.core.dto.promotion.*;
import vn.viettel.core.enums.PromotionCustObjectType;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.service.BaseServiceImpl;
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

    private final int P_ZV01TOZV21 = 1;
    private final int P_ZM = 2;
    private final int P_ZV23 = 3;
    private final String PC_ZV = "zv";
    private final String PC_ZM = "zm";

    /*
    Lấy danh sách các khuyến mãi cho 1 đơn hàng
     */
    @Override
    public List<SalePromotionDTO> getSaleItemPromotions(OrderPromotionRequest request, Long shopId) {
        List<SalePromotionDTO> results = new ArrayList<>();

        /////////////
        SalePromotionDTO itemPromotion1 = new SalePromotionDTO();
        itemPromotion1.setIsUse(true);
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
        itemPromotion1.setIsUse(true);
        itemPromotion1.setPromotionType(0);
        itemPromotion1.setProgramId(10L);
        itemPromotion1.setPromotionProgramName("Khuyến mãi tự động 10");
        itemPromotion1.setIsEditable(true);
        itemPromotion1.setContraintType(1);

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
        itemPromotion3.setPromotionType(1);
        itemPromotion3.setProgramId(3L);
        itemPromotion3.setPromotionProgramName("Khuyến mãi tay 1");
        itemPromotion3.setIsEditable(true);
        itemPromotion3.setContraintType(0);

        results.add(itemPromotion3);
        /////////////////////

        SalePromotionDTO itemPromotion4 = new SalePromotionDTO();
        itemPromotion4.setIsUse(true);
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

        List<SalePromotionDTO> zv01zv21 = new ArrayList<>();
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
                case ZV19:
                case ZV20:
                case ZV21:
                    this.addItemPromotion(zv01zv21, this.getAutoItemPromotionZV01ToZV21(program, orderData, shopId, warehouseId));
                    break;
                case ZV23:
                    this.addItemPromotion(zv23, this.getItemPromotionZV23(program, orderData, shopId, warehouseId));
                    break;
                case ZM:
                    this.addItemPromotion(zm, this.getItemPromotionZM(program, orderData, shopId, warehouseId));
                    break;
                default:
                    // Todo
            }
        }

        if (zv01zv21 != null && zv01zv21.size() > 0){
            zv01zv21.stream().forEachOrdered(results::add);
        }

        if (zm != null && zm.size() > 0){
            results.stream().forEachOrdered(zm::add);
        }

        if (zv23 != null && zv23.size() > 0){
            results.stream()
                    .forEachOrdered(zv23::add);
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
    private SalePromotionDTO getAutoItemPromotionZV01ToZV21(PromotionProgramDTO program, ProductOrderDataDTO orderData, Long shopId, Long warehouseId){
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
                auto = this.getZV07(program, orderData);
                break;
            case ZV08:
                auto = this.getZV08(program, orderData);
                break;
            case ZV09:
                break;
            case ZV10:
                auto = this.getZV10(program, orderData);
                break;
            case ZV11:
                //Todo
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
                auto = this.getZV19ToZV21(program, orderData, shopId, warehouseId);
                break;
            default:
                // Todo
        }

        if(auto != null){
            auto.setPromotionType(0);
            auto.setProgramId(program.getId());
            auto.setPromotionProgramName(program.getPromotionProgramName());
            if (program.getIsEdited() != null && program.getIsEdited() == 0)
                auto.setIsEditable(false);
            else
                auto.setIsEditable(true);
            if (program.getRelation() == null || program.getRelation() == 0)
                auto.setContraintType(0);
            else
                auto.setContraintType(program.getRelation());

            PromotionShopMapDTO promotionShopMap = promotionClient.getPromotionShopMapV1(program.getId(), shopId).getData();
            if(promotionShopMap.getQuantityMax() == null) auto.setIsUse(true);
            if(auto.getAmount() != null && promotionShopMap.getQuantityMax() != null) {
                double quantityReceive = promotionShopMap.getQuantityReceived()!=null?promotionShopMap.getQuantityReceived():0;
                if(promotionShopMap.getQuantityMax() >= (quantityReceive + auto.getAmount().getAmount())) auto.setIsUse(true);
                else auto.setIsUse(false);
            }
        }

        return auto;
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
        int totalQty = 0;
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
            totalQty += dto.getQuantity();
        }

        List<PromotionSaleProductDTO> programDetails = new ArrayList<>(mapProductPro.values());
        boolean flag = false;
        for (ProductOrderDetailDataDTO productOrder : orderData.getProducts()){
            if (productOrder.getTotalPrice() == null) productOrder.setTotalPrice(0.0);
            if (productOrder.getTotalPriceNotVAT() == null) productOrder.setTotalPriceNotVAT(0.0);
            if (program.getDiscountPriceType() == null || program.getDiscountPriceType() == 0 ){
                totalAmount += productOrder.getTotalPriceNotVAT();
            }else {
                totalAmount += productOrder.getTotalPrice();
            }

            // Mua 1 sản phẩm/bộ sản phẩm đạt số lượng
            for (PromotionSaleProductDTO productPromotion : programDetails) {
                if ((productPromotion.getProductId() == null && totalQty >= productPromotion.getQuantity()) //không quy định
                        || (productPromotion.getProductId() != null && productOrder.getQuantity() >= productPromotion.getQuantity() && productOrder.getProductId().equals(productPromotion.getProductId()))
                ) {
                    flag = true;
                    break;
                }
            }
        }

        if (flag){
            List<PromotionProgramDiscountDTO> programDiscount = promotionClient.findPromotionDiscountByPromotion(program.getId()).getData();
            if (programDiscount == null || programDiscount.isEmpty())
                return null;

            SalePromotionDTO salePromotion = new SalePromotionDTO();
            PromotionProgramDiscountDTO discountDTO = programDiscount.get(0);

            if (discountDTO.getType() == null || program.getDiscountType() == 0){ // KM tiền
                //đạt số tiền trong khoảng quy định
                if ((discountDTO.getMinSaleAmount() != null && discountDTO.getMinSaleAmount() > totalAmount)
                        || (discountDTO.getMaxSaleAmount() != null && discountDTO.getMaxSaleAmount() < totalAmount)){
                    return null;
                }
                if (discountDTO.getDiscountAmount() == null){
                    discountDTO.setDiscountAmount(0.0);
                }
                if (discountDTO.getMaxDiscountAmount() == null){
                    discountDTO.setMaxDiscountAmount(0.0);
                }
                if (discountDTO.getDiscountAmount() == 0.0 && discountDTO.getMaxDiscountAmount() == 0.0){
                    return null;
                }

                SalePromotionDiscountDTO spDto = new SalePromotionDiscountDTO();

                if (discountDTO.getDiscountAmount() > discountDTO.getMaxDiscountAmount() ){
                    spDto.setMaxAmount(discountDTO.getDiscountAmount());
                    spDto.setAmount(discountDTO.getMaxDiscountAmount());
                }else{
                    spDto.setMaxAmount(discountDTO.getMaxDiscountAmount());
                    spDto.setAmount(discountDTO.getDiscountAmount());
                }
                salePromotion.setAmount(spDto);
            } else if (discountDTO.getType() == null || program.getDiscountType() == 0){ // KM %
                //đạt số tiền quy định
                if ((discountDTO.getMinSaleAmount() != null && discountDTO.getMinSaleAmount() > totalAmount)){
                    return null;
                }
                if (discountDTO.getDiscountPercent() == null || discountDTO.getDiscountPercent() == 0 ){
                    return null;
                }

                SalePromotionDiscountDTO spDto = new SalePromotionDiscountDTO();
                spDto.setPercentage(discountDTO.getDiscountPercent());
                salePromotion.setAmount(spDto);
            }

            salePromotion.setPromotionType(1);
            salePromotion.setProgramId(program.getId());
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
            if(promotionShopMap.getQuantityMax() == null) salePromotion.setIsUse(true);
            if(salePromotion.getAmount() != null && promotionShopMap.getQuantityMax() != null) {
                double quantityReceive = promotionShopMap.getQuantityReceived()!=null?promotionShopMap.getQuantityReceived():0;
                if(promotionShopMap.getQuantityMax() >= (quantityReceive + salePromotion.getAmount().getAmount())) salePromotion.setIsUse(true);
                else salePromotion.setIsUse(false);
            }

            return salePromotion;
        }

        return null;
    }

    /*
    Lấy danh sách khuyến mãi ZV23
     */
    private SalePromotionDTO getItemPromotionZV23(PromotionProgramDTO program, ProductOrderDataDTO orderData, Long shopId, Long warehouseId){
        if (program == null || orderData == null || orderData.getProducts() == null || orderData.getProducts().isEmpty())
            return null;

        /* lấy amount từ promotion service: lấy doanh số RPT_ZV23.TOTAL_AMOUNT của khách hàng
         Doanh số tại thời điểm mua = doanh số tổng hợp đồng bộ đầu ngày + doanh số phát sinh trong ngày */
        Double totalCusAmount = 0.0;
        // danh sách sản phẩm loại trừ theo id ctkm
        List<PromotionProgramProductDTO> programProduct = new ArrayList<>();
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
            discountDTO.setPercentage(percent);
            SalePromotionDTO salePromotion = new SalePromotionDTO();
            salePromotion.setAmount(discountDTO);

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
            //o	TT tính KM = Tổng tiền đơn hàng – Tổng tiền chiết khấu từ các CTKM (ZV01 – ZV21 – ZM) – Tổng tiền chiết khấu từ CTKM ZV23
            if (calculationRequest.getPromotionInfo() != null && calculationRequest.getPromotionInfo().size() > 0){
                double amount1 = 0;
                double amount2 = 0;
                double amount3 = 0;
                double percent1 = 0;
                double percent2 = 0;
                double percent3 = 0;
                for (SalePromotionCalItemRequest item : calculationRequest.getPromotionInfo()){
                    if (item.getProgramId() != null){
                        PromotionProgramDTO programDTO = promotionClient.getByIdV1(item.getProgramId()).getData();
                        if (programDTO != null){
                            if (item.getAmount() != null){
                                if (programDTO.getAmountOrderType() != null && programDTO.getDiscountType() != null && programDTO.getDiscountPriceType() != null){
                                    // chiết khấu tiền
                                    if (programDTO.getDiscountType() == 0 && item.getAmount().getAmount() != null && item.getAmount().getAmount() > 0){
                                        // giảm giá đã gồm vat
                                        if (programDTO.getDiscountPriceType() == 1){
                                            // giảm giá trên tổng tiền
                                            if (programDTO.getAmountOrderType() == 0)
                                                amount1 += item.getAmount().getAmount();

                                                // giảm giá sau khi chiết khấu đợt 1
                                            else if (programDTO.getAmountOrderType() == 1)
                                                amount2 += item.getAmount().getAmount();

                                                // giảm giá sau khi chiết khấu đợt 2
                                            else if (programDTO.getAmountOrderType() == 2)
                                                amount3 += item.getAmount().getAmount();
                                        }
                                        // giảm giá chưa gồm vat
                                        else{
                                            // giảm giá trên tổng tiền
                                            if (programDTO.getAmountOrderType() == 0)
                                                amount1 += item.getAmount().getAmount() * 10/100;

                                                // giảm giá sau khi chiết khấu đợt 1
                                            else if (programDTO.getAmountOrderType() == 1)
                                                amount2 += item.getAmount().getAmount() * 10/100;

                                                // giảm giá sau khi chiết khấu đợt 2
                                            else if (programDTO.getAmountOrderType() == 2)
                                                amount3 += item.getAmount().getAmount() * 10/100;
                                        }
                                    }
                                    // chiết khấu %
                                    else if (programDTO.getDiscountType() == 1 &&  item.getAmount().getPercentage() != null && item.getAmount().getPercentage() > 0){
                                        // giảm giá trên tổng tiền
                                        if (programDTO.getAmountOrderType() == 0)
                                            percent1 += item.getAmount().getPercentage();

                                            // giảm giá sau khi chiết khấu đợt 1
                                        else if (programDTO.getAmountOrderType() == 1)
                                            percent2 += item.getAmount().getPercentage();

                                            // giảm giá sau khi chiết khấu đợt 2
                                        else if (programDTO.getAmountOrderType() == 2)
                                            percent3 += item.getAmount().getPercentage();
                                    }
                                }
                            }
                        }
                    }
                }

                if (percent1 > 0){
                    amount1 += paymentAmount * percent1 / 100;
                }
                promotionAmount += amount1;

                if (percent2 > 0){
                    amount2 += (paymentAmount - amount1) * percent2 / 100;
                }
                promotionAmount += amount2;

                if (percent3 > 0){
                    amount3 += (paymentAmount - (amount1 + amount2)) * percent3 / 100;
                }
                promotionAmount += amount3;
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

        HashMap<Long, PromotionProgramDetailDTO> mapProductPro = new HashMap<>();
        HashMap<Long, List<PromotionProgramDetailDTO>> mapFreeProduct = new HashMap<>();
        // gộp sản phẩm nếu có trùng
        for (PromotionProgramDetailDTO dto : details){
            // tất cả sp trong promotion phải được mua
            if (!idProductOrder.contains(dto.getProductId()))
                return null;
            if (dto.getRequired() == null || dto.getRequired() != 1)
                return null;
            if(dto.getSaleQty() == null) dto.setSaleQty(0);
            if(dto.getDisPer() == null) dto.setDisPer(0.0);
            if (mapProductPro.containsKey(dto.getProductId())){
                List<PromotionProgramDetailDTO> exited = mapFreeProduct.get(dto.getProductId());
                exited.add(dto);
                mapFreeProduct.put(dto.getProductId(), exited);
            }else{
                mapProductPro.put(dto.getProductId(), dto);
                List<PromotionProgramDetailDTO> dtl = new ArrayList<>();
                dtl.add(dto);
                mapFreeProduct.put(dto.getProductId(), dtl);
            }
        }

        List<PromotionProgramDetailDTO> programDetails = new ArrayList<>(mapProductPro.values());

        // zv01 , zv02, zv04, zv05
        if (program.getType() != null && (
                //Mua 1 sản phẩm, với số lượng xác định, giảm % tổng tiền
                "zv01".equalsIgnoreCase(program.getType().trim()) ||
                        //Mua 1 sản phẩm, với số lượng xác định, giảm số tiền
                        "zv02".equalsIgnoreCase(program.getType().trim()) ||
                        //Mua 1 sản phẩm, với số tiền đạt mức nào đó, thì được giảm % tổng tiền
                        "zv04".equalsIgnoreCase(program.getType().trim()) ||
                        //Mua 1 sản phẩm, với số tiền đạt mức nào đó, thì được giảm trừ 1 số tiền
                        "zv05".equalsIgnoreCase(program.getType().trim()) )
        ){ // KM tien
            double amountPromotion = 0;
            double percentPromotion = 0;
            for (ProductOrderDetailDataDTO productOrder : orderData.getProducts()){
                for (PromotionProgramDetailDTO productPromotion : programDetails){
                    if(productOrder.getProductId().equals(productPromotion.getProductId())){ //bắt buộc phải mua SP
                        if ((productOrder.getQuantity() >= productPromotion.getSaleQty()) // Mua 1 sản phẩm, với số lượng xác định
                                || (productOrder.getTotalPrice() >= productPromotion.getSaleAmt()) //Mua 1 sản phẩm, với số tiền đạt mức
                        ){
                            if (productPromotion.getDiscAmt() != null && productPromotion.getDiscAmt() != 0){
                                amountPromotion += productPromotion.getDiscAmt();
                            }else if (productPromotion.getDisPer() != null && productPromotion.getDisPer() != 0){
                                percentPromotion += productPromotion.getDisPer();
                            }
                        }
                    }
                }
            }

            if (amountPromotion > 0){
                SalePromotionDiscountDTO discountDTO = new SalePromotionDiscountDTO();
                SalePromotionDTO salePromotion = new SalePromotionDTO();
                discountDTO.setMaxAmount(amountPromotion);
                discountDTO.setAmount(amountPromotion);
                salePromotion.setAmount(discountDTO);
                return salePromotion;
            }else if (percentPromotion > 0){
                SalePromotionDiscountDTO discountDTO = new SalePromotionDiscountDTO();
                SalePromotionDTO salePromotion = new SalePromotionDTO();
                discountDTO.setPercentage(percentPromotion);
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
            for (ProductOrderDetailDataDTO productOrder : orderData.getProducts()){
                for (PromotionProgramDetailDTO productPromotion : programDetails){
                    if(productPromotion.getRequired()!=null && productPromotion.getRequired() == 1 && productOrder.getProductId().equals(productPromotion.getProductId())){ //bắt buộc phải mua SP
                        if ((productOrder.getQuantity() >= productPromotion.getSaleQty()) // Mua 1 sản phẩm, với số lượng xác định
                                || (productOrder.getTotalPrice() >= productPromotion.getSaleAmt()) //Mua 1 sản phẩm, với số tiền đạt mức
                        ){
                            List<PromotionProgramDetailDTO> dtl = mapFreeProduct.get(productOrder.getProductId());
                            if (dtl != null && !dtl.isEmpty()) {
                                for ( PromotionProgramDetailDTO dto : dtl){
                                    FreeProductDTO freeProductDTO = productRepository.getFreeProductDTONoOrder(shopId, warehouseId, dto.getFreeProductId());
                                    if (freeProductDTO != null) {
                                        freeProductDTO.setQuantityMax(dto.getFreeQty());
                                        if (program.getIsEdited() == null || program.getIsEdited() == 0) {
                                            if (dto.getFreeQty() > freeProductDTO.getStockQuantity()) {
                                                freeProductDTO.setQuantity(freeProductDTO.getStockQuantity());
                                            } else {
                                                freeProductDTO.setQuantity(dto.getFreeQty());
                                            }
                                        } else {
                                            freeProductDTO.setQuantity(0);
                                        }
                                        lstProductPromotion.add(freeProductDTO);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (!lstProductPromotion.isEmpty()){
                SalePromotionDTO salePromotion = new SalePromotionDTO();
                salePromotion.setProducts(lstProductPromotion);
                return salePromotion;
            }
        }

        return null;
    }

    /*
     *ZV13 to zv18
     */
    public SalePromotionDTO getZV13ToZV18(PromotionProgramDTO program, ProductOrderDataDTO orderData, Long shopId, Long warehouseId) {
        if (program == null || orderData == null || orderData.getProducts() == null || orderData.getProducts().isEmpty())
            return null;

        List<PromotionProgramDetailDTO> details = promotionClient.findPromotionProgramDetailV1(program.getId()).getData();
        if(details.isEmpty()) return null;

        List<Long> idProductOrder = new ArrayList<>();
        for (ProductOrderDetailDataDTO item : orderData.getProducts()){
            if (!idProductOrder.contains(item.getProductId()))
                idProductOrder.add(item.getProductId());
        }

        // Lấy 1 dòng duy nhất tương ứng với mỗi sản phẩm mua
        HashMap<Long, PromotionProgramDetailDTO> mapProductPro = new HashMap<>();
        // gộp những sản phẩm km
        HashMap<Long, PromotionProgramDetailDTO> mapFreeProduct = new HashMap<>();
        // 1 sp mua có nhiều mức, 1 mức theo 1 sản phẩm sẽ có nhiều item km
        HashMap<Long, HashMap<Integer, List<PromotionProgramDetailDTO>>> mapOrderNumber = new HashMap<>();
        // gộp sản phẩm nếu có trùng
        for (PromotionProgramDetailDTO dto : details){
            // tất cả sp trong promotion phải được mua
            if (!idProductOrder.contains(dto.getProductId()))
                return null;
//            if (dto.getRequired() == null || dto.getRequired() != 1)
//                return null;
            if(dto.getSaleQty() == null) dto.setSaleQty(0);
            if(dto.getDisPer() == null) dto.setDisPer(0.0);
            if (mapProductPro.containsKey(dto.getProductId())){
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
                mapProductPro.put(dto.getProductId(), dto);
                HashMap<Integer, List<PromotionProgramDetailDTO>> orderNo = new HashMap<>();
                List<PromotionProgramDetailDTO> lst = new ArrayList<>();
                lst.add(dto);
                orderNo.put(dto.getOrderNumber(), lst);
                mapOrderNumber.put(dto.getProductId(), orderNo);
            }
        }

        List<PromotionProgramDetailDTO> programDetails = new ArrayList<>(mapProductPro.values());

        double percent = 0;
        double amount = 0;
        double amountOrder = 0;
        int countProductQty = 0; // đếm item thỏa số lượng xem có đủ bộ
        int countProductAmt = 0; // đếm item thỏa tiền xem có đủ bộ
        int multiple = 1; // tính bội số
        for (ProductOrderDetailDataDTO productOrder : orderData.getProducts()){
            for (PromotionProgramDetailDTO productPromotion : programDetails){
                if(productOrder.getProductId().equals(productPromotion.getProductId())){ //bắt buộc phải mua SP
                    //lấy KM theo mức
                    PromotionProgramDetailDTO newPromo = null;
                    int no = 0;
                    for (Map.Entry<Integer, List<PromotionProgramDetailDTO>> map : mapOrderNumber.get(productOrder.getProductId()).entrySet()){
                        if ((map.getKey() == null && no == 0) || (map.getKey() != null && map.getKey() >= no )) {
                            for (PromotionProgramDetailDTO item : map.getValue()) {
                                if ((productOrder.getQuantity() >= item.getSaleQty())) { // Mua sản phẩm, với số lượng xác định
                                    newPromo = item;
                                    if (!mapFreeProduct.containsKey(item.getFreeProductId())) {
                                        mapFreeProduct.put(item.getFreeProductId(), item);
                                    }
                                }
                            }

                            if (map.getKey() != null) no = map.getKey();
                        }
                    }

                    if (newPromo != null){
                        if (program.getDiscountPriceType() == null || program.getDiscountPriceType() == 0){
                            amountOrder = productOrder.getTotalPriceNotVAT();
                        }else{
                            amountOrder = productOrder.getTotalPrice();
                        }

                        if (newPromo.getDisPer() != null)
                            percent = newPromo.getDisPer();

                        if (newPromo.getDiscAmt() != null)
                            amount = newPromo.getDiscAmt();

                        if (productOrder.getQuantity() >= productPromotion.getSaleQty()) { // Mua sản phẩm, với số lượng xác định
                            countProductQty++;
                            if (newPromo.getSaleQty() > 0 && (productOrder.getQuantity() / newPromo.getSaleQty()) >= multiple ){
                                multiple = productOrder.getQuantity() / newPromo.getSaleQty();
                            }else if (newPromo.getSaleQty() > 0 && (productOrder.getQuantity() / newPromo.getSaleQty()) < 2 ){
                                multiple = 1;
                            }
                        }

                        if (((program.getDiscountPriceType() == null || program.getDiscountPriceType() == 0)// km trước thuế
                                && (productOrder.getTotalPriceNotVAT() >= newPromo.getSaleAmt()) )// Mua sản phẩm, với số tiền xác định
                                || (program.getDiscountPriceType() == 1 && productOrder.getTotalPrice() >= newPromo.getSaleAmt()) //km sau thuế
                        ){
                            countProductAmt++;
                        }
                    }else{
                        return  null;
                    }
                }
            }
        }

        if (program.getType() != null && percent > 0 &&
                // Mua theo Bộ sản phẩm (nghĩa là phải đầy đủ sản phẩm, bắt buộc)- với số lượng xác định, thì sẽ được giảm % tổng tiền của nhóm này
                (("zv13".equalsIgnoreCase(program.getType().trim()) && mapProductPro.size() == countProductQty)
                //Mua theo Bộ sản phẩm (nghĩa là phải đầy đủ sản phẩm, bắt buộc)- với số tiền xác định, thì sẽ được giảm %
                || ("zv16".equalsIgnoreCase(program.getType().trim()) && mapProductPro.size() == countProductAmt))
        ){
            if ("zv13".equalsIgnoreCase(program.getType().trim()) && multiple > 1){ // nhân lên theo số bộ
                percent = percent * multiple;
            }

            SalePromotionDiscountDTO discountDTO = new SalePromotionDiscountDTO();
            discountDTO.setMaxAmount(amountOrder * percent / 100);
            discountDTO.setAmount(amountOrder * percent / 100);
            discountDTO.setPercentage(percent);
            SalePromotionDTO salePromotion = new SalePromotionDTO();
            salePromotion.setAmount(discountDTO);

            return salePromotion;
        }
        else if (program.getType() != null && amount > 0 &&
                //Mua theo Bộ sản phẩm (nghĩa là phải mua đầy đủ sản phẩm, bắt buộc) - với số lượng xác định, thì sẽ được giảm trừ 1 số tiền
                (("zv14".equalsIgnoreCase(program.getType().trim()) && mapProductPro.size() == countProductQty )
                //Mua theo Bộ sản phẩm (nghĩa là phải đầy đủ sản phẩm, bắt buộc)- với số tiền xác định, thì sẽ được trừ tiền.
                || ("zv17".equalsIgnoreCase(program.getType().trim()) && mapProductPro.size() == countProductAmt))
        ){
            SalePromotionDiscountDTO discountDTO = new SalePromotionDiscountDTO();
            discountDTO.setMaxAmount(amount);
            discountDTO.setAmount(amount);
            SalePromotionDTO salePromotion = new SalePromotionDTO();
            salePromotion.setAmount(discountDTO);

            return salePromotion;
        }
        else if (program.getType() != null &&
                //Mua theo Bộ sản phẩm (nghĩa là phải mua đầy đủ sản phẩm, bắt buộc) - với số lượng xác định, thì sẽ được tặng 1 hoặc nhóm sản phẩm nào đó với số lượng xác định
                (("zv15".equalsIgnoreCase(program.getType().trim()) && mapProductPro.size() == countProductQty)
                //Mua theo Bộ sản phẩm (nghĩa là phải đầy đủ sản phẩm, bắt buộc)- với số tiền xác định, thì sẽ được tặng 1 hoặc nhóm sản phẩm nào đó.
                 || ("zv18".equalsIgnoreCase(program.getType().trim()) && mapProductPro.size() == countProductAmt)
                )
        ){
            List<FreeProductDTO> lstProductPromotion = new ArrayList<>();
            List<PromotionProgramDetailDTO> dtlProgram = new ArrayList<>(mapFreeProduct.values());
            if (!dtlProgram.isEmpty()) {
                for ( PromotionProgramDetailDTO dto : dtlProgram){
                    FreeProductDTO freeProductDTO = productRepository.getFreeProductDTONoOrder(shopId, warehouseId, dto.getFreeProductId());
                    if (freeProductDTO != null) {
                        freeProductDTO.setQuantityMax(dto.getFreeQty());
                        if (program.getIsEdited() == null || program.getIsEdited() == 0) {
                            if (dto.getFreeQty() > freeProductDTO.getStockQuantity()) {
                                freeProductDTO.setQuantity(freeProductDTO.getStockQuantity());
                            } else {
                                freeProductDTO.setQuantity(dto.getFreeQty());
                            }
                        } else {
                            freeProductDTO.setQuantity(0);
                        }
                        lstProductPromotion.add(freeProductDTO);
                    }
                }
            }

            if (!lstProductPromotion.isEmpty()){
                SalePromotionDTO salePromotion = new SalePromotionDTO();
                salePromotion.setProducts(lstProductPromotion);

                return salePromotion;
            }
        }

        return null;
    }

    /*
     *ZV19 to zv21
     */
    public SalePromotionDTO getZV19ToZV21(PromotionProgramDTO program, ProductOrderDataDTO orderData, Long shopId, Long warehouseId) {
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

        //lấy KM theo mức
        PromotionProgramDetailDTO newPromo = null;
        int no = 0;
        List<PromotionProgramDetailDTO> mapFreeProduct = new ArrayList<>();
        for (Map.Entry<Integer, List<PromotionProgramDetailDTO>> map : mapOrderNumber.entrySet()){
            PromotionProgramDetailDTO productPromotion = map.getValue().get(0);
            if (((map.getKey() == null && no == 0) || (map.getKey() != null && map.getKey() >= no )) &&
                    (productPromotion.getSaleAmt() > 0 && ((program.getDiscountPriceType() == null || program.getDiscountPriceType() == 0)
                            && orderData.getTotalPriceNotVAT() != null && orderData.getTotalPriceNotVAT() >= productPromotion.getSaleAmt())// km trước thuế
                            || (program.getDiscountPriceType() == 1 && orderData.getTotalPrice() != null && orderData.getTotalPrice() >= productPromotion.getSaleAmt())// km sau thuế
                    )
            ){
                newPromo = productPromotion;
                mapFreeProduct.clear();
                mapFreeProduct = map.getValue();

                if (map.getKey() != null) no = map.getKey();
            }
        }

        if (newPromo != null && program.getType() != null && newPromo.getSaleAmt() != null && newPromo.getSaleAmt() > 0){
            if (orderData.getTotalPrice() == null) orderData.setTotalPrice(0.0);
            if (orderData.getTotalPriceNotVAT() == null) orderData.setTotalPriceNotVAT(0.0);
            //Tính theo trị giá đơn hàng, nếu đạt tổng tiền xác định, sẽ được giảm % trên đơn hàng (ZV không bội số, tối ưu)
            if ("zv19".equalsIgnoreCase(program.getType().trim()) && newPromo.getDisPer() != null && newPromo.getDisPer() > 0){
                SalePromotionDiscountDTO discountDTO = new SalePromotionDiscountDTO();
                if (program.getDiscountPriceType() == null || program.getDiscountPriceType() == 0){ // exclusive vat
                    discountDTO.setMaxAmount(orderData.getTotalPriceNotVAT() * newPromo.getDisPer() / 100);
                    discountDTO.setAmount(orderData.getTotalPriceNotVAT() * newPromo.getDisPer() / 100);
                }else{ // inclusive vat
                    discountDTO.setMaxAmount(orderData.getTotalPrice() * newPromo.getDisPer() / 100);
                    discountDTO.setAmount(orderData.getTotalPrice() * newPromo.getDisPer() / 100);
                }

                discountDTO.setPercentage(newPromo.getDisPer());
                SalePromotionDTO salePromotion = new SalePromotionDTO();
                salePromotion.setAmount(discountDTO);

                return salePromotion;
            }
            //Tính theo trị giá đơn hàng, nếu đạt tổng tiền xác định, sẽ được giảm trừ 1 số tiền xác định trước
            else if ("zv20".equalsIgnoreCase(program.getType().trim()) && newPromo.getDiscAmt() != null && newPromo.getDiscAmt() > 0){
                SalePromotionDiscountDTO discountDTO = new SalePromotionDiscountDTO();
                discountDTO.setMaxAmount(newPromo.getDiscAmt());
                discountDTO.setAmount(newPromo.getDiscAmt());
                SalePromotionDTO salePromotion = new SalePromotionDTO();
                salePromotion.setAmount(discountDTO);

                return salePromotion;
            }
            //Tính theo trị giá đơn hàng, nếu đạt tổng tiền xác định, sẽ được tặng 1 hoặc nhóm sản phẩm
            else if ("zv21".equalsIgnoreCase(program.getType().trim())){
                List<FreeProductDTO> lstProductPromotion = new ArrayList<>();
                if (!mapFreeProduct.isEmpty()) {
                    for ( PromotionProgramDetailDTO dto : mapFreeProduct){
                        FreeProductDTO freeProductDTO = productRepository.getFreeProductDTONoOrder(shopId, warehouseId, dto.getFreeProductId());
                        if (freeProductDTO != null) {
                            freeProductDTO.setQuantityMax(dto.getFreeQty());
                            if (program.getIsEdited() == null || program.getIsEdited() == 0) {
                                if (dto.getFreeQty() > freeProductDTO.getStockQuantity()) {
                                    freeProductDTO.setQuantity(freeProductDTO.getStockQuantity());
                                } else {
                                    freeProductDTO.setQuantity(dto.getFreeQty());
                                }
                            } else {
                                freeProductDTO.setQuantity(0);
                            }
                            lstProductPromotion.add(freeProductDTO);
                        }
                    }
                }

                if (!lstProductPromotion.isEmpty()){
                    SalePromotionDTO salePromotion = new SalePromotionDTO();
                    salePromotion.setProducts(lstProductPromotion);

                    return salePromotion;
                }
            }
        }

        return null;
    }


    /*
     * ZV07 - không tính bội số, ko tính tối ưu
     * Mua 1 nhóm sản phẩm nào đó - với số lượng xác định (tổng), thì được giảm % tổng tiền.
     * Vd: Mua nhóm sản phẩm ( A , B, C) với số lượng 50 hộp, được giảm 5% tổng tiền cho nhóm sản phẩm này.
     */
    public SalePromotionDTO getZV07(PromotionProgramDTO program, ProductOrderDataDTO orderData) {
        SalePromotionDTO auto = new SalePromotionDTO();
        List<ProductOrderDetailDataDTO> productOrders = orderData.getProducts();
        List<PromotionProgramDetailDTO> programDetails = this.getPromotionProgramDetaiDTO(program);
        if(programDetails.isEmpty()) return null;

        //Promotion program theo mức - trong 1 bộ thì SaleQty đều lưu chung một mức
        Map<Integer, List<PromotionProgramDetailDTO>> mapPromotions = programDetails.stream().
                collect(Collectors.groupingBy(PromotionProgramDetailDTO::getSaleQty));
        //detail của 1 mức bất kỳ - tức các sản phẩm khuyến mãi trong 1 mức
        List<PromotionProgramDetailDTO> subDetails = mapPromotions.get(programDetails.get(0).getSaleQty());

        Map<Long, ProductOrderDetailDataDTO> mapProductOrders = productOrders.stream()
                .collect(Collectors.toMap(ProductOrderDetailDataDTO::getProductId, Function.identity()));

        int countProductQuantity = 0; //số lượng trong bộ sản phẩm
        for(PromotionProgramDetailDTO detail: subDetails) {
            //Sản phẩm bắt buộc phải mua
            if(detail.getRequired()!=null && detail.getRequired()!=0 && !mapProductOrders.containsKey(detail.getProductId()))
                return null;
            if(mapProductOrders.containsKey(detail.getProductId())) {
                ProductOrderDetailDataDTO productOrder = mapProductOrders.get(detail.getProductId());
                countProductQuantity += productOrder.getQuantity();
            }
        }
        if(countProductQuantity == 0) return null;

        // Mức được hưởng cao nhất - check đủ bộ SP
        List<Integer> quantityMap = new ArrayList<>(mapPromotions.keySet());
        int n = countProductQuantity;
        List<Integer> integers = quantityMap.stream().filter(i -> i<= n).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        if(integers.isEmpty()) return null;

        Integer promotionMax = integers.get(0);
        List<PromotionProgramDetailDTO> promotions = mapPromotions.get(promotionMax);
        PromotionProgramDetailDTO promotion = promotions.get(0);

        //Tính khuyến mãi - DiscAmt đều lưu chung một mức
        SalePromotionDiscountDTO discountDTO = new SalePromotionDiscountDTO();
        discountDTO.setPercentage(promotion.getDisPer());
        auto.setAmount(discountDTO);
        return auto;
    }


    /*
     * ZV08 - không tính bội số - ko tính tối ưu -> lấy km cao nhất
     *  Mua 1 nhóm sản phẩm nào đó – với số lượng xác định (tổng), thì được giảm trừ tiền.
     * Vd: Mua nhóm sản phẩm (A, B, C) với số lượng 50 hộp, được giảm trừ 5000 đ.
     */
    public SalePromotionDTO getZV08(PromotionProgramDTO program, ProductOrderDataDTO orderData) {
        SalePromotionDTO auto = new SalePromotionDTO();
        List<ProductOrderDetailDataDTO> productOrders = orderData.getProducts();
        List<PromotionProgramDetailDTO> programDetails = this.getPromotionProgramDetaiDTO(program);
        if(programDetails.isEmpty()) return null;

        //Promotion program theo mức - trong 1 bộ thì SaleQty đều lưu chung một mức
        Map<Integer, List<PromotionProgramDetailDTO>> mapPromotions = programDetails.stream().
                collect(Collectors.groupingBy(PromotionProgramDetailDTO::getSaleQty));
        //detail của 1 mức bất kỳ - tức các sản phẩm khuyến mãi trong 1 mức
        List<PromotionProgramDetailDTO> subDetails = mapPromotions.get(programDetails.get(0).getSaleQty());

        Map<Long, ProductOrderDetailDataDTO> mapProductOrders = productOrders.stream()
                .collect(Collectors.toMap(ProductOrderDetailDataDTO::getProductId, Function.identity()));

        int countProductQuantity = 0; //số lượng trong bộ sản phẩm
        for(PromotionProgramDetailDTO detail: subDetails) {
            //Sản phẩm bắt buộc phải mua
            if(detail.getRequired()!=null && detail.getRequired()!=0 && !mapProductOrders.containsKey(detail.getProductId()))
                return null;
            if(mapProductOrders.containsKey(detail.getProductId())) {
                ProductOrderDetailDataDTO productOrder = mapProductOrders.get(detail.getProductId());
                countProductQuantity += productOrder.getQuantity();
            }
        }
        if(countProductQuantity == 0) return null;

        // Mức được hưởng cao nhất - check đủ bộ SP
        List<Integer> quantityMap = new ArrayList<>(mapPromotions.keySet());
        int n = countProductQuantity;
        List<Integer> integers = quantityMap.stream().filter(i -> i<= n).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        if(integers.isEmpty()) return null;

        Integer promotionMax = integers.get(0);
        List<PromotionProgramDetailDTO> promotions = mapPromotions.get(promotionMax);
        PromotionProgramDetailDTO promotion = promotions.get(0);

        //Tính khuyến mãi - DiscAmt đều lưu chung một mức
        SalePromotionDiscountDTO discountDTO = new SalePromotionDiscountDTO();
        discountDTO.setAmount(promotion.getDiscAmt());
        auto.setAmount(discountDTO);

        return auto;
    }

    /*
     *ZV10 -- không tính bộ số - không tính tối ưu
     *Mua 1 nhóm sản phẩm nào đó – với số tiền xác định (tổng), thì được giảm % tổng tiền của nhóm này.
     *Vd: Mua nhóm sản phẩm (A, B, C) với tổng tiền là 500.000 đ, thì được giảm 10%.
     */
    public SalePromotionDTO getZV10(PromotionProgramDTO program, ProductOrderDataDTO orderData) {
        SalePromotionDTO auto = new SalePromotionDTO();
        List<ProductOrderDetailDataDTO> productOrders = orderData.getProducts();
        List<PromotionProgramDetailDTO> programDetails = this.getPromotionProgramDetaiDTO(program);
        if(programDetails.isEmpty()) return null;

        //Promotion program theo mức - trong 1 bộ thì SaleAmt đều lưu chung một mức
        Map<Double, List<PromotionProgramDetailDTO>> mapPromotions = programDetails.stream().
                collect(Collectors.groupingBy(PromotionProgramDetailDTO::getSaleAmt));
        //detail của 1 mức bất kỳ - tức các sản phẩm khuyến mãi trong 1 mức
        List<PromotionProgramDetailDTO> subDetails = mapPromotions.get(programDetails.get(0).getSaleAmt());

        Map<Long, ProductOrderDetailDataDTO> mapProductOrders = productOrders.stream()
                .collect(Collectors.toMap(ProductOrderDetailDataDTO::getProductId, Function.identity()));

        double totalProductVAT = 0;
        double totalProductNotVAT = 0;
        for(PromotionProgramDetailDTO detail: subDetails) {
            //Sản phẩm bắt buộc phải mua
            if(detail.getRequired()!=null && detail.getRequired()!=0 && !mapProductOrders.containsKey(detail.getProductId()))
                return null;
            if(mapProductOrders.containsKey(detail.getProductId())) {
                ProductOrderDetailDataDTO productOrder = mapProductOrders.get(detail.getProductId());
                totalProductVAT += productOrder.getTotalPrice();
                totalProductNotVAT += productOrder.getTotalPriceNotVAT();
            }
        }
        if(totalProductVAT == 0 && totalProductNotVAT ==0) return null;

        // Mức được hưởng cao nhất - check đủ bộ SP
        List<Double> quantityMap = new ArrayList<>(mapPromotions.keySet());
        double n = program.getDiscountPriceType()==0?totalProductNotVAT:totalProductVAT;
        List<Double> doubles = quantityMap.stream().filter(i -> i<= n).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        if(doubles.isEmpty()) return null;

        Double promotionMax = doubles.get(0);
        List<PromotionProgramDetailDTO> promotions = mapPromotions.get(promotionMax);
        PromotionProgramDetailDTO promotion = promotions.get(0);

        //Tính khuyến mãi - DisPer đều lưu chung một mức
        SalePromotionDiscountDTO discountDTO = new SalePromotionDiscountDTO();
        discountDTO.setPercentage(promotion.getDisPer());
        auto.setAmount(discountDTO);

        return auto;
    }

    private List<PromotionProgramDetailDTO> getPromotionProgramDetaiDTO(PromotionProgramDTO program) {
        List<PromotionProgramDetailDTO> details = promotionClient.findPromotionProgramDetailV1(program.getId()).getData();
        List<PromotionProgramDetailDTO> programDetails = details.stream().map(detail -> {
            if(detail.getSaleAmt() == null) detail.setSaleAmt(0.0);
            if(detail.getSaleQty() == null) detail.setSaleQty(0);
            if(detail.getDisPer() == null) detail.setDisPer(0.0);
            if(detail.getDiscAmt() == null) detail.setDiscAmt(0.0);
            return detail;
        }).collect(Collectors.toList());

        return programDetails;
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








