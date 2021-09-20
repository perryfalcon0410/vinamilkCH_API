package vn.viettel.sale.controller;

import io.swagger.annotations.*;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.StringUtils;
import vn.viettel.sale.messaging.OnlineOrderFilter;
import vn.viettel.sale.service.OnlineOrderService;
import vn.viettel.sale.service.dto.OnlineOrderDTO;
import vn.viettel.sale.xml.DataSet;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

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
        OnlineOrderFilter filter = new OnlineOrderFilter(orderNumber, this.getShopId(request), synStatus, DateUtils.convertFromDate(fromDate), DateUtils.convertToDate(toDate));
        Page<OnlineOrderDTO> response = onlineOrderService.getOnlineOrders(filter, pageable);
        LogFile.logToFile(appName, getUsername(request), LogLevel.INFO, request, LogMessage.FIND_ONLINE_ORDERS_SUCCESS);
        return new Response<Page<OnlineOrderDTO>>().withData(response);
    }

    @GetMapping(value = { V1 + root + "/{id}"})
    @ApiOperation(value = "Chọn đơn online trong bán hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<OnlineOrderDTO> getOnlineOrder(HttpServletRequest request, @PathVariable Long id) {
        OnlineOrderDTO response = onlineOrderService.getOnlineOrder(id, this.getShopId(request), this.getUserId(request));
        LogFile.logToFile(appName, getUsername(request), LogLevel.INFO, request, LogMessage.GET_ONLINE_ORDER_SUCCESS);
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
        LogFile.logToFile(appName, getUsername(request), LogLevel.INFO, request, LogMessage.ONLINE_ORDER_NUMBER_SUCCESS);
        return new Response<String>().withData(response);
    }

}
