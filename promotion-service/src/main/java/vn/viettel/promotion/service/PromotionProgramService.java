package vn.viettel.promotion.service;

import vn.viettel.core.db.entity.promotion.PromotionProgram;
import vn.viettel.core.db.entity.promotion.PromotionProgramDiscount;
import vn.viettel.core.db.entity.promotion.PromotionSaleProduct;
import vn.viettel.core.db.entity.promotion.*;
import vn.viettel.core.messaging.Response;

import java.util.List;

public interface PromotionProgramService {
    Response<List<PromotionProgramDiscount>> listPromotionProgramDiscountByOrderNumber(String orderNumber);
    Response<PromotionProgram> getPromotionProgramById(long Id);
    Response<List<PromotionSaleProduct>> listPromotionSaleProductsByProductId(long productId);

    Response<List<PromotionCustATTR>> getGroupCustomerMatchProgram(Long shopId);
    Response<List<PromotionProgramDetail>> getPromotionDetailByPromotionId(Long shopId);
    Response<List<PromotionProgramProduct>> getRejectProduct(List<Long> ids, Long productId);
}
