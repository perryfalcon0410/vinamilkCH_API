package vn.viettel.promotion.service;

import vn.viettel.core.dto.promotion.*;

import java.util.List;

public interface PromotionProgramService {
    List<PromotionProgramDiscountDTO> listPromotionProgramDiscountByOrderNumber(String orderNumber);
    PromotionProgramDTO getPromotionProgramById(long Id);
    PromotionProgramDTO getPromotionProgramByCode(String code);
    List<PromotionSaleProductDTO> listPromotionSaleProductsByProductId(long productId);

    List<PromotionCustATTRDTO> getGroupCustomerMatchProgram(Long shopId);
    List<PromotionProgramDetailDTO> getPromotionDetailByPromotionId(Long shopId);
    List<PromotionProgramProductDTO> getRejectProduct(List<Long> ids);
    PromotionShopMapDTO getPromotionShopMap(Long promotionProgramId, Long shopId);
    void saveChangePromotionShopMap(Long promotionProgramId, Long shopId, Float receivedQuantity);
    List<PromotionSaleProductDTO> getZmPromotionByProductId(long productId);
    List<PromotionProductOpenDTO> getFreeItems(long programId);
    List<PromotionProgramDiscountDTO> getPromotionDiscounts(List<Long> ids, String cusCode);
    PromotionProgramDiscountDTO getPromotionDiscount(String cusCode);
    Boolean isReturn(String code);
}
