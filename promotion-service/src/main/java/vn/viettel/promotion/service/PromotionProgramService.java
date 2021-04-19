package vn.viettel.promotion.service;

import vn.viettel.core.messaging.Response;
import vn.viettel.promotion.entities.*;
import java.util.List;

public interface PromotionProgramService {
    Response<List<PromotionProgramDiscount>> listPromotionProgramDiscountByOrderNumber(String orderNumber);
    Response<PromotionProgram> getPromotionProgramById(long Id);
    Response<List<PromotionSaleProduct>> listPromotionSaleProductsByProductId(long productId);

    Response<List<PromotionCustATTR>> getGroupCustomerMatchProgram(Long shopId);
    Response<List<PromotionProgramDetail>> getPromotionDetailByPromotionId(Long shopId);
    Response<List<PromotionProgramProduct>> getRejectProduct(List<Long> ids);
    Response<PromotionShopMap> getPromotionShopMap(Long promotionProgramId, Long shopId);
    void saveChangePromotionShopMap(PromotionShopMap promotionShopMap, float amountReceived, Integer quantityReceived);
    Response<List<PromotionSaleProduct>> getZmPromotionByProductId(long productId);
    Response<List<PromotionProductOpen>> getFreeItems(long programId);
    Response<List<PromotionProgramDiscount>> getPromotionDiscount(List<Long> ids, String cusCode);
}
