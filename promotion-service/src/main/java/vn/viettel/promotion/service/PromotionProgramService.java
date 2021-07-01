package vn.viettel.promotion.service;

import vn.viettel.core.dto.promotion.*;

import java.util.List;

public interface PromotionProgramService {
    List<PromotionProgramDiscountDTO> listPromotionProgramDiscountByOrderNumber(String orderNumber);
    PromotionProgramDTO getPromotionProgramById(long Id);
    PromotionProgramDTO getPromotionProgramByCode(String code);

    List<PromotionCustATTRDTO> getGroupCustomerMatchProgram(Long shopId);
    List<PromotionProgramDetailDTO> getPromotionDetailByPromotionId(Long shopId);
    List<PromotionProgramProductDTO> findByPromotionIds(List<Long> promotionIds);
    PromotionShopMapDTO getPromotionShopMap(Long promotionProgramId, Long shopId);
    PromotionShopMapDTO updatePromotionShopMap(PromotionShopMapDTO shopMap);
    List<PromotionSaleProductDTO> getZmPromotionByProductId(long productId);
    List<PromotionProductOpenDTO> getFreeItems(long programId);
    List<PromotionProgramDiscountDTO> getPromotionDiscounts(List<Long> ids, String cusCode);
    /*
      Lấy mã giảm giá cho sale
     */
    PromotionProgramDiscountDTO getPromotionDiscount(String discountCode, Long shopId);
    Boolean isReturn(String code);
    Double getDiscountPercent(String type, String code, Double amount);
    Long checkBuyingCondition(String type, Integer quantity, Double amount, List<Long> ids);
    List<Long> getRequiredProducts(String type);
    /*
     Lấy tất cả các chương trình mà shop hiện tại được hưởng
    */
    List<PromotionProgramDTO> findPromotionPrograms(Long shopId);
    List<PromotionProgramDetailDTO> findPromotionDetailByProgramId(Long programId);
    List<PromotionSaleProductDTO> findPromotionSaleProductByProgramId(Long programId);
    List<PromotionProgramDiscountDTO> findPromotionDiscountByPromotion(Long promotionId);
    PromotionProgramDiscountDTO updatePromotionProgramDiscount(PromotionProgramDiscountDTO discount);
}
