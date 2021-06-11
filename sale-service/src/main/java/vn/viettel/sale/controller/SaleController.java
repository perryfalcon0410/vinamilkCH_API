package vn.viettel.sale.controller;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.entities.SaleOrder;
import vn.viettel.sale.messaging.OrderPromotionRequest;
import vn.viettel.sale.messaging.SaleOrderRequest;
import vn.viettel.sale.messaging.SalePromotionCalculationRequest;
import vn.viettel.sale.service.ProductService;
import vn.viettel.sale.service.SalePromotionService;
import vn.viettel.sale.service.SaleService;
import vn.viettel.sale.service.dto.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@RestController
@Api(tags = "Api sử dụng cho tạo đơn bán hàng")
public class SaleController extends BaseController {
    @Autowired
    SaleService service;

    @Autowired
    ProductService productService;

    @Autowired
    SalePromotionService salePromotionService;

    private final String root = "/sales";

    @ApiOperation(value = "Api dùng để tạo mới đơn bán hàng, đơn hàng online")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 6000, message = "Khách hàng không tìm thấy"),
            @ApiResponse(code = 4007, message = "Tên đăng nhập hoặc mật khẩu không đúng"),
            @ApiResponse(code = 6014, message = "Không tìm thấy cửa hàng"),
            @ApiResponse(code = 6166, message = "Loại đơn hàng không hợp lệ"),
            @ApiResponse(code = 9010, message = "Không tìm thấy đơn online"),
            @ApiResponse(code = 9031, message = "Cửa hàng không có quyền chỉnh sửa đơn Online"),
            @ApiResponse(code = 9032, message = "Cửa hàng không có quyền tạo tay đơn Online"),
            @ApiResponse(code = 9019, message = "Sản phẩm không tồn tại"),
            @ApiResponse(code = 6174, message = "Không tìm thấy giá được áp dụng cho sản phẩm"),
            @ApiResponse(code = 6175, message = "Tạo mới thất bại"),
            @ApiResponse(code = 9015, message = "Đơn hàng đã được tạo"),
            @ApiResponse(code = 6100, message = "Số lượng mua vượt quá tồn kho")
    })
    @PostMapping(value = { V1 + root })
    public Response<HashMap> createSaleOrder(@Valid @ApiParam("Thông tin tạo mới đơn hàng") @RequestBody SaleOrderRequest request) {
        Long id = service.createSaleOrder(request, this.getUserId(), this.getRoleId(), this.getShopId());
        Response<HashMap> response = new Response<>();
        HashMap<String,Long> map = new HashMap<>();
        map.put("orderId", id);
        return response.withData(map);
    }

    @ApiOperation(value = "Api dùng để cập nhật thông tin khuyến mãi ZV19, ZV20, ZV21")
    @ApiResponse(code = 200, message = "Success")
    @PostMapping(value = { V1 + root + "/order-updatezv19"})
    public Response<List<SalePromotionDTO>> updatePromotionZV19(@Valid @ApiParam("Thông tin khuyến mãi tiền") @RequestBody List<SalePromotionDTO> allDiscountAmount) {
        List<SalePromotionDTO> list = salePromotionService.updatePromotionZV19(allDiscountAmount, this.getShopId());
        return new Response<List<SalePromotionDTO>>().withData(list);
    }

    @ApiOperation(value = "Api dùng để cập kiêm tra thông tin số xuât")
    @ApiResponse(code = 200, message = "Success")
    @PostMapping(value = { V1 + root + "/check-limit"})
    public Response<HashMap> checkPromotionLimit(@Valid @ApiParam("Thông tin khuyến mãi tiền") @RequestBody SalePromotionDTO salePromotion) {
        boolean isUsed = salePromotionService.checkPromotionLimit(salePromotion, this.getShopId());
        HashMap<String,Boolean> map = new HashMap<>();
        map.put("isUsed", isUsed);
        return new Response<HashMap>().withData(map);
    }

    @ApiOperation(value = "Api dùng để lấy danh sách khuyến mãi cho một đơn hàng")
    @ApiResponse(code = 200, message = "Success")
    @PostMapping(value = { V1 + root + "/order-promotions"})
    public Response<List<SalePromotionDTO>> getOrderPromotions(@Valid @ApiParam("Thông tin mua hàng") @RequestBody OrderPromotionRequest orderRequest) {
        if (orderRequest == null || orderRequest.getProducts() == null || orderRequest.getProducts().size() < 1){
            throw new ValidateException(ResponseMessage.ORDER_ITEM_NOT_NULL);
        }

        List<SalePromotionDTO> list = salePromotionService.getSaleItemPromotions(orderRequest, this.getShopId(), false);
        return new Response<List<SalePromotionDTO>>().withData(list);
    }

    @ApiOperation(value = "Api dùng để lấy danh sách sản phẩm cho khuyến mãi tay")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root + "/promotion-products"})
    public Response<List<FreeProductDTO>> getPromotionProduct(@Valid @ApiParam("ID chương trình khuyến mãi") @RequestParam Long promotionId,
                                                              @ApiParam("Tìm kiếm theo tên hoặc mã sản phẩm")
                                                              @RequestParam(name = "keyWord", required = false, defaultValue = "") String keyWord,
                                                              @RequestParam(name = "page", required = false) Integer page) {
        if (promotionId == null){
            throw new ValidateException(ResponseMessage.PROMOTION_DOSE_NOT_EXISTS);
        }

        if (page == null)
            page = 0;

        List<FreeProductDTO> response = productService.findFreeProductDTONoOrder(this.getShopId(), null, keyWord, page);

        return new Response<List<FreeProductDTO>>().withData(response);
    }

    @ApiOperation(value = "Api dùng để tính khuyến mãi")
    @ApiResponse(code = 200, message = "Success")
    @PostMapping(value = { V1 + root + "/promotion-calculation"})
    public Response<SalePromotionCalculationDTO> promotionCalculation(@Valid @ApiParam("Thông tin cần tính") @RequestBody SalePromotionCalculationRequest calculationRequest) {

        SalePromotionCalculationDTO result = salePromotionService.promotionCalculation(calculationRequest, this.getShopId());
        return new Response<SalePromotionCalculationDTO>().withData(result);
    }

    @ApiOperation(value = "Feign lấy giá trị sản phẩm")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root + "/price/{productId}"})
    public Response<PriceDTO> getPriceByPrID(@PathVariable Long productId) {
        return new Response<PriceDTO>().withData(productService.getProductPriceById(productId));
    }
}
