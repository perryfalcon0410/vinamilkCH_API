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
import vn.viettel.sale.messaging.PromotionProductRequest;
import vn.viettel.sale.repository.ProductPriceRepository;
import vn.viettel.sale.repository.SaleOrderDiscountRepository;
import vn.viettel.sale.repository.SaleOrderRepository;
import vn.viettel.sale.service.SalePromotionService;
import vn.viettel.sale.service.dto.AutoPromotionDTO;
import vn.viettel.sale.service.dto.AutoPromotionDiscountDTO;
import vn.viettel.sale.service.dto.ProductOrderDataDTO;
import vn.viettel.sale.service.dto.ProductOrderDetailDataDTO;
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
    public List<AutoPromotionDTO> getFreeItems(PromotionProductRequest request, Long shopId, Long customerId) {
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

        AutoPromotionDiscountDTO discountDTO = new AutoPromotionDiscountDTO();
        for(PromotionProgramDetailDTO promotion: maxs) {
            PromotionShopMapDTO promotionShopMap = promotionClient.getPromotionShopMapV1(program.getId(), shopId).getData();
            if(promotionShopMap == null) throw new ValidateException(ResponseMessage.PROMOTION_SHOP_MAP_NOT_EXISTS);

            if(program.getDiscountPriceType() == PriceType.NOT_VAT.getValue()) {
                double discount = orderData.getTotalPriceNotVAT()*(promotion.getDisPer()/100);
                discountDTO.addPrice(discount);
            }
            if(program.getDiscountPriceType() == PriceType.VAT.getValue()) {
                double discount = orderData.getTotalPrice()*(promotion.getDisPer()/100);
                discountDTO.addPrice(discount);
            }

            auto.setDiscount(discountDTO);
            if(promotionShopMap.getQuantityMax() == null) {
                auto.setIsUse(true);
            }else {
                double quantityReceive = promotionShopMap.getQuantityReceived()!=null?promotionShopMap.getQuantityReceived():0;
                if(promotionShopMap.getQuantityMax() >= (quantityReceive + auto.getDiscount().getPrice())) auto.setIsUse(true);
                else auto.setIsUse(false);
            }

            auto.setProgramId(program.getId());
            auto.setPromotionProgramName(program.getPromotionProgramName());
        }

        return auto;
    }

    //Kiểm tra các chwuong trình hợp lệ
    public List<PromotionProgramDTO> validPromotionProgram(PromotionProductRequest request, Long shopId, CustomerDTO customer) {

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

    public ProductOrderDataDTO getProductOrderData(PromotionProductRequest request, CustomerDTO customer) {
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








