package vn.viettel.promotion.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.core.security.anotation.RoleFeign;
import vn.viettel.promotion.entities.*;
import vn.viettel.promotion.service.PromotionProgramService;

import java.util.List;

@RestController
@RequestMapping("/api/promotion/")
public class PromotionController extends BaseController {
    @Autowired
    PromotionProgramService promotionProgramDiscountService;

    @RoleFeign
    @GetMapping("/promotion-program-discount/{orderNumber}")
    Response<List<PromotionProgramDiscount>> listPromotionProgramDiscountByOrderNumber(@PathVariable String orderNumber) {
        return promotionProgramDiscountService.listPromotionProgramDiscountByOrderNumber(orderNumber);
    }

    @RoleFeign
    @GetMapping("/{id}")
    Response<PromotionProgram> getById(@PathVariable Long id) {
        return promotionProgramDiscountService.getPromotionProgramById(id);
    }

    @RoleFeign
    @GetMapping("/available-promotion-cus-attr/{shopId}")
    public Response<List<PromotionCustATTR>> getGroupCustomerMatchProgram(@PathVariable Long shopId) {
        return promotionProgramDiscountService.getGroupCustomerMatchProgram(shopId);
    }

    @RoleFeign
    @GetMapping("/get-promotion-detail/{shopId}")
    public Response<List<PromotionProgramDetail>> getPromotionDetailByPromotionId(@PathVariable Long shopId) {
        return promotionProgramDiscountService.getPromotionDetailByPromotionId(shopId);
    }

    @RoleFeign
    @GetMapping("/get-rejected-products")
    public Response<List<PromotionProgramProduct>> getRejectProduct(@RequestParam List<Long> ids) {
        return promotionProgramDiscountService.getRejectProduct(ids);
    }

    @RoleFeign
    @GetMapping("get-promotion-shop-map")
    public Response<PromotionShopMap> getPromotionShopMap(@RequestParam Long promotionProgramId,
                                                          @RequestParam Long shopId) {
        return promotionProgramDiscountService.getPromotionShopMap(promotionProgramId, shopId);
    }

    @RoleFeign
    @PutMapping("save-change-promotion-shop-map")
    public void saveChangePromotionShopMap(@RequestBody PromotionShopMap promotionShopMap,
                                           @RequestParam float amountReceived, @RequestParam Integer quantityReceived) {
        promotionProgramDiscountService.saveChangePromotionShopMap(promotionShopMap, amountReceived, quantityReceived);
    }

    @RoleFeign
    @GetMapping("get-zm-promotion")
    public Response<List<PromotionSaleProduct>> getZmPromotion(@RequestParam Long productId) {
        return promotionProgramDiscountService.getZmPromotionByProductId(productId);
    }

    @RoleFeign
    @RoleAdmin
    @PostMapping("get-free-items/{programId}")
    public Response<List<PromotionProductOpen>> getFreeItem(@PathVariable Long programId) {
        return  promotionProgramDiscountService.getFreeItems(programId);
    }

    @RoleFeign
    @GetMapping("get-promotion-discount")
    public Response<List<PromotionProgramDiscount>> getPromotionDiscount(@RequestParam List<Long> ids, @RequestParam String cusCode) {
        return promotionProgramDiscountService.getPromotionDiscount(ids, cusCode);
    }
}
