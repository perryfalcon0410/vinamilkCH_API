package vn.viettel.sale.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.DateUtils;
import vn.viettel.sale.messaging.SaleOrderFilter;
import vn.viettel.sale.messaging.SaleOrderTotalResponse;
import vn.viettel.sale.service.SaleOrderService;
import vn.viettel.sale.service.dto.PrintSaleOrderDTO;
import vn.viettel.sale.service.dto.SaleOrderDTO;
import vn.viettel.sale.service.dto.SaleOrderDetailDTO;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
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
    public Response<CoverResponse<Page<SaleOrderDTO>, SaleOrderTotalResponse>> getAllSaleOrder(HttpServletRequest httpRequest, @RequestParam(value = "searchKeywords", required = false) String searchKeywords,
                                                                                               @RequestParam(value = "customerPhone", required = false) String customerPhone,
                                                                                               @RequestParam(value = "orderNumber", required = false) String orderNumber,
                                                                                               @RequestParam(value = "usedRedInvoice", required = false) Boolean usedRedInvoice,
                                                                                               @RequestParam(value = "fromDate") Date fromDate,
                                                                                               @RequestParam(value = "toDate") Date toDate,
                                                                                               @SortDefault.SortDefaults({
                                                                                                   @SortDefault(sort = "orderDate", direction = Sort.Direction.DESC),
                                                                                                   @SortDefault(sort = "orderNumber", direction = Sort.Direction.DESC)
                                                                                               })
                                                                                               Pageable pageable) {
        SaleOrderFilter filter = new SaleOrderFilter(searchKeywords, customerPhone, orderNumber, usedRedInvoice, DateUtils.convertFromDate(fromDate), DateUtils.convertToDate(toDate));
        Response<CoverResponse<Page<SaleOrderDTO>, SaleOrderTotalResponse>> response = new Response<>();
        return response.withData(saleOrderService.getAllSaleOrder(filter, pageable, this.getShopId(httpRequest)));
    }

    @GetMapping(value = { V1 + root + "/detail"})
    @ApiOperation(value = "Chi tiết hóa đơn")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")})
    public Response<SaleOrderDetailDTO> getSaleOrderDetail(@RequestParam long saleOrderId,
                                                           @RequestParam String orderNumber) {
        Response<SaleOrderDetailDTO> response = new Response<>();
        return response.withData(saleOrderService.getSaleOrderDetail(saleOrderId, orderNumber));
    }

    @GetMapping(V1 + root +"/last-sale-order/{id}")
    Response<SaleOrderDTO> getLastSaleOrderByCustomerId(@PathVariable("id") Long id){
        SaleOrderDTO saleOrderDTO = saleOrderService.getLastSaleOrderByCustomerId(id);
        return new Response<SaleOrderDTO>().withData(saleOrderDTO);
    }

    @GetMapping(V1 + root +"/print-sale-order/{id}")
    @ApiOperation(value = "In hóa đơn")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")})
    Response<PrintSaleOrderDTO> printSaleOrder(HttpServletRequest httpRequest, @PathVariable("id") Long id){
        Response<PrintSaleOrderDTO> response = new Response<>();
        return response.withData(saleOrderService.printSaleOrder(id, this.getShopId(httpRequest)));
    }

    @GetMapping(value = { V1 + root + "/total-bill-for-month"})
    @ApiOperation(value = "Tổng doanh thu trong tháng của khách hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")})
    public Response<Double> getTotalBillForTheMonthByCustomerId(@RequestParam Long customerId,
                                                                @RequestParam(value = "lastOrderDate", required = false) LocalDate lastOrderDate){
        Response<Double> response = new Response<>();
        return response.withData(saleOrderService.getTotalBillForTheMonthByCustomerId(customerId, lastOrderDate));
    }

    @GetMapping(value = { V1 + root + "/top-five-products"})
    @ApiOperation(value = "Top 5 sản phẩm yêu thích của khách hàng trong vòng 6 tháng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")})
    public Response<List<String>> getTopFiveFavoriteProducts(@RequestParam Long customerId){
        Response<List<String>> response = new Response<>();
        return response.withData(saleOrderService.getTopFiveFavoriteProducts(customerId));
    }


    public void setService(SaleOrderService service) {
        if(saleOrderService == null) saleOrderService = service;
    }
}
