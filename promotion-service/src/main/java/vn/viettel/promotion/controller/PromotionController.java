package vn.viettel.promotion.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.db.entity.promotion.PromotionProgram;
import vn.viettel.core.db.entity.promotion.PromotionProgramDiscount;
import vn.viettel.core.messaging.Response;
import vn.viettel.promotion.service.PromotionProgramDiscountService;

import java.util.List;

@RestController
@RequestMapping("/api/promotion")
public class PromotionController {
    @Autowired
    PromotionProgramDiscountService promotionProgramDiscountService;

    @GetMapping("/promotion-discount-program/{orderNumber}")
    Response<List<PromotionProgramDiscount>> listPromotionProgramDiscountByOrderNumber(@PathVariable String orderNumber) {
        return promotionProgramDiscountService.listPromotionProgramDiscountByOrderNumber(orderNumber);
    }

    @GetMapping("/promotion-program-by-id/{id}")
    Response<PromotionProgram> getById(@PathVariable Long id) {
        return promotionProgramDiscountService.getPromotionProgramById(id);
    }
}
