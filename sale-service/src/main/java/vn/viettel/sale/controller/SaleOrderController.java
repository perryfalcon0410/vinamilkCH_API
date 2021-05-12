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
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.sale.messaging.OnlineOrderFilter;
import vn.viettel.sale.messaging.SaleOrderFilter;
import vn.viettel.sale.messaging.SaleOrderTotalResponse;
import vn.viettel.sale.service.SaleOrderService;
import vn.viettel.sale.service.dto.*;

import java.util.Date;
import java.util.List;

@RestController
@Api(tags = "API sử dụng cho quản lý hóa đơn")
public class SaleOrderController extends BaseController {
    @Autowired
    SaleOrderService saleOrderService;
    private final String root = "/sales/sale-orders";

    @GetMapping(value = { V1 + root })
    @ApiOperation(value = "Danh sách hóa đơn, tìm kiếm trong danh sách")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")})
    public Response<CoverResponse<Page<SaleOrderDTO>, SaleOrderTotalResponse>> getAllSaleOrder(@RequestParam(value = "searchKeywords", required = false) String searchKeywords,
                                                                                               @RequestParam(value = "orderNumber", required = false) String orderNumber,
                                                                                               @RequestParam(value = "usedRedInvoice", required = false) Integer usedRedInvoice,
                                                                                               @RequestParam(value = "fromDate", required = false) Date fromDate,
                                                                                               @RequestParam(value = "toDate", required = false) Date toDate,Pageable pageable) {
        SaleOrderFilter filter = new SaleOrderFilter(searchKeywords, orderNumber, usedRedInvoice, fromDate, toDate);
        return saleOrderService.getAllSaleOrder(filter, pageable);
    }

    @GetMapping(value = { V1 + root + "/detail"})
    @ApiOperation(value = "Chi tiết hóa đơn")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")})
    public Response<SaleOrderDetailDTO> getSaleOrderDetail(@RequestParam long saleOrderId,
                                                           @RequestParam String orderNumber) {
        return saleOrderService.getSaleOrderDetail(saleOrderId, orderNumber);
    }

    @GetMapping(V1 + root +"/last-sale-order/{id}")
    Response<SaleOrderDTO> GetLastSaleOrderByCustomerId(@PathVariable("id") Long id){
        return saleOrderService.getLastSaleOrderByCustomerId(id);
    }

    @GetMapping(V1 + root +"/print-sale-order/{id}")
    @ApiOperation(value = "In hóa đơn")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")})
    Response<PrintSaleOrderDTO> printSaleOrder(@PathVariable("id") Long id){
        return saleOrderService.printSaleOrder(id, this.getShopId());
    }
}
