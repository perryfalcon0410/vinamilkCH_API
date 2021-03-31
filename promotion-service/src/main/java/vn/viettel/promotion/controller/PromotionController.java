package vn.viettel.promotion.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.db.entity.promotion.*;
import vn.viettel.core.messaging.Response;
import vn.viettel.promotion.service.PromotionProgramService;
import vn.viettel.promotion.service.dto.RejectedProductDTO;

import java.util.List;

@RestController
@RequestMapping("/api/promotion")
public class PromotionController {
    @Autowired
    PromotionProgramService promotionProgramDiscountService;

    @GetMapping("/promotion-discount-program/{orderNumber}")
    Response<List<PromotionProgramDiscount>> listPromotionProgramDiscountByOrderNumber(@PathVariable String orderNumber) {
        return promotionProgramDiscountService.listPromotionProgramDiscountByOrderNumber(orderNumber);
    }

    @GetMapping("/promotion-program-by-id/{id}")
    Response<PromotionProgram> getById(@PathVariable Long id) {
        return promotionProgramDiscountService.getPromotionProgramById(id);
    }

    @GetMapping("/available-promotion-cus-attr/{shopId}")
    public Response<List<PromotionCustATTR>> getGroupCustomerMatchProgram(@PathVariable Long shopId) {
        return promotionProgramDiscountService.getGroupCustomerMatchProgram(shopId);
    }

    @GetMapping("/get-promotion-detail/{shopId}")
    public Response<List<PromotionProgramDetail>> getPromotionDetailByPromotionId(@PathVariable Long shopId) {
        return promotionProgramDiscountService.getPromotionDetailByPromotionId(shopId);
    }

    @GetMapping("/get-rejected-products")
    public Response<List<PromotionProgramProduct>> getRejectProduct(@RequestBody RejectedProductDTO body) {
        return promotionProgramDiscountService.getRejectProduct(body.getIds(), body.getProductId());
    }
}
