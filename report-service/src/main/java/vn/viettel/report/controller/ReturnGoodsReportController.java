package vn.viettel.report.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
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
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.StringUtils;
import vn.viettel.report.messaging.ReturnGoodsReportsRequest;
import vn.viettel.report.service.ReturnGoodsReportService;
import vn.viettel.report.service.dto.ReturnGoodsDTO;
import vn.viettel.report.service.dto.ReturnGoodsReportDTO;
import vn.viettel.report.service.dto.ReportTotalDTO;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@RestController
@Api(tags = "API báo cáo hàng trả lại")
public class ReturnGoodsReportController extends BaseController {
    private final String root = "/reports/returnGoods";

    @Autowired
    ReturnGoodsReportService returnGoodsReportService;

    @GetMapping(V1 + root)
    @ApiOperation(value = "Danh sách dữ liệu báo cáo hàng trả lại")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<CoverResponse<Page<ReturnGoodsDTO>, ReportTotalDTO>> getReportReturnGoods(
            HttpServletRequest request,
            @RequestParam(value = "reciept", required = false) String reciept,
            @RequestParam(value = "fromDate", required = false) Date fromDate,
            @RequestParam(value = "toDate", required = false) Date toDate,
            @RequestParam(value = "reason", required = false) String reason,
            @RequestParam(value = "productKW", required = false) String productKW,
            Pageable pageable) {
        ReturnGoodsReportsRequest filter = new ReturnGoodsReportsRequest(this.getShopId(), reciept, DateUtils.convert2Local(fromDate), DateUtils.convert2Local(toDate), reason, productKW);
        CoverResponse<Page<ReturnGoodsDTO>, ReportTotalDTO> response = returnGoodsReportService.getReturnGoodsReport(filter, pageable);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.SEARCH_REPORT_RETURN_GOODS_SUCCESS);
        return new  Response<CoverResponse<Page<ReturnGoodsDTO>, ReportTotalDTO>>().withData(response);
    }

    @GetMapping(V1 + root + "/export")
    @ApiOperation(value = "Xuất excel dữ liệu báo cáo hàng trả lại")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public ResponseEntity exportToExcel(
            HttpServletRequest request,
            @RequestParam(value = "reciept", required = false) String reciept,
            @RequestParam(value = "fromDate", required = false) Date fromDate,
            @RequestParam(value = "toDate", required = false) Date toDate,
            @RequestParam(value = "reason", required = false) String reason,
            @RequestParam(value = "productKW", required = false) String productKW) throws IOException {

        ReturnGoodsReportsRequest filter = new ReturnGoodsReportsRequest(this.getShopId(), reciept, DateUtils.convert2Local(fromDate), DateUtils.convert2Local(toDate), reason, productKW);
        ByteArrayInputStream in = returnGoodsReportService.exportExcel(filter);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=DB_Hang_tra_lai_Filled_" + StringUtils.createExcelFileName());
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.EXPORT_EXCEL_REPORT_RETURN_GOODS_SUCCESS);
        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
    }


    @GetMapping(V1 + root + "/print")
    @ApiOperation(value = "In dữ liệu báo cáo hàng trả lại")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<CoverResponse<List<ReturnGoodsReportDTO>, ReportTotalDTO>> getDataPrint(
            HttpServletRequest request,
            @RequestParam(value = "reciept", required = false) String reciept,
            @RequestParam(value = "fromDate", required = false) Date fromDate,
            @RequestParam(value = "toDate", required = false) Date toDate,
            @RequestParam(value = "reason", required = false) String reason,
            @RequestParam(value = "productKW", required = false) String productKW) {
        ReturnGoodsReportsRequest filter = new ReturnGoodsReportsRequest(this.getShopId(), reciept, DateUtils.convert2Local(fromDate), DateUtils.convert2Local(toDate), reason, productKW);
        CoverResponse<List<ReturnGoodsReportDTO>, ReportTotalDTO> response = returnGoodsReportService.getDataPrint(filter);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_DATA_PRINT_REPORT_RETURN_GOODS_SUCCESS);
        return new Response<CoverResponse<List<ReturnGoodsReportDTO>, ReportTotalDTO>>().withData(response);
    }
}
