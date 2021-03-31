package vn.viettel.promotion.service;

import vn.viettel.core.db.entity.promotion.PromotionProgram;
import vn.viettel.core.db.entity.promotion.PromotionProgramDiscount;
import vn.viettel.core.messaging.Response;

import java.util.List;

public interface PromotionProgramService {
    Response<List<PromotionProgramDiscount>> listPromotionProgramDiscountByOrderNumber(String orderNumber);
    Response<PromotionProgram> getPromotionProgramById(Long Id);
}
