package vn.viettel.promotion.controller;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.promotion.*;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.RPT_ZV23Request;
import vn.viettel.core.messaging.Response;
import vn.viettel.promotion.service.PromotionCustAttrService;
import vn.viettel.promotion.service.PromotionItemProductService;
import vn.viettel.promotion.service.PromotionProgramService;
import vn.viettel.promotion.service.RPT_ZV23Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@Api(tags = "Api sử dụng để lấy khuyến mãi của sản phẩm")
public class PromotionController extends BaseController {
    private final String root = "/promotions";

    @Autowired
    PromotionProgramService promotionProgramService;

    @Autowired
    PromotionCustAttrService promotionCustAttrService;

    @Autowired
    RPT_ZV23Service rpt_zv23Service;

    @Autowired
    PromotionItemProductService itemProductService;


    @GetMapping(value = { V1 + root + "/promotion-program-discount/{orderNumber}"})
    Response<List<PromotionProgramDiscountDTO>> listPromotionProgramDiscountByOrderNumber(HttpServletRequest request, @PathVariable String orderNumber) {
        List<PromotionProgramDiscountDTO> response = promotionProgramService.listPromotionProgramDiscountByOrderNumber(orderNumber);
        LogFile.logToFile(appName, getUsername(request), LogLevel.INFO, request, LogMessage.GET_LIST_PROMOTION_PROGRAM_DISCOUNT_SUCCESS);
        return new Response<List<PromotionProgramDiscountDTO>>().withData(response);
    }


