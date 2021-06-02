package vn.viettel.sale.controller;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.entities.SaleOrder;
import vn.viettel.sale.messaging.ProductOrderRequest;
import vn.viettel.sale.messaging.PromotionProductRequest;
import vn.viettel.sale.messaging.SaleOrderRequest;
import vn.viettel.sale.service.SalePromotionService;
import vn.viettel.sale.service.SaleService;
import vn.viettel.sale.service.dto.ZmFreeItemDTO;

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

    @ApiOperation(value = "Api dùng để lấy danh sách sản phẩm khuyến mãi tay")
    @ApiResponse(code = 200, message = "Success")
    @PostMapping(value = { V1 + root + "/promotion-free-item"})
    public Response<List<ZmFreeItemDTO>> getFreeItems(@RequestBody List<ProductOrderRequest> productList, @RequestParam Long customerId) {
        return service.getFreeItems(productList, this.getShopId(), customerId);
    }

    @ApiOperation(value = "Api dùng để lấy danh sách sản phẩm khuyến mãi tay v2")
    @ApiResponse(code = 200, message = "Success")
    @PostMapping(value = { V1 + root + "/promotion-free-item-v2"})
    public Response<List<ZmFreeItemDTO>> getFreeItemV2(@RequestBody PromotionProductRequest request, @RequestParam Long customerId) {
        List<ZmFreeItemDTO> list = salePromotionService.getFreeItems(request, this.getShopId(), customerId);
        return new Response<List<ZmFreeItemDTO>>().withData(list);
    }

}
