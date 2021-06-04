package vn.viettel.sale.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.customer.MemberCardDTO;
import vn.viettel.core.dto.promotion.PromotionProgramDTO;
import vn.viettel.core.dto.promotion.PromotionProgramDetailDTO;
import vn.viettel.core.dto.promotion.PromotionShopMapDTO;
import vn.viettel.core.enums.PromotionCustObjectType;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.entities.Price;
import vn.viettel.sale.entities.SaleOrder;
import vn.viettel.sale.messaging.ProductOrderRequest;
import vn.viettel.sale.messaging.OrderPromotionRequest;
import vn.viettel.sale.messaging.SalePromotionCalculationRequest;
import vn.viettel.sale.repository.ProductPriceRepository;
import vn.viettel.sale.repository.SaleOrderDiscountRepository;
import vn.viettel.sale.repository.SaleOrderRepository;
import vn.viettel.sale.service.SalePromotionService;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.service.enums.PriceType;
import vn.viettel.sale.service.enums.PromotionProgramType;
import vn.viettel.sale.service.feign.CustomerClient;
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

        List<SalePromotionDTO> zv01zv21 = getAutoItemPromotionZV01ToZV21(request, shopId);
        if (zv01zv21 != null && zv01zv21.size() > 0){
            zv01zv21.stream().forEachOrdered(results::add);
        }

        List<SalePromotionDTO> zv23 = getItemPromotionZV23(request, shopId);
        if (zv23 != null && zv23.size() > 0){
            results.stream()
                    .forEachOrdered(zv23::add);
        }

        List<SalePromotionDTO> autoAmount = getAutoItemPromotionAmount(request, shopId);
        if (autoAmount != null && autoAmount.size() > 0){
            results.stream().forEachOrdered(autoAmount::add);
        }

        List<SalePromotionDTO> autoDiscount = getAutoItemPromotionDiscount(request, shopId);
        if (autoDiscount != null && autoDiscount.size() > 0){
            results.stream()
                    .forEachOrdered(autoDiscount::add);
        }

        List<SalePromotionDTO> zm = getItemPromotionZM(request, shopId);
        if (zm != null && zm.size() > 0){
            results.stream().forEachOrdered(zm::add);
        }

        List<SalePromotionDTO> amount = getItemPromotionAmount(request, shopId);
        if (amount != null && amount.size() > 0){
            results.stream().forEachOrdered(amount::add);
        }

        List<SalePromotionDTO> discount = getItemPromotionDiscount(request, shopId);
        if (discount != null && discount.size() > 0){
            results.stream().forEachOrdered(discount::add);
        }

        List<SalePromotionDTO> newYear = getItemPromotionNewYear(request, shopId);
        if (newYear != null && newYear.size() > 0){
            results.stream().forEachOrdered(newYear::add);
        }

        return results;
    }

    /*
    Lấy danh sách khuyến mãi ZV01 đến ZV21
     */
    private List<SalePromotionDTO> getAutoItemPromotionZV01ToZV21(OrderPromotionRequest request, Long shopId){
        List<SalePromotionDTO> results = new ArrayList<>();
        CustomerDTO customer = customerClient.getCustomerByIdV1(request.getCustomerId()).getData();
        if(customer == null) throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
        // Danh sách chương trình khuyến mãi thỏa các điều kiện cửa hàng, khách hàng
        List<PromotionProgramDTO> programs = this.validPromotionProgram(request, shopId, customer);
        if(programs.isEmpty()) return null;

        ProductOrderDataDTO orderData = this.getProductOrderData(request, customer);
        for (PromotionProgramDTO program: programs) {
            SalePromotionDTO auto = null;
            switch (PromotionProgramType.valueOf(program.getType())) {
                case ZV01:
                    auto = this.zV01(program, orderData, shopId);
                    break;
                case ZV02:
                    // Todo
                    break;
                case ZV03:
                    // Todo
                    break;
                case ZV04:
                    auto = this.zV04(program, orderData, shopId);
                    break;
                case ZV05:
                    // Todo
                    break;
                default:
                    // Todo
            }

            if(auto != null) results.add(auto);
        }


        return results;
    }

    /*
    Lấy danh sách khuyến mãi tay ZM
     */
    private List<SalePromotionDTO> getItemPromotionZM(OrderPromotionRequest request, Long shopId){
        List<SalePromotionDTO> results = new ArrayList<>();

        return results;
    }

    /*
    Lấy danh sách khuyến mãi ZV23
     */
    private List<SalePromotionDTO> getItemPromotionZV23(OrderPromotionRequest request, Long shopId){
        List<SalePromotionDTO> results = new ArrayList<>();

        return results;
    }

    /*
    Lấy danh sách khuyến mãi tay tặng tiền
     */
    private List<SalePromotionDTO> getItemPromotionAmount(OrderPromotionRequest request, Long shopId){
        List<SalePromotionDTO> results = new ArrayList<>();

        return results;
    }

    /*
    Lấy danh sách khuyến mãi tay giảm giá
     */
    private List<SalePromotionDTO> getItemPromotionDiscount(OrderPromotionRequest request, Long shopId){
        List<SalePromotionDTO> results = new ArrayList<>();

        return results;
    }

    /*
    Lấy danh sách khuyến mãi tự động tặng tiền
     */
    private List<SalePromotionDTO> getAutoItemPromotionAmount(OrderPromotionRequest request, Long shopId){
        List<SalePromotionDTO> results = new ArrayList<>();

        return results;
    }

    /*
    Lấy danh sách khuyến mãi tự động giảm giá
     */
    private List<SalePromotionDTO> getAutoItemPromotionDiscount(OrderPromotionRequest request, Long shopId){
        List<SalePromotionDTO> results = new ArrayList<>();

        return results;
    }

    /*
    Lấy danh sách khuyến mãi tích lỹ quà tết
     */
    private List<SalePromotionDTO> getItemPromotionNewYear(OrderPromotionRequest request, Long shopId){
        List<SalePromotionDTO> results = new ArrayList<>();

        return results;
    }

    /*
    Lấy danh sách sản phẩm khuyến mãi cho chương trình khuyến mãi tay
     */
    @Override
    public List<FreeProductDTO> getPromotionProduct(Long promotionId, Long shopId) {
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

        return lstProduct1;
    }

    /*
    Tính tiền khuyến mãi và tiền cần thanh toán cho 1 đơn hàng
     */
    @Override
    public SalePromotionCalculationDTO promotionCalculation(SalePromotionCalculationRequest calculationRequest, Long shopId){
        SalePromotionCalculationDTO result = new SalePromotionCalculationDTO();
        result.setPromotionAmount(500000.0);
        result.setPaymentAmount(1500000.0);
        return result;
    }


    public SalePromotionDTO zV01(PromotionProgramDTO program, ProductOrderDataDTO orderData, Long shopId) {
        SalePromotionDTO auto = new SalePromotionDTO();

        List<ProductOrderDetailDataDTO> productOrders = orderData.getProducts();
        List<PromotionProgramDetailDTO> programDetails = promotionClient.findPromotionProgramDetailV1(program.getId()).getData();
        if(programDetails.isEmpty()) return null;
        // tat ca cac dk sp phai co trong chuong trinh
        Map<Long, List<PromotionProgramDetailDTO>> programDetailsReqs = programDetails.stream().filter(detail -> {
            if(detail.getRequired()!=null && detail.getRequired() == 1) {
                if(detail.getSaleQty() == null) detail.setSaleQty(0);
                return true;
            }
            return false;
        } ).collect(Collectors.groupingBy(PromotionProgramDetailDTO::getProductId));
        if(!programDetailsReqs.isEmpty()) {
            //dk cao nhất
            Map<Long, Integer> detailsReqs = new HashMap<>();
            for (Map.Entry<Long, List<PromotionProgramDetailDTO>> entry : programDetailsReqs.entrySet()) {
                PromotionProgramDetailDTO dto = entry.getValue().stream().max(Comparator.comparing(PromotionProgramDetailDTO::getSaleQty)).get();
                detailsReqs.put(dto.getProductId(), dto.getSaleQty());
            }
            List<ProductOrderDetailDataDTO> productEquals = productOrders.stream().filter(product ->
                    detailsReqs.keySet().contains(product.getProductId()) && product.getQuantity() >= detailsReqs.get(product.getProductId()))
                    .collect(Collectors.toList());
            if(productEquals.size() != detailsReqs.size()) return null;
        }

        // all detail duoc huong
        Map<Long, List<PromotionProgramDetailDTO>> promotions = programDetails.stream().filter(detail -> {
            for(ProductOrderDetailDataDTO product: productOrders) {
                if(detail.getProductId().equals(product.getProductId()) && product.getQuantity() >= detail.getSaleQty()) return true;
            }
            return false;
        }).collect(Collectors.groupingBy(PromotionProgramDetailDTO::getProductId));

        // detail duoc huong cao nhat
        List<PromotionProgramDetailDTO> maxs = new ArrayList<>();
        for (Map.Entry<Long, List<PromotionProgramDetailDTO>> entry : promotions.entrySet()) {
            PromotionProgramDetailDTO dto = entry.getValue().stream().max(Comparator.comparing(PromotionProgramDetailDTO::getDisPer)).get();
            maxs.add(dto);
        }

        SalePromotionDiscountDTO discountDTO = new SalePromotionDiscountDTO();
        PromotionShopMapDTO promotionShopMap = promotionClient.getPromotionShopMapV1(program.getId(), shopId).getData();
        if(promotionShopMap == null) throw new ValidateException(ResponseMessage.PROMOTION_SHOP_MAP_NOT_EXISTS);

        Map<Long, ProductOrderDetailDataDTO> productsOrderMap = productOrders.stream()
                .collect(Collectors.toMap(ProductOrderDetailDataDTO::getProductId, Function.identity()));
        for(PromotionProgramDetailDTO promotion: maxs) {
            ProductOrderDetailDataDTO product = productsOrderMap.get(promotion.getProductId());
            if((program.getDiscountPriceType() == PriceType.VAT.getValue()))
                discountDTO.addAmount( product.getTotalPrice()*(promotion.getDisPer()/100));
            if((program.getDiscountPriceType() == PriceType.NOT_VAT.getValue()))
                discountDTO.addAmount( product.getTotalPriceNotVAT()*(promotion.getDisPer()/100));
        }

        auto.setAmount(discountDTO);
        if(promotionShopMap.getQuantityMax() == null) {
            auto.setIsUse(true);
        }else {
            double quantityReceive = promotionShopMap.getQuantityReceived()!=null?promotionShopMap.getQuantityReceived():0;
            if(promotionShopMap.getQuantityMax() >= (quantityReceive + auto.getAmount().getAmount())) auto.setIsUse(true);
            else auto.setIsUse(false);
        }

        auto.setPromotionType(0);
        auto.setProgramId(program.getId());
        auto.setPromotionProgramName(program.getPromotionProgramName());

        return auto;
    }

    public SalePromotionDTO zV04(PromotionProgramDTO program, ProductOrderDataDTO orderData, Long shopId) {
        SalePromotionDTO auto = new SalePromotionDTO();
        List<ProductOrderDetailDataDTO> productOrders = orderData.getProducts();
        List<PromotionProgramDetailDTO> programDetails = promotionClient.findPromotionProgramDetailV1(program.getId()).getData();
        Map<Long, ProductOrderDetailDataDTO> productsOrderMap = productOrders.stream()
                .collect(Collectors.toMap(ProductOrderDetailDataDTO::getProductId, Function.identity()));
        if(programDetails.isEmpty()) return null;
        //Kt dieu kien san pham
        Map<Long, List<PromotionProgramDetailDTO>> programDetailsReqs = programDetails.stream()
                .filter(detail -> {
                    if(detail.getRequired()!=null && detail.getRequired() == 1) {
                        if(detail.getSaleAmt() == null) detail.setSaleAmt(0.0);
                        return true;
                    }
                    return false;
                })
                .collect(Collectors.groupingBy(PromotionProgramDetailDTO::getProductId));
        //DK cao nhat
        Map<Long, Double> detailsReqs = new HashMap<>();
        for (Map.Entry<Long, List<PromotionProgramDetailDTO>> entry : programDetailsReqs.entrySet()) {
            PromotionProgramDetailDTO dto = entry.getValue().stream().max(Comparator.comparing(PromotionProgramDetailDTO::getSaleAmt)).get();
            detailsReqs.put(dto.getProductId(), dto.getSaleAmt());
        }

        PromotionShopMapDTO promotionShopMap = promotionClient.getPromotionShopMapV1(program.getId(), shopId).getData();
        if(promotionShopMap == null) throw new ValidateException(ResponseMessage.PROMOTION_SHOP_MAP_NOT_EXISTS);
        SalePromotionDiscountDTO discountDTO = new SalePromotionDiscountDTO();

        // chuong trinh tinh KM tren gia truoc thue + dk tong gia mua cua sp
        if(program.getDiscountPriceType() == PriceType.NOT_VAT.getValue()) {
            List<ProductOrderDetailDataDTO> productEquals = productOrders.stream().filter(product ->
                    detailsReqs.keySet().contains(product.getProductId()) && product.getTotalPriceNotVAT() >= detailsReqs.get(product.getProductId()))
                    .collect(Collectors.toList());
            if(productEquals.size() != detailsReqs.size()) return null;
            // all detail duoc huong
            Map<Long, List<PromotionProgramDetailDTO>> promotions = programDetails.stream().filter(detail -> {
                for(ProductOrderDetailDataDTO product: productOrders) {
                    if(detail.getProductId().equals(product.getProductId()) && product.getTotalPriceNotVAT() >= detail.getSaleAmt()) return true;
                }
                return false;
            }).collect(Collectors.groupingBy(PromotionProgramDetailDTO::getProductId));
            if(promotions.isEmpty()) return null;
            // detail duoc huong cao nhat
            List<PromotionProgramDetailDTO> maxs = new ArrayList<>();
            for (Map.Entry<Long, List<PromotionProgramDetailDTO>> entry : promotions.entrySet()) {
                PromotionProgramDetailDTO dto = entry.getValue().stream().max(Comparator.comparing(PromotionProgramDetailDTO::getDisPer)).get();
                maxs.add(dto);
            }
            for(PromotionProgramDetailDTO promotion: maxs) {
                ProductOrderDetailDataDTO product = productsOrderMap.get(promotion.getProductId());
                discountDTO.addAmount(product.getTotalPriceNotVAT()*(promotion.getDisPer()/100));
            }

        }
        // chuong trinh tinh KM tren gia sau thue + dk tong gia mua cua sp
        if(program.getDiscountPriceType() == PriceType.VAT.getValue()) {
            List<ProductOrderDetailDataDTO> productEquals = productOrders.stream().filter(product ->
                    detailsReqs.keySet().contains(product.getProductId()) && product.getTotalPrice() >= detailsReqs.get(product.getProductId()))
                    .collect(Collectors.toList());
            if(productEquals.size() != detailsReqs.size()) return null;
            // all detail duoc huong
            Map<Long, List<PromotionProgramDetailDTO>> promotions = programDetails.stream().filter(detail -> {
                for(ProductOrderDetailDataDTO product: productOrders) {
                    if(detail.getProductId().equals(product.getProductId()) && product.getTotalPrice() >= detail.getSaleAmt()) return true;
                }
                return false;
            }).collect(Collectors.groupingBy(PromotionProgramDetailDTO::getProductId));
            if(promotions.isEmpty()) return null;
            // detail duoc huong cao nhat
            List<PromotionProgramDetailDTO> maxs = new ArrayList<>();
            for (Map.Entry<Long, List<PromotionProgramDetailDTO>> entry : promotions.entrySet()) {
                PromotionProgramDetailDTO dto = entry.getValue().stream().max(Comparator.comparing(PromotionProgramDetailDTO::getDisPer)).get();
                maxs.add(dto);
            }
            for(PromotionProgramDetailDTO promotion: maxs) {
                ProductOrderDetailDataDTO product = productsOrderMap.get(promotion.getProductId());
                discountDTO.addAmount(product.getTotalPrice()*(promotion.getDisPer()/100));
            }
        }

        auto.setAmount(discountDTO);
        if(promotionShopMap.getQuantityMax() == null) {
            auto.setIsUse(true);
        }else {
            double quantityReceive = promotionShopMap.getQuantityReceived()!=null?promotionShopMap.getQuantityReceived():0;
            if(promotionShopMap.getQuantityMax() >= (quantityReceive + auto.getAmount().getAmount())) auto.setIsUse(true);
            else auto.setIsUse(false);
        }

        auto.setPromotionType(0);
        auto.setProgramId(program.getId());
        auto.setPromotionProgramName(program.getPromotionProgramName());

        return auto;
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
        for (ProductOrderRequest product: products) {
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








