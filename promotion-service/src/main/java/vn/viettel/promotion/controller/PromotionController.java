package vn.viettel.promotion.controller;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.promotion.*;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleFeign;
import vn.viettel.promotion.service.PromotionProgramService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Api(tags = "Api sử dụng để lấy khuyến mãi của sản phẩm")
public class PromotionController extends BaseController {
    @Autowired
    PromotionProgramService promotionProgramDiscountService;
    private final String root = "/promotions";

    @RoleFeign
    @GetMapping(value = { V1 + root + "/promotion-program-discount/{orderNumber}"})
    Response<List<PromotionProgramDiscountDTO>> listPromotionProgramDiscountByOrderNumber(HttpServletRequest request, @PathVariable String orderNumber) {
        List<PromotionProgramDiscountDTO> response = promotionProgramDiscountService.listPromotionProgramDiscountByOrderNumber(orderNumber);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_LIST_PROMOTION_PROGRAM_DISCOUNT_SUCCESS);
        return new Response<List<PromotionProgramDiscountDTO>>().withData(response);
    }

    @RoleFeign
    @ApiOperation(value = "Api dùng khi tạo đơn bán hàng để lấy thông tin chương trình khuyến mãi theo id")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root + "/{id}"})
    Response<PromotionProgramDTO> getById(HttpServletRequest request, @PathVariable Long id) {
        PromotionProgramDTO response = promotionProgramDiscountService.getPromotionProgramById(id);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_PROMOTION_PROGRAM_BY_ID_SUCCESS);
        return new Response<PromotionProgramDTO>().withData(response);
    }

    @RoleFeign
    @ApiOperation(value = "Api dùng khi tạo đơn bán hàng để lấy danh sách loại khách hàng được hưởng chương trình khuyến mãi")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root + "/available-promotion-cus-attr/{shopId}"})
    public Response<List<PromotionCustATTRDTO>> getGroupCustomerMatchProgram(HttpServletRequest request, @PathVariable Long shopId) {
        List<PromotionCustATTRDTO> reponse = promotionProgramDiscountService.getGroupCustomerMatchProgram(shopId);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_GROUP_CUSTOMER_MATCH_PROGRAM_SUCCESS);
        return new Response<List<PromotionCustATTRDTO>>().withData(reponse);
    }

    @RoleFeign
    @ApiOperation(value = "Api dùng khi tạo đơn bán hàng để lấy thông tin chi tiết chương trình khuyến mãi")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root + "/get-promotion-detail/{shopId}"})
    public Response<List<PromotionProgramDetailDTO>> getPromotionDetailByPromotionId(HttpServletRequest request, @PathVariable Long shopId) {
        List<PromotionProgramDetailDTO> response = promotionProgramDiscountService.getPromotionDetailByPromotionId(shopId);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_PROMOTION_DETAIL_BY_PROMOTION_ID_SUCCESS);
        return new Response<List<PromotionProgramDetailDTO>>().withData(response);
    }

    @RoleFeign
    @ApiOperation(value = "Api dùng khi tạo đơn bán hàng để lấy danh sách sản phẩm loại trừ")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root + "/get-rejected-products"})
    public Response<List<PromotionProgramProductDTO>> getRejectProduct(HttpServletRequest request, @RequestParam List<Long> ids) {
        List<PromotionProgramProductDTO> response = promotionProgramDiscountService.getRejectProduct(ids);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_REJECTED_PRODUCT_SUCCESS);
        return new Response<List<PromotionProgramProductDTO>>().withData(response);
    }

    @RoleFeign
    @ApiOperation(value = "Api dùng khi tạo đơn bán hàng để lấy thông tin chương trình khuyến mãi được áp dụng tại cửa hàng")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root + "/get-promotion-shop-map"})
    public Response<PromotionShopMapDTO> getPromotionShopMap(HttpServletRequest request, @RequestParam Long promotionProgramId,
                                                          @RequestParam Long shopId) {
        PromotionShopMapDTO response = promotionProgramDiscountService.getPromotionShopMap(promotionProgramId, shopId);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_PROMOTION_SHOP_MAP_SUCCESS);
        return new Response<PromotionShopMapDTO>().withData(response);
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
    public Response<List<PromotionSaleProductDTO>> getZmPromotion(HttpServletRequest request, @RequestParam Long productId) {
        List<PromotionSaleProductDTO> response = promotionProgramDiscountService.getZmPromotionByProductId(productId);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_ZM_PROMOTION_SUCCESS);
        return new Response<List<PromotionSaleProductDTO>>().withData(response);
    }

    @RoleFeign
    @ApiOperation(value = "Api dùng khi tạo đơn bán hàng để lấy danh sách sản phẩm khuyến mãi")
    @ApiResponse(code = 200, message = "Success")
    @PostMapping(value = { V1 + root + "/get-free-items/{programId}"})
    public Response<List<PromotionProductOpenDTO>> getFreeItem(HttpServletRequest request, @PathVariable Long programId) {
        List<PromotionProductOpenDTO> response = promotionProgramDiscountService.getFreeItems(programId);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_FREE_ITEMS_SUCCESS);
        return new Response<List<PromotionProductOpenDTO>>().withData(response);
    }

    @RoleFeign
    @ApiOperation(value = "Api dùng khi tạo đơn bán hàng để lấy số tiền/phần trăm được giảm")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root + "/get-promotion-discount"})
    public Response<List<PromotionProgramDiscountDTO>> getPromotionDiscounts(HttpServletRequest request, @RequestParam List<Long> ids, @RequestParam String cusCode) {
        List<PromotionProgramDiscountDTO> response = promotionProgramDiscountService.getPromotionDiscounts(ids, cusCode);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_PROMOTION_PROGRAM_DISCOUNT_SUCCESS);
        return new Response<List<PromotionProgramDiscountDTO>>().withData(response);
    }

    @ApiOperation(value = "Api lấy khuyến mãi theo mã")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root + "/promotion-program-discount/discount-code/{code}"})
    public Response<PromotionProgramDiscountDTO> getPromotionDiscount(HttpServletRequest request,@ApiParam("Mã giảm giá") @PathVariable("code") String cusCode) {
        PromotionProgramDiscountDTO response = promotionProgramDiscountService.getPromotionDiscount(cusCode);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_PROMOTION_PROGRAM_DISCOUNT_SUCCESS);
        return new Response<PromotionProgramDiscountDTO>().withData(response);
    }

    @RoleFeign
    @ApiOperation(value = "Kiểm tra có được trả hàng hay không")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root + "/isReturn/{code}"})
    public Boolean isReturn(HttpServletRequest request, @PathVariable("code") String code) {
        Boolean response = promotionProgramDiscountService.isReturn(code);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.CHECK_IS_RETURN_SUCCESS);
        return response;
    }
}
