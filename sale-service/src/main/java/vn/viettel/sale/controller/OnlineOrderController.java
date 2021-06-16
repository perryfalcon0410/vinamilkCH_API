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
        OnlineOrderFilter filter = new OnlineOrderFilter(orderNumber, this.getShopId(), synStatus, DateUtils.convertFromDate(fromDate), DateUtils.convertToDate(toDate));
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

    @ApiOperation(value = "Api dùng để đọc file xml và đồng bộ lên db của đơn online")
    @ApiResponse(code = 200, message = "Success")
    @PostMapping(value = { V1 + root + "/xml"})
    public Response<DataSet> syncXmlOnlineOrder(HttpServletRequest httpRequest,
                                                @RequestParam(name = "file") MultipartFile file
    ) throws IOException {
        DataSet dataSet = onlineOrderService.syncXmlOnlineOrder(file);
        Response<DataSet> response = new Response<>();
        response.setStatusValue("Đọc file thành công");
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.SYNCHRONIZATION_XML_PO_SUCCESS);
        return response.withData(dataSet);
    }

    @ApiOperation(value = "Api dùng để đọc file xml và hủy đơn của đơn online")
    @ApiResponse(code = 200, message = "Success")
    @PostMapping(value = { V1 + root + "/xml/cancel-online-order"})
    public Response<DataSet> syncXmlToCancelOnlineOrder(HttpServletRequest httpRequest,
                                                @RequestParam(name = "file") MultipartFile file
    ) throws IOException {
        DataSet dataSet = onlineOrderService.syncXmlToCancelOnlineOrder(file);
        Response<DataSet> response = new Response<>();
        response.setStatusValue("Đã hủy "+dataSet.getStt()+" đơn online");
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.SYNCHRONIZATION_XML_PO_SUCCESS);
        return response.withData(dataSet);
    }

    @ApiOperation(value = "Xuất file xml đơn online")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(V1 + root + "/xml/export")
    public ResponseEntity exportToXml(HttpServletRequest httpRequest,
                                        @ApiParam(value = "Danh sách id đơn bán hàng bằng chọn đơn online ")
                                        @RequestParam(value = "ids") List<Long> ids) throws IOException {
        String xml = onlineOrderService.exportXmlFile(ids);
        ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        String fileName = "ORDERPOS_"+ StringUtils.createXmlFileName();
        headers.add("Content-Disposition", "attachment; filename=" + fileName);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.EXPORT_XML_ONLINE_ORDER_SUCCESS);
        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
    }



}
