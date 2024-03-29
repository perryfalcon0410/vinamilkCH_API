package vn.viettel.promotion.service;

import vn.viettel.core.dto.promotion.*;

import java.util.List;
import java.util.Map;

public interface PromotionProgramService {
    List<PromotionProgramDiscountDTO> listPromotionProgramDiscountByOrderNumber(String orderNumber);
    PromotionProgramDTO getPromotionProgramById(long Id);

    /*
    Lấy danh sách ctkm theo tập id
     */
    List<PromotionProgramDTO> findByIds(List<Long> promotionIds);

    List<PromotionProgramProductDTO> findByPromotionIds(List<Long> promotionIds);

    /*
      Lấy shop map
     */
    PromotionShopMapDTO getPromotionShopMap(Long promotionProgramId, Long shopId);
    PromotionShopMapDTO updatePromotionShopMap(PromotionShopMapDTO shopMap);
    List<PromotionProductOpenDTO> getFreeItems(long programId);
    /*
      Lấy mã giảm giá cho sale
     */
    PromotionProgramDiscountDTO getPromotionDiscount(String discountCode, Long shopId);
    Boolean isReturn(String code);
    /*
     Lấy tất cả các chương trình mà shop hiện tại được hưởng
    */
    List<PromotionProgramDTO> findPromotionPrograms(Long shopId, Long orderType, Long customerTypeId, Long memberCardId, Long cusCloselyTypeId
            ,Long cusCardTypeId);
    List<PromotionProgramDetailDTO> findPromotionDetailByProgramId(Long programId, List<Long> productIds);
    List<PromotionProgramDiscountDTO> findPromotionDiscountByPromotion(Long promotionId);
    PromotionProgramDiscountDTO updatePromotionProgramDiscount(PromotionProgramDiscountDTO discount);
    List<PromotionSaleProductDTO> findPromotionSaleProductByProgramId(Long programId);

    Boolean checkPromotionSaleProduct(Long programId, List<Long> productIds);

    /*
     Update MGG cho đơn trả
    */
    List<Long> returnMGG(String orderCode, Long shopId);

    /*
        Update số xuất cho đơn trả
     */
    List<Long> returnPromotionShopmap(Map<String, Double> shopMaps, Long shopId);

    PromotionProgramDTO getPromotionProgram(String code);
}
