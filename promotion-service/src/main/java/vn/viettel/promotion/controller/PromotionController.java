package vn.viettel.promotion.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.promotion.*;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleFeign;
import vn.viettel.promotion.service.PromotionProgramService;

import java.util.List;

@RestController
@Api(tags = "Api sử dụng để lấy khuyến mãi của sản phẩm")
public class PromotionController extends BaseController {
    @Autowired
    PromotionProgramService promotionProgramDiscountService;
    private final String root = "/promotions";

    @RoleFeign
    @GetMapping(value = { V1 + root + "/promotion-program-discount/{orderNumber}"})
    Response<List<PromotionProgramDiscountDTO>> listPromotionProgramDiscountByOrderNumber(@PathVariable String orderNumber) {
        return promotionProgramDiscountService.listPromotionProgramDiscountByOrderNumber(orderNumber);
    }

    @RoleFeign
    @ApiOperation(value = "Api dùng khi tạo đơn bán hàng để lấy thông tin chương trình khuyến mãi theo id")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root + "/{id}"})
    Response<PromotionProgramDTO> getById(@PathVariable Long id) {
        return promotionProgramDiscountService.getPromotionProgramById(id);
    }

    @RoleFeign
    @ApiOperation(value = "Api dùng khi tạo đơn bán hàng để lấy danh sách loại khách hàng được hưởng chương trình khuyến mãi")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root + "/available-promotion-cus-attr/{shopId}"})
    public Response<List<PromotionCustATTRDTO>> getGroupCustomerMatchProgram(@PathVariable Long shopId) {
        return promotionProgramDiscountService.getGroupCustomerMatchProgram(shopId);
    }

    @RoleFeign
    @ApiOperation(value = "Api dùng khi tạo đơn bán hàng để lấy thông tin chi tiết chương trình khuyến mãi")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root + "/get-promotion-detail/{shopId}"})
    public Response<List<PromotionProgramDetailDTO>> getPromotionDetailByPromotionId(@PathVariable Long shopId) {
        return promotionProgramDiscountService.getPromotionDetailByPromotionId(shopId);
    }

    @RoleFeign
    @ApiOperation(value = "Api dùng khi tạo đơn bán hàng để lấy danh sách sản phẩm loại trừ")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root + "/get-rejected-products"})
    public Response<List<PromotionProgramProductDTO>> getRejectProduct(@RequestParam List<Long> ids) {
        return promotionProgramDiscountService.getRejectProduct(ids);
    }

    @RoleFeign
    @ApiOperation(value = "Api dùng khi tạo đơn bán hàng để lấy thông tin chương trình khuyến mãi được áp dụng tại cửa hàng")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root + "/get-promotion-shop-map"})
    public Response<PromotionShopMapDTO> getPromotionShopMap(@RequestParam Long promotionProgramId,
                                                          @RequestParam Long shopId) {
        return promotionProgramDiscountService.getPromotionShopMap(promotionProgramId, shopId);
    }

    @RoleFeign
    @ApiOperation(value = "Api dùng khi tạo đơn bán hàng để cập nhập thông tin chương trình khuyến được áp dụng tại cửa hàng")
    @ApiResponse(code = 200, message = "Success")
    @PutMapping(value = { V1 + root + "/save-change-promotion-shop-map"})
    public void saveChangePromotionShopMap(@RequestBody PromotionShopMapDTO promotionShopMap,
                                           @RequestParam float amountReceived, @RequestParam Integer quantityReceived) {
        promotionProgramDiscountService.saveChangePromotionShopMap(promotionShopMap, amountReceived, quantityReceived);
    }

    @RoleFeign
    @ApiOperation(value = "Api dùng khi tạo đơn bán hàng để lấy thông tin khuyến mãi tay của sản phẩm")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root + "/get-zm-promotion"})
    public Response<List<PromotionSaleProductDTO>> getZmPromotion(@RequestParam Long productId) {
        return promotionProgramDiscountService.getZmPromotionByProductId(productId);
    }

//    @RoleFeign
    @ApiOperation(value = "Api dùng khi tạo đơn bán hàng để lấy danh sách sản phẩm khuyến mãi")
    @ApiResponse(code = 200, message = "Success")
    @PostMapping(value = { V1 + root + "/get-free-items/{programId}"})
    public Response<List<PromotionProductOpenDTO>> getFreeItem(@PathVariable Long programId) {
        return  promotionProgramDiscountService.getFreeItems(programId);
    }

//    @RoleFeign
    @ApiOperation(value = "Api dùng khi tạo đơn bán hàng để lấy số tiền/phần trăm được giảm")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root + "/get-promotion-discount"})
    public Response<List<PromotionProgramDiscountDTO>> getPromotionDiscount(@RequestParam List<Long> ids, @RequestParam String cusCode) {
        return promotionProgramDiscountService.getPromotionDiscount(ids, cusCode);
    }
}