    @ApiOperation(value = "Api dùng khi tạo đơn bán hàng để lấy thông tin chương trình khuyến mãi theo id")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root + "/{id}"})
    Response<PromotionProgramDTO> getById(HttpServletRequest request, @PathVariable Long id) {
        PromotionProgramDTO response = promotionProgramService.getPromotionProgramById(id);
        LogFile.logToFile(appName, getUsername(request), LogLevel.INFO, request, LogMessage.GET_PROMOTION_PROGRAM_BY_ID_SUCCESS);
        return new Response<PromotionProgramDTO>().withData(response);
    }

    @ApiOperation(value = "Api dùng khi tạo đơn bán hàng để lấy thông tin chương trình khuyến mãi theo id")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root + "/ids"})
    Response<List<PromotionProgramDTO>> getByIds(HttpServletRequest request, @RequestParam List<Long> programIds) {
        List<PromotionProgramDTO> response = promotionProgramService.findByIds(programIds);
        LogFile.logToFile(appName, getUsername(request), LogLevel.INFO, request, LogMessage.GET_PROMOTION_PROGRAM_BY_ID_SUCCESS);
        return new Response<List<PromotionProgramDTO>>().withData(response);
    }


    @ApiOperation(value = "Api dùng khi tạo đơn bán hàng để lấy danh sách sản phẩm loại trừ")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root + "/getzv23products"})
    public Response<List<PromotionProgramProductDTO>> findByPromotionIds(HttpServletRequest request, @RequestParam List<Long> promotionIds) {
        List<PromotionProgramProductDTO> response = promotionProgramService.findByPromotionIds(promotionIds);
        LogFile.logToFile(appName, getUsername(request), LogLevel.INFO, request, LogMessage.GET_REJECTED_PRODUCT_SUCCESS);
        return new Response<List<PromotionProgramProductDTO>>().withData(response);
    }

    @ApiOperation(value = "Api lấy thông tin chương trình khuyến mãi được áp dụng tại cửa hàng")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root + "/get-promotion-shop-map"})
    public Response<PromotionShopMapDTO> getPromotionShopMap(HttpServletRequest request, @RequestParam Long promotionProgramId,
                                                          @RequestParam Long shopId) {
        PromotionShopMapDTO response = promotionProgramService.getPromotionShopMap(promotionProgramId, shopId);
        LogFile.logToFile(appName, getUsername(request), LogLevel.INFO, request, LogMessage.GET_PROMOTION_SHOP_MAP_SUCCESS);
        return new Response<PromotionShopMapDTO>().withData(response);
    }

    @ApiOperation(value = "Api dùng khi tạo đơn bán hàng để cập nhập thông tin chương trình khuyến được áp dụng tại cửa hàng")
    @ApiResponse(code = 200, message = "Success")
    @PutMapping(value = { V1 + root + "/promotion-shop-map"})
    public Response<PromotionShopMapDTO> updatePromotionShopMap(@RequestBody PromotionShopMapDTO shopmap) {
        PromotionShopMapDTO dto = promotionProgramService.updatePromotionShopMap(shopmap);
        return new Response<PromotionShopMapDTO>().withData(dto);
    }

    @ApiOperation(value = "Api dùng khi tạo đơn bán hàng để lấy danh sách sản phẩm khuyến mãi")
    @ApiResponse(code = 200, message = "Success")
    @PostMapping(value = { V1 + root + "/get-free-items/{programId}"})
    public Response<List<PromotionProductOpenDTO>> getFreeItem(HttpServletRequest request, @PathVariable Long programId) {
        List<PromotionProductOpenDTO> response = promotionProgramService.getFreeItems(programId);
        LogFile.logToFile(appName, getUsername(request), LogLevel.INFO, request, LogMessage.GET_FREE_ITEMS_SUCCESS);
        return new Response<List<PromotionProductOpenDTO>>().withData(response);
    }

    @ApiOperation(value = "Api lấy khuyến mãi theo mã giảm giá")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root + "/promotion-program-discount/code/{code}"})
    public Response<PromotionProgramDiscountDTO> getPromotionDiscount(@ApiParam("Mã giảm giá") @PathVariable("code") String discountCode, @RequestParam Long shopId) {
        PromotionProgramDiscountDTO response = promotionProgramService.getPromotionDiscount(discountCode, shopId);
        return new Response<PromotionProgramDiscountDTO>().withData(response);
    }


    @PutMapping(value = { V1 + root + "/promotion-program-discount"})
    public Response<PromotionProgramDiscountDTO> updatePromotionProgramDiscount(@RequestBody PromotionProgramDiscountDTO discount) {
        PromotionProgramDiscountDTO response = promotionProgramService.updatePromotionProgramDiscount(discount);
        return new Response<PromotionProgramDiscountDTO>().withData(response);
    }


    @ApiOperation(value = "Kiểm tra có được trả hàng hay không")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root + "/isReturn"})
    public Boolean isReturn(HttpServletRequest request, @RequestParam(value = "code", required = false) String code) {
        Boolean response = promotionProgramService.isReturn(code);
        LogFile.logToFile(appName, getUsername(request), LogLevel.INFO, request, LogMessage.CHECK_IS_RETURN_SUCCESS);
        return response;
    }

    @GetMapping(value = {V1 + root + "/promotion-programs/shop/{id}"})
    Response<List<PromotionProgramDTO>> findPromotionPrograms(@PathVariable Long id,@RequestParam Long orderType,@RequestParam Long customerTypeId,
                                                              @RequestParam(required = false) Long memberCard,@RequestParam(required = false) Long cusCloselyTypeId,@RequestParam(required = false) Long cusCardTypeId) {
        List<PromotionProgramDTO> list = promotionProgramService.findPromotionPrograms(id, orderType, customerTypeId, memberCard, cusCloselyTypeId
                , cusCardTypeId);
        return new Response<List<PromotionProgramDTO>>().withData(list);
    }

    @GetMapping(value = { V1 + root + "/promotion-cust-attr/{programId}"})
    @ApiOperation(value = "Tìm thuộc tính khách hàng tham gia chương trình")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<Set<Long>> findCusCardPromotion(@PathVariable Long programId,@RequestParam  Integer objectType) {
        Set<Long> cusCard = promotionCustAttrService.getListCusCard(programId, objectType);
        return new Response<Set<Long>>().withData(cusCard);
    }

    @GetMapping(value = { V1 + root + "/promotion-program-detail/{programId}"})
    @ApiOperation(value = "Tìm thuộc tính khách hàng tham gia chương trình")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<List<PromotionProgramDetailDTO>> findPromotionProgramDetail(@PathVariable Long programId, @RequestParam(required = false) List<Long> productIds) {
        List<PromotionProgramDetailDTO> response = promotionProgramService.findPromotionDetailByProgramId(programId, productIds);
        return new Response<List<PromotionProgramDetailDTO>>().withData(response);
    }

    @GetMapping(value = { V1 + root + "/promotion-discount/{programId}"})
    @ApiOperation(value = "Lấy sản phảm KM tay")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<List<PromotionProgramDiscountDTO>> findPromotionDiscountByPromotion(@PathVariable Long programId) {
        List<PromotionProgramDiscountDTO> response = promotionProgramService.findPromotionDiscountByPromotion(programId);
        return new Response<List<PromotionProgramDiscountDTO>>().withData(response);
    }

    @GetMapping(value = { V1 + root + "/RPT-ZV23/promotion-checkZV23"})
    @ApiOperation(value = "Lấy sản phảm KM tay")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<RPT_ZV23DTO> checkZV23Require(
            @RequestParam String promotionCode,
            @RequestParam Long customerId,
            @RequestParam Long shopId) {
        RPT_ZV23DTO dto = rpt_zv23Service.checkSaleOrderZV23(promotionCode, customerId, shopId);
        return new Response<RPT_ZV23DTO>().withData(dto);
    }

    @GetMapping(value = { V1 + root + "/RPT-ZV23"})
    @ApiOperation(value = "Lấy Danh sách  rpt zv23")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<List<RPT_ZV23DTO>> findByProgramIds(HttpServletRequest httpRequest, @RequestParam Set<Long> programIds, @RequestParam Long customerId) {
        List<RPT_ZV23DTO> response = rpt_zv23Service.findByProgramIds(programIds, customerId, this.getShopId(httpRequest));
        return new Response<List<RPT_ZV23DTO>>().withData(response);
    }

    @PutMapping(value = { V1 + root + "/RPT-ZV23/{id}"})
    @ApiOperation(value = "Cập nhật thông tin bảng rpt-zv23 trong bán hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<Boolean> updateRPTZV23(@PathVariable Long id, @RequestBody RPT_ZV23Request request) {
        Boolean result = rpt_zv23Service.updateRPT_ZV23(id, request);
        return new Response<Boolean>().withData(result);
    }

    @PostMapping(value = { V1 + root + "/promotion-item-product/not-accumlated"})
    @ApiOperation(value = "danh sách sản phẩm không tích lũy")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<List<Long>> getProductsNotAccumulated(@RequestBody List<Long> productIds ) {
        List<Long> listNotAccumulated = itemProductService.listProductsNotAccumulated(productIds);
        return new Response<List<Long>>().withData(listNotAccumulated);
    }

    @PutMapping(value = { V1 + root + "/create/RPT-ZV23"})
    @ApiOperation(value = "Tạo mới rpt-zv23 trong bán hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<Boolean> createRPTZV23(@RequestBody RPT_ZV23Request request) {
        Boolean result = rpt_zv23Service.createRPT_ZV23(request);
        return new Response<Boolean>().withData(result);
    }


    @GetMapping(value = { V1 + root + "/promotion-sale-product/{programId}"})
    @ApiOperation(value = "Lấy sản phảm KM tay")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<List<PromotionSaleProductDTO>> findPromotionSaleProductByProgramId(@PathVariable Long programId) {
        List<PromotionSaleProductDTO> response = promotionProgramService.findPromotionSaleProductByProgramId(programId);
        return new Response<List<PromotionSaleProductDTO>>().withData(response);
    }

    @GetMapping(value = { V1 + root + "/check/promotion-sale-product/{programId}"})
    @ApiOperation(value = "Lấy sản phảm KM tay")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<Boolean> checkSaleProductByProgramId(@PathVariable Long programId, @RequestParam(required = false) List<Long> productIds) {
        Boolean response = promotionProgramService.checkPromotionSaleProduct(programId, productIds);
        return new Response<Boolean>().withData(response);
    }

    @ApiOperation(value = "Update MGG cho đơn trả")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @PutMapping(value = { V1 + root + "/mgg/return"})
    public Response<List<Long>> returnMGG(HttpServletRequest httpRequest, @RequestParam String orderNumber) {
    	List<Long> result = promotionProgramService.returnMGG(orderNumber, this.getShopId(httpRequest));
        return new Response<List<Long>>().withData(result);
    }

    @ApiOperation(value = "Update Shop Map cho đơn trả")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @PutMapping(value = { V1 + root + "/promotion-shop-map/return"})
    public Response<Boolean> returnPromotionShopmap(HttpServletRequest httpRequest, @RequestBody Map<String, Double> shopMaps) {
        Boolean result = promotionProgramService.returnPromotionShopmap(shopMaps, this.getShopId(httpRequest));
        return new Response<Boolean>().withData(result);
    }


}
