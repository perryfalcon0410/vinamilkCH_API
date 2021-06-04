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

        return results;
    }

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

    @Override
    public SalePromotionCalculationDTO promotionCalculation(SalePromotionCalculationRequest calculationRequest, Long shopId){
        SalePromotionCalculationDTO result = new SalePromotionCalculationDTO();
        result.setPromotionAmount(500000.0);
        result.setPaymentAmount(1500000.0);
        return result;
    }

    @Override
    public List<AutoPromotionDTO> getFreeItems(OrderPromotionRequest request, Long shopId, Long customerId) {
        CustomerDTO customer = customerClient.getCustomerByIdV1(customerId).getData();
        if(customer == null) throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
        // Danh sách chương trình khuyến mãi thỏa các điều kiện cửa hàng, khách hàng
        List<PromotionProgramDTO> programs = this.validPromotionProgram(request, shopId, customer);
        if(programs.isEmpty()) return null;

        List<AutoPromotionDTO> autoPromotionDTOS = new ArrayList<>();
        ProductOrderDataDTO orderData = this.getProductOrderData(request, customer);
        for (PromotionProgramDTO program: programs) {
            AutoPromotionDTO auto = null;
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
                    // Todo
                    break;
                case ZV05:
                    // Todo
                    break;
                default:
                    // Todo
            }

            if(auto != null) autoPromotionDTOS.add(auto);
        }

        return autoPromotionDTOS;
    }

    public AutoPromotionDTO zV01(PromotionProgramDTO program, ProductOrderDataDTO orderData, Long shopId) {
        AutoPromotionDTO auto = new AutoPromotionDTO();
        List<ProductOrderDetailDataDTO> productOrders = orderData.getProducts();
        List<PromotionProgramDetailDTO> programDetails = promotionClient.getPromotionDetailByPromotionIdV1(program.getId()).getData();

        Map<Long, List<PromotionProgramDetailDTO>> programDetailsReqs = programDetails.stream()
                                            .filter(detail -> detail.getRequired()!=null && detail.getRequired() == 1)
                                                .collect(Collectors.groupingBy(PromotionProgramDetailDTO::getProductId));
        if(!programDetailsReqs.isEmpty()) {
            //DK cao nhất
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
        for(PromotionProgramDetailDTO promotion: maxs) {
            PromotionShopMapDTO promotionShopMap = promotionClient.getPromotionShopMapV1(program.getId(), shopId).getData();
            if(promotionShopMap == null) throw new ValidateException(ResponseMessage.PROMOTION_SHOP_MAP_NOT_EXISTS);

//            if(program.getDiscountPriceType() == PriceType.NOT_VAT.getValue()) {
//                double discount = orderData.getTotalPriceNotVAT()*(promotion.getDisPer()/100);
//                discountDTO.addPrice(discount);
//            }
//            if(program.getDiscountPriceType() == PriceType.VAT.getValue()) {
//                double discount = orderData.getTotalPrice()*(promotion.getDisPer()/100);
//                discountDTO.addPrice(discount);
//            }

            auto.setDiscount(discountDTO);
            if(promotionShopMap.getQuantityMax() == null) {
                auto.setIsUse(true);
            }else {
//                double quantityReceive = promotionShopMap.getQuantityReceived()!=null?promotionShopMap.getQuantityReceived():0;
//                if(promotionShopMap.getQuantityMax() >= (quantityReceive + auto.getDiscount().getPrice())) auto.setIsUse(true);
//                else auto.setIsUse(false);
            }

            auto.setProgramId(program.getId());
            auto.setPromotionProgramName(program.getPromotionProgramName());
        }

        return auto;
    }

    //Kiểm tra các chwuong trình hợp lệ
    public List<PromotionProgramDTO> validPromotionProgram(OrderPromotionRequest request, Long shopId, CustomerDTO customer) {

        List<PromotionProgramDTO> programs = promotionClient.findPromotionPrograms(shopId).getData();
        // Kiểm tra loại đơn hàng tham gia & Kiểm tra thuộc tính khách hàng tham gia
        List<PromotionProgramDTO> programDTOS = programs.stream().filter(program -> {
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

            Set<Long> layalCustomers = promotionClient.findCusCardPromotion(program.getId(), PromotionCustObjectType.LOYAL_CUSTOMER.getValue()).getData();
            if(!layalCustomers.isEmpty() && !layalCustomers.contains(customer.getCloselyTypeId())) return false;

            Set<Long> customerCardTypes = promotionClient.findCusCardPromotion(program.getId(), PromotionCustObjectType.CUSTOMER_CARD_TYPE.getValue()).getData();
            if(!customerCardTypes.isEmpty() && !customerCardTypes.contains(customer.getCardTypeId())) return false;

            return true;
        }).collect(Collectors.toList());

        return programDTOS;
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








