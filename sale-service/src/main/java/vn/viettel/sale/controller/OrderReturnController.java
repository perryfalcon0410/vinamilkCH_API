package vn.viettel.sale.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.sale.entities.SaleOrder;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.messaging.*;
import vn.viettel.sale.service.OrderReturnService;
import vn.viettel.sale.service.dto.OrderReturnDTO;
import vn.viettel.sale.service.dto.OrderReturnDetailDTO;
import vn.viettel.sale.service.dto.SaleOrderDTO;

import java.util.Date;
import java.util.List;

@RestController
@Api(tags = "API sử dụng cho quản lý hóa đơn trả lại")
public class OrderReturnController extends BaseController {
    @Autowired
    OrderReturnService orderReturnService;
    private final String root = "/sales/order-return";

    @GetMapping(value = { V1 + root })
    @ApiOperation(value = "Danh sách hóa đơn trả lại, tìm kiếm trong danh sách")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")})
    public Response<CoverResponse<Page<OrderReturnDTO>, SaleOrderTotalResponse>> getAllOrderReturn(  @RequestParam(value = "searchKeywords", required = false) String searchKeywords,
                                                                                                     @RequestParam(value = "returnNumber", required = false) String orderNumber,
                                                                                                     @RequestParam(value = "fromDate", required = false) Date fromDate,
                                                                                                     @RequestParam(value = "toDate", required = false) Date toDate,Pageable pageable) {
        SaleOrderFilter filter = new SaleOrderFilter(searchKeywords, orderNumber, null, fromDate, toDate);
        Response<CoverResponse<Page<OrderReturnDTO>, SaleOrderTotalResponse>> response = new Response<>();
        return response.withData(orderReturnService.getAllOrderReturn(filter, pageable, this.getShopId()));
    }

    @GetMapping(value = { V1 + root + "/detail/{id}"})
    @ApiOperation(value = "Chi tiết hóa đơn trả lại")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")})
    public Response<OrderReturnDetailDTO> getOrderReturnDetail(@PathVariable long id) {
        Response<OrderReturnDetailDTO> response = new Response<>();
        return response.withData(orderReturnService.getOrderReturnDetail(id));
    }

    @GetMapping(value = { V1 + root + "/choose"})
    @ApiOperation(value = "Danh sách chọn hóa đơn để trả, tìm kiếm trong danh sách")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")})
    public Response<CoverResponse<List<SaleOrderDTO>,TotalOrderChoose>> selectForReturn(@RequestParam(value = "orderNumber", required = false, defaultValue = "") String orderNumber,
                                                                                        @RequestParam(value = "searchKeywords", required = false, defaultValue = "") String searchKeywords,
                                                                                        @RequestParam(value = "product", required = false, defaultValue = "") String product,
                                                                                        @RequestParam(value = "fromDate", required = false) Date fromDate,
                                                                                        @RequestParam(value = "toDate", required = false) Date toDate, Pageable pageable) {
        SaleOrderChosenFilter filter = new SaleOrderChosenFilter(orderNumber, searchKeywords, product, fromDate, toDate);
        Response<CoverResponse<List<SaleOrderDTO>,TotalOrderChoose>> response = new Response<>();
        return response.withData(orderReturnService.getSaleOrderForReturn(filter, pageable, this.getShopId()));
    }

    @GetMapping(value = { V1 + root + "/chosen/{id}"})
    @ApiOperation(value = "Hóa đơn đã chọn để trả")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")})
    public Response<OrderReturnDetailDTO> orderSelected(@PathVariable long id) {
        Response<OrderReturnDetailDTO> response = new Response<>();
        return response.withData(orderReturnService.getSaleOrderChosen(id));
    }

    @PostMapping(value = { V1 + root })
    @ApiOperation(value = "Tạo hóa đơn trả lại")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")})
    public Response<SaleOrder> createOrderReturn(@RequestBody OrderReturnRequest request) {
        Response<SaleOrder> response = new Response<>();
        return response.withData(orderReturnService.createOrderReturn(request, this.getShopId(), this.getUserName()));
    }
}
