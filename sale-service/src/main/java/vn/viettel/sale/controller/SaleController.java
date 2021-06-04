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
import vn.viettel.sale.service.SalePromotionService;
import vn.viettel.sale.service.SaleService;
import vn.viettel.sale.service.dto.AutoPromotionDTO;
import vn.viettel.sale.service.dto.FreeProductDTO;
import vn.viettel.sale.service.dto.SalePromotionCalculationDTO;
import vn.viettel.sale.service.dto.SalePromotionDTO;

import javax.validation.Valid;
import java.util.List;

@RestController
@Api(tags = "Api sử dụng cho tạo đơn bán hàng")
public class SaleController extends BaseController {
    @Autowired
    SaleService service;

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
    public Response<SaleOrder> createSaleOrder(@Valid @ApiParam("Thông tin tạo mới đơn hàng") @RequestBody SaleOrderRequest request) {
        return service.createSaleOrder(request, this.getUserId(), this.getRoleId(), this.getShopId());
    }

    @ApiOperation(value = "Api dùng để lấy danh sách khuyến mãi cho một đơn hàng")
    @ApiResponse(code = 200, message = "Success")
    @PostMapping(value = { V1 + root + "/order-promotions"})
    public Response<List<SalePromotionDTO>> getOrderPromotions(@Valid @ApiParam("Thông tin mua hàng") @RequestBody OrderPromotionRequest orderRequest) {
        if (orderRequest == null || orderRequest.getProducts() == null || orderRequest.getProducts().size() < 1){
            throw new ValidateException(ResponseMessage.ORDER_ITEM_NOT_NULL);
        }

        List<SalePromotionDTO> list = salePromotionService.getSaleItemPromotions(orderRequest, this.getShopId());
        return new Response<List<SalePromotionDTO>>().withData(list);
    }

    @ApiOperation(value = "Api dùng để lấy danh sách sản phẩm cho khuyến mãi tay")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root + "/promotion-products"})
    public Response<List<FreeProductDTO>> getPromotionProduct(@Valid @ApiParam("ID chương trình khuyến mãi") @RequestParam Long promotionId) {
        if (promotionId == null){
            throw new ValidateException(ResponseMessage.PROMOTION_DOSE_NOT_EXISTS);
        }

        List<FreeProductDTO> list = salePromotionService.getPromotionProduct(promotionId, this.getShopId());
        return new Response<List<FreeProductDTO>>().withData(list);
    }

    @ApiOperation(value = "Api dùng để tính khuyến mãi")
    @ApiResponse(code = 200, message = "Success")
    @PostMapping(value = { V1 + root + "/promotion-calculation"})
    public Response<SalePromotionCalculationDTO> promotionCalculation(@Valid @ApiParam("Thông tin cần tính") @RequestBody SalePromotionCalculationRequest calculationRequest) {

        SalePromotionCalculationDTO result = salePromotionService.promotionCalculation(calculationRequest, this.getShopId());
        return new Response<SalePromotionCalculationDTO>().withData(result);
    }

    @ApiOperation(value = "Api dùng để lấy danh sách sản phẩm khuyến mãi tay v2")
    @ApiResponse(code = 200, message = "Success")
    @PostMapping(value = { V1 + root + "/promotion-free-item"})
    public Response<List<AutoPromotionDTO>> getFreeItemV2(@RequestBody OrderPromotionRequest request, @RequestParam Long customerId) {
        List<AutoPromotionDTO> list = salePromotionService.getFreeItems(request, this.getShopId(), customerId);
        return new Response<List<AutoPromotionDTO>>().withData(list);
    }

}
