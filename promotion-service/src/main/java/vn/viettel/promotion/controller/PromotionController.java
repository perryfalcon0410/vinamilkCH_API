package vn.viettel.promotion.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.db.entity.promotion.*;
import vn.viettel.core.messaging.Response;
import vn.viettel.promotion.service.PromotionProgramService;

import java.util.List;

@RestController
@RequestMapping("/api/promotion/")
public class PromotionController {
    @Autowired
    PromotionProgramService promotionProgramDiscountService;

    @GetMapping("/{orderNumber}")
    Response<List<PromotionProgramDiscount>> listPromotionProgramDiscountByOrderNumber(@PathVariable String orderNumber) {
        return promotionProgramDiscountService.listPromotionProgramDiscountByOrderNumber(orderNumber);
    }

    @GetMapping("/{id}")
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
    public Response<List<PromotionProgramProduct>> getRejectProduct(@RequestParam List<Long> ids) {
        return promotionProgramDiscountService.getRejectProduct(ids);
    }

    @GetMapping("get-promotion-shop-map")
    public Response<PromotionShopMap> getPromotionShopMap(@RequestParam Long promotionProgramId,
                                                          @RequestParam Long shopId) {
        return promotionProgramDiscountService.getPromotionShopMap(promotionProgramId, shopId);
    }

    @PutMapping("save-change-promotion-shop-map")
    public void saveChangePromotionShopMap(@RequestBody PromotionShopMap promotionShopMap,
                                           @RequestParam float amountReceived, @RequestParam Integer quantityReceived) {
        promotionProgramDiscountService.saveChangePromotionShopMap(promotionShopMap, amountReceived, quantityReceived);
    }

    @GetMapping("get-zm-promotion")
    public Response<List<PromotionSaleProduct>> getZmPromotion(@RequestParam Long productId) {
        return promotionProgramDiscountService.getZmPromotionByProductId(productId);
    }

    @PostMapping("get-free-items/{programId}")
    public Response<List<PromotionProductOpen>> getFreeItem(@PathVariable Long programId) {
        return  promotionProgramDiscountService.getFreeItems(programId);
    }

    @GetMapping("get-promotion-discount")
    public Response<List<PromotionProgramDiscount>> getPromotionDiscount(@RequestParam List<Long> ids, @RequestParam String cusCode) {
        return promotionProgramDiscountService.getPromotionDiscount(ids, cusCode);
    }
}
