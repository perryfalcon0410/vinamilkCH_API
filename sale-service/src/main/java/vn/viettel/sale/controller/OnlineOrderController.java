package vn.viettel.sale.controller;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.messaging.OnlineOrderFilter;
import vn.viettel.sale.service.OnlineOrderService;
import vn.viettel.sale.service.dto.OnlineOrderDTO;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
@Api(tags = "API sử dụng cho bán hàng đơn online")
public class OnlineOrderController extends BaseController {

    @Autowired
    OnlineOrderService onlineOrderService;
    private final String root = "/sales/online-orders";

    @GetMapping(value = { V1 + root } )
    @ApiOperation(value = "Tìm kiếm đơn online trong bán hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<Page<OnlineOrderDTO>> findOnlineOrders(HttpServletRequest request,
                                                          @ApiParam("Tìm theo số hóa đơn")
                                                          @RequestParam(value = "orderNumber", required = false) String orderNumber,
                                                          @ApiParam("Trạng thái đơn online: 0 chưa tạo đơn hàng, 1 đã tạo đơn hàng")
                                                          @RequestParam(value = "synStatus", required = false) Integer synStatus,
                                                          @RequestParam(value = "fromDate", required = false) Date fromDate,
                                                          @RequestParam(value = "toDate", required = false) Date toDate,
                                                          Pageable pageable) {
        OnlineOrderFilter filter = new OnlineOrderFilter(orderNumber, this.getShopId(), synStatus, fromDate, toDate);
        Page<OnlineOrderDTO> response = onlineOrderService.getOnlineOrders(filter, pageable);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.FIND_ONLINE_ORDERS_SUCCESS);
        return new Response<Page<OnlineOrderDTO>>().withData(response);
    }

    @GetMapping(value = { V1 + root + "/{id}"})
    @ApiOperation(value = "Chọn đơn online trong bán hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<OnlineOrderDTO> getOnlineOrder(HttpServletRequest request, @PathVariable Long id) {
        OnlineOrderDTO response = onlineOrderService.getOnlineOrder(id, this.getShopId(), this.getUserId());
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_ONLINE_ORDER_SUCCESS);
        return new Response<OnlineOrderDTO>().withData(response);
    }

    @GetMapping(value = { V1 + root + "/order-number/{code}"})
    @ApiOperation(value = "Kiểm tra trùng mã đơn online tạo tay")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<String> checkOnlineNumber(HttpServletRequest request, @PathVariable String code) {
        String response = onlineOrderService.checkOnlineNumber(code);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.ONLINE_ORDER_NUMBER_SUCCESS);
        return new Response<String>().withData(response);
    }

}
