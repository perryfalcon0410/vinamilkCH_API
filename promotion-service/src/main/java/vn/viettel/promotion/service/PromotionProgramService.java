package vn.viettel.promotion.service;

import vn.viettel.core.db.entity.promotion.*;
import vn.viettel.core.messaging.Response;

import java.util.List;

public interface PromotionProgramService {
    Response<List<PromotionProgramDiscount>> listPromotionProgramDiscountByOrderNumber(String orderNumber);
    Response<PromotionProgram> getPromotionProgramById(Long Id);

    Response<List<PromotionCustATTR>> getGroupCustomerMatchProgram(Long shopId);
    Response<List<PromotionProgramDetail>> getPromotionDetailByPromotionId(Long shopId);
    Response<List<PromotionProgramProduct>> getRejectProduct(List<Long> ids, Long productId);
}
