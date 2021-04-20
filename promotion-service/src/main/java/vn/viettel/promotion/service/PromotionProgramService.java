package vn.viettel.promotion.service;

import vn.viettel.core.dto.*;
import vn.viettel.core.messaging.Response;
import vn.viettel.promotion.entities.*;
import java.util.List;

public interface PromotionProgramService {
    Response<List<PromotionProgramDiscountDTO>> listPromotionProgramDiscountByOrderNumber(String orderNumber);
    Response<PromotionProgramDTO> getPromotionProgramById(long Id);
    Response<List<PromotionSaleProductDTO>> listPromotionSaleProductsByProductId(long productId);

    Response<List<PromotionCustATTRDTO>> getGroupCustomerMatchProgram(Long shopId);
    Response<List<PromotionProgramDetailDTO>> getPromotionDetailByPromotionId(Long shopId);
    Response<List<PromotionProgramProductDTO>> getRejectProduct(List<Long> ids);
    Response<PromotionShopMapDTO> getPromotionShopMap(Long promotionProgramId, Long shopId);
    void saveChangePromotionShopMap(PromotionShopMapDTO promotionShopMap, float amountReceived, Integer quantityReceived);
    Response<List<PromotionSaleProductDTO>> getZmPromotionByProductId(long productId);
    Response<List<PromotionProductOpenDTO>> getFreeItems(long programId);
    Response<List<PromotionProgramDiscountDTO>> getPromotionDiscount(List<Long> ids, String cusCode);
}
