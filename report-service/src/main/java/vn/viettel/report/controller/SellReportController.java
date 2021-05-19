package vn.viettel.report.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.report.messaging.SellsReportsFilter;
import vn.viettel.report.service.SellsReportService;
import vn.viettel.report.service.dto.*;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;


@RestController
@Api(tags = "API báo cáo bán hàng")
public class SellReportController extends BaseController {
    private final String root = "/reports/sells";

    @Autowired
    SellsReportService sellsReportService;

    @RoleAdmin
    @GetMapping(V1 + root)
    @ApiOperation(value = "Danh sách dữ liệu báo cáo bán hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<CoverResponse<Page<SellDTO>, SellTotalDTO>> getReportSells(
            HttpServletRequest request,
            @RequestParam(value = "orderNumber", required = false) String orderNumber,
            @RequestParam(value = "fromDate", required = false) Date fromDate,
            @RequestParam(value = "toDate", required = false) Date toDate,
            @RequestParam(value = "productKW", required = false) String productKW,
            @RequestParam(value = "collecter", required = false) Integer collecter,
            @RequestParam(value = "salesChannel", required = false) Integer salesChannel,
            @RequestParam(value = "customerKW", required = false) String customerKW,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(value = "fromInvoiceSales", required = false) Float fromInvoiceSales,
            @RequestParam(value = "toInvoiceSales", required = false) Float toInvoiceSales,
            Pageable pageable) {
        SellsReportsFilter filter = new SellsReportsFilter(this.getShopId(), orderNumber, fromDate, toDate, productKW, collecter,salesChannel,customerKW,phoneNumber,fromInvoiceSales,toInvoiceSales);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.LOGIN_SUCCESS);
        return sellsReportService.getSellReport(filter, pageable);
    }

    @RoleAdmin
    @GetMapping(V1 + root + "/export")
    @ApiOperation(value = "Xuất excel dữ liệu báo cáo bán hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public ResponseEntity exportToExcel(
            HttpServletRequest request,
            @RequestParam(value = "orderNumber", required = false) String orderNumber,
            @RequestParam(value = "fromDate", required = false) Date fromDate,
            @RequestParam(value = "toDate", required = false) Date toDate,
            @RequestParam(value = "productKW", required = false) String productKW,
            @RequestParam(value = "collecter", required = false) Integer collecter,
            @RequestParam(value = "salesChannel", required = false) Integer salesChannel,
            @RequestParam(value = "customerKW", required = false) String customerKW,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(value = "fromInvoiceSales", required = false) Float fromInvoiceSales,
            @RequestParam(value = "toInvoiceSales", required = false) Float toInvoiceSales) throws IOException {

        SellsReportsFilter filter = new SellsReportsFilter(this.getShopId(), orderNumber, fromDate, toDate, productKW, collecter,salesChannel,customerKW,phoneNumber,fromInvoiceSales,toInvoiceSales);
        ByteArrayInputStream in = sellsReportService.exportExcel(filter);
        HttpHeaders headers = new HttpHeaders();
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        headers.add("Content-Disposition", "attachment; filename=Bao_Cao_Ban_Hang_Filled_" + date + ".xlsx");
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.LOGIN_SUCCESS);
        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
    }

//    @RoleAdmin
//    @GetMapping(V1 + root + "/print")
//    @ApiOperation(value = "In dữ liệu báo cáo hàng trả lại")
//    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
//            @ApiResponse(code = 400, message = "Bad request"),
//            @ApiResponse(code = 500, message = "Internal server error")}
//    )
//    public Response<CoverResponse<List<ReturnGoodsReportDTO>, ReportTotalDTO>> getDataPrint(
//            HttpServletRequest request,
//            @RequestParam(value = "reciept", required = false) String reciept,
//            @RequestParam(value = "fromDate", required = false) Date fromDate,
//            @RequestParam(value = "toDate", required = false) Date toDate,
//            @RequestParam(value = "reason", required = false) String reason,
//            @RequestParam(value = "productKW", required = false) String productKW) {
//        ReturnGoodsReportsFilter filter = new ReturnGoodsReportsFilter(this.getShopId(), reciept, fromDate, toDate, reason, productKW);
//        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.LOGIN_SUCCESS);
//        return returnGoodsReportService.getDataPrint(filter);
//    }
}

