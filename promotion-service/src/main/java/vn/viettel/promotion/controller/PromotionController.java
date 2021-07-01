package vn.viettel.promotion.controller;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.promotion.*;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.PromotionProductRequest;
import vn.viettel.core.messaging.RPT_ZV23Request;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleFeign;
import vn.viettel.promotion.service.PromotionCustAttrService;
import vn.viettel.promotion.service.PromotionItemProductService;
import vn.viettel.promotion.service.PromotionProgramService;
import vn.viettel.promotion.service.RPT_ZV23Service;
import vn.viettel.promotion.service.dto.TotalPriceZV23DTO;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;
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


    @RoleFeign
    @GetMapping(value = { V1 + root + "/promotion-program-discount/{orderNumber}"})
    Response<List<PromotionProgramDiscountDTO>> listPromotionProgramDiscountByOrderNumber(HttpServletRequest request, @PathVariable String orderNumber) {
        List<PromotionProgramDiscountDTO> response = promotionProgramService.listPromotionProgramDiscountByOrderNumber(orderNumber);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_LIST_PROMOTION_PROGRAM_DISCOUNT_SUCCESS);
        return new Response<List<PromotionProgramDiscountDTO>>().withData(response);
    }

    @RoleFeign
    @ApiOperation(value = "Api dùng khi tạo đơn bán hàng để lấy thông tin chương trình khuyến mãi theo id")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root + "/{id}"})
    Response<PromotionProgramDTO> getById(HttpServletRequest request, @PathVariable Long id) {
        PromotionProgramDTO response = promotionProgramService.getPromotionProgramById(id);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_PROMOTION_PROGRAM_BY_ID_SUCCESS);
        return new Response<PromotionProgramDTO>().withData(response);
    }

    @RoleFeign
    @ApiOperation(value = "Api dùng khi tạo đơn bán hàng để lấy thông tin chương trình khuyến mãi theo code")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root})
    Response<PromotionProgramDTO> getByCode(HttpServletRequest request, @RequestParam String code) {
        PromotionProgramDTO response = promotionProgramService.getPromotionProgramByCode(code);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_PROMOTION_PROGRAM_BY_CODE_SUCCESS);
        return new Response<PromotionProgramDTO>().withData(response);
    }

    @RoleFeign
    @ApiOperation(value = "Api dùng khi tạo đơn bán hàng để lấy danh sách loại khách hàng được hưởng chương trình khuyến mãi")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root + "/available-promotion-cus-attr/{shopId}"})
    public Response<List<PromotionCustATTRDTO>> getGroupCustomerMatchProgram(HttpServletRequest request, @PathVariable Long shopId) {
        List<PromotionCustATTRDTO> reponse = promotionProgramService.getGroupCustomerMatchProgram(shopId);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_GROUP_CUSTOMER_MATCH_PROGRAM_SUCCESS);
        return new Response<List<PromotionCustATTRDTO>>().withData(reponse);
    }

    @RoleFeign
    @ApiOperation(value = "Api dùng khi tạo đơn bán hàng để lấy thông tin chi tiết chương trình khuyến mãi")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root + "/get-promotion-detail/{shopId}"})
    public Response<List<PromotionProgramDetailDTO>> getPromotionDetailByPromotionId(HttpServletRequest request, @PathVariable Long shopId) {
        List<PromotionProgramDetailDTO> response = promotionProgramService.getPromotionDetailByPromotionId(shopId);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_PROMOTION_DETAIL_BY_PROMOTION_ID_SUCCESS);
        return new Response<List<PromotionProgramDetailDTO>>().withData(response);
    }

    @RoleFeign
    @ApiOperation(value = "Api dùng khi tạo đơn bán hàng để lấy danh sách sản phẩm loại trừ")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root + "/getzv23products"})
    public Response<List<PromotionProgramProductDTO>> findByPromotionIds(HttpServletRequest request, @RequestParam List<Long> promotionIds) {
        List<PromotionProgramProductDTO> response = promotionProgramService.findByPromotionIds(promotionIds);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_REJECTED_PRODUCT_SUCCESS);
        return new Response<List<PromotionProgramProductDTO>>().withData(response);
    }

    @RoleFeign
    @ApiOperation(value = "Api lấy thông tin chương trình khuyến mãi được áp dụng tại cửa hàng")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root + "/get-promotion-shop-map"})
    public Response<PromotionShopMapDTO> getPromotionShopMap(HttpServletRequest request, @RequestParam Long promotionProgramId,
                                                          @RequestParam Long shopId) {
        PromotionShopMapDTO response = promotionProgramService.getPromotionShopMap(promotionProgramId, shopId);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_PROMOTION_SHOP_MAP_SUCCESS);
        return new Response<PromotionShopMapDTO>().withData(response);
    }

    @RoleFeign
    @ApiOperation(value = "Api dùng khi tạo đơn bán hàng để cập nhập thông tin chương trình khuyến được áp dụng tại cửa hàng")
    @ApiResponse(code = 200, message = "Success")
    @PutMapping(value = { V1 + root + "/promotion-shop-map"})
    public Response<PromotionShopMapDTO> updatePromotionShopMap(@Valid @RequestBody PromotionShopMapDTO shopmap) {
        PromotionShopMapDTO dto = promotionProgramService.updatePromotionShopMap(shopmap);
        return new Response<PromotionShopMapDTO>().withData(dto);
    }

    @RoleFeign
    @ApiOperation(value = "Api dùng khi tạo đơn bán hàng để lấy thông tin khuyến mãi tay của sản phẩm")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root + "/get-zm-promotion"})
    public Response<List<PromotionSaleProductDTO>> getZmPromotion(HttpServletRequest request, @RequestParam Long productId) {
        List<PromotionSaleProductDTO> response = promotionProgramService.getZmPromotionByProductId(productId);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_ZM_PROMOTION_SUCCESS);
        return new Response<List<PromotionSaleProductDTO>>().withData(response);
    }

    @RoleFeign
    @ApiOperation(value = "Api dùng khi tạo đơn bán hàng để lấy danh sách sản phẩm khuyến mãi")
    @ApiResponse(code = 200, message = "Success")
    @PostMapping(value = { V1 + root + "/get-free-items/{programId}"})
    public Response<List<PromotionProductOpenDTO>> getFreeItem(HttpServletRequest request, @PathVariable Long programId) {
        List<PromotionProductOpenDTO> response = promotionProgramService.getFreeItems(programId);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_FREE_ITEMS_SUCCESS);
        return new Response<List<PromotionProductOpenDTO>>().withData(response);
    }

    @RoleFeign
    @ApiOperation(value = "Api dùng khi tạo đơn bán hàng để lấy số tiền/phần trăm được giảm")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root + "/get-promotion-discount"})
    public Response<List<PromotionProgramDiscountDTO>> getPromotionDiscounts(HttpServletRequest request, @RequestParam List<Long> ids, @RequestParam String cusCode) {
        List<PromotionProgramDiscountDTO> response = promotionProgramService.getPromotionDiscounts(ids, cusCode);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_PROMOTION_PROGRAM_DISCOUNT_SUCCESS);
        return new Response<List<PromotionProgramDiscountDTO>>().withData(response);
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

    @RoleFeign
    @PutMapping(value = { V1 + root + "/promotion-program-discount"})
    public Response<PromotionProgramDiscountDTO> updatePromotionProgramDiscount(@Valid @RequestBody PromotionProgramDiscountDTO discount) {
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
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.CHECK_IS_RETURN_SUCCESS);
        return response;
    }

    @GetMapping(value = {V1 + root + "/discount-percent"})
    public Double getDiscountPercent(@RequestParam String type, @RequestParam String code, @RequestParam Double amount) {
        return promotionProgramService.getDiscountPercent(type, code, amount);
    }

    @GetMapping(value = {V1 + root + "/buying-condition"})
    public Long checkBuyingCondition(@RequestParam String type, @RequestParam Integer quantity, @RequestParam Double amount, @RequestParam List<Long> ids) {
        return promotionProgramService.checkBuyingCondition(type, quantity, amount, ids);
    }

    @GetMapping(value = {V1 + root + "/required-products"})
    List<Long> getRequiredProducts(@RequestParam String type) {
        return promotionProgramService.getRequiredProducts(type);
    }

  //  @RoleFeign
    @GetMapping(value = {V1 + root + "/promotion-programs/shop/{id}"})
    Response<List<PromotionProgramDTO>> findPromotionPrograms(@PathVariable Long id) {
        List<PromotionProgramDTO> list = promotionProgramService.findPromotionPrograms(id);
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

    @RoleFeign
    @GetMapping(value = { V1 + root + "/promotion-program-detail/{programId}"})
    @ApiOperation(value = "Tìm thuộc tính khách hàng tham gia chương trình")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<List<PromotionProgramDetailDTO>> findPromotionProgramDetail(@PathVariable Long programId) {
        List<PromotionProgramDetailDTO> response = promotionProgramService.findPromotionDetailByProgramId(programId);
        return new Response<List<PromotionProgramDetailDTO>>().withData(response);
    }

    @RoleFeign
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

    @RoleFeign
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
            @RequestParam Long promotionId,
            @RequestParam Long customerId,
            @RequestParam Long shopId) {
        RPT_ZV23DTO dto = rpt_zv23Service.checkSaleOrderZV23(promotionId, customerId, shopId);
        return new Response<RPT_ZV23DTO>().withData(dto);
    }

    @GetMapping(value = { V1 + root + "/RPT-ZV23/totalVATorNotZV23"})
    @ApiOperation(value = "tính doanh số theo thuế hoặc không")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<TotalPriceZV23DTO> totalVATorNotZV23(
            @RequestParam Long promotionId,
            @RequestParam Integer quantity ) {
        TotalPriceZV23DTO totalPriceZV23DTO = rpt_zv23Service.VATorNotZV23(promotionId, quantity);
        return new Response<TotalPriceZV23DTO>().withData(totalPriceZV23DTO);
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
}
