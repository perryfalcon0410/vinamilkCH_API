package vn.viettel.report.controller;

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
import vn.viettel.report.messaging.ExportGoodFilter;
import vn.viettel.report.messaging.ReportVoucherFilter;
import vn.viettel.report.messaging.TotalReport;
import vn.viettel.report.service.ReportVoucherService;
import vn.viettel.report.service.dto.ExportGoodsDTO;
import vn.viettel.report.service.dto.ReportVoucherDTO;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

@RestController
public class ReportVoucherController extends BaseController {

    @Autowired
    ReportVoucherService reportVoucherService;

    private final String root = "/reports/vouchers";

    @ApiOperation(value = "Danh sách báo cáo voucher")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root})
    public Response<Page<ReportVoucherDTO>> getAllExportGood(HttpServletRequest httpRequest,
                                                             @RequestParam(value = "fromExportDate", required = false) Date fromExportDate,
                                                             @RequestParam(value = "toExportDate", required = false) Date toExportDate,
                                                             @RequestParam(value = "fromOrderDate", required = false) Date fromOrderDate,
                                                             @RequestParam(value = "toOrderDate", required = false) Date toOrderDate,
                                                             @RequestParam(value = "lstProduct", required = false) String lstProduct,
                                                             @RequestParam(value = "lstExportType", required = false) String lstExportType,
                                                             @RequestParam(value = "searchKeywords", required = false) String searchKeywords, Pageable pageable) {
        ReportVoucherFilter filter = new ReportVoucherFilter();
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.SEARCH_REPORT_VOUCHER_SUCCESS);
        return reportVoucherService.index(filter, pageable);
    }

    @ApiOperation(value = "Xuất excel báo cáo voucher")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @RoleAdmin
    @GetMapping(V1 + root + "/excel")
    public ResponseEntity exportToExcel(HttpServletRequest httpRequest,
                                        @RequestParam(value = "fromExportDate", required = false) Date fromExportDate,
                                        @RequestParam(value = "toExportDate", required = false) Date toExportDate,
                                        @RequestParam(value = "fromOrderDate", required = false) Date fromOrderDate,
                                        @RequestParam(value = "toOrderDate", required = false) Date toOrderDate,
                                        @RequestParam(value = "lstProduct", required = false) String lstProduct,
                                        @RequestParam(value = "lstExportType", required = false) String lstExportType,
                                        @RequestParam(value = "searchKeywords", required = false) String searchKeywords) throws IOException {

        ReportVoucherFilter filter = new ReportVoucherFilter();
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.SEARCH_REPORT_VOUCHER_SUCCESS);

        ByteArrayInputStream in = reportVoucherService.exportExcel(filter);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=Voucher_Filled_Date.xlsx");

        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.EXPORT_EXCEL_REPORT_VOUCHER_SUCCESS);
        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
    }
}
