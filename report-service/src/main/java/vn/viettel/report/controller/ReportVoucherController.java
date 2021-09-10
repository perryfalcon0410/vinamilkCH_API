package vn.viettel.report.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.StringUtils;
import vn.viettel.report.messaging.ReportVoucherFilter;
import vn.viettel.report.service.ReportVoucherService;
import vn.viettel.report.service.dto.ReportVoucherDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
                                                             @RequestParam(value = "fromProgramDate", required = false) Date fromProgramDate,
                                                             @RequestParam(value = "toProgramDate", required = false) Date toProgramDate,
                                                             @RequestParam(value = "fromUseDate", required = false) Date fromUseDate,
                                                             @RequestParam(value = "toUseDate", required = false) Date toUseDate,
                                                             @RequestParam(value = "voucherProgramName", required = false) String voucherProgramName,
                                                             @RequestParam(value = "voucherKeywords", required = false) String voucherKeywords,
                                                             @RequestParam(value = "customerKeywords", required = false) String customerKeywords,
                                                             @RequestParam(value = "customerMobiPhone", required = false) String customerMobiPhone, Pageable pageable) {
        ReportVoucherFilter filter = new ReportVoucherFilter(DateUtils.convertFromDate(fromProgramDate), DateUtils.convertFromDate(toProgramDate), DateUtils.convertFromDate(fromUseDate), DateUtils.convertFromDate(toUseDate), voucherProgramName,
                voucherKeywords, customerKeywords, customerMobiPhone, this.getShopId());
        Page<ReportVoucherDTO> reportVoucherDTOS = reportVoucherService.index(filter, pageable);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.SEARCH_REPORT_VOUCHER_SUCCESS);
        return new Response<Page<ReportVoucherDTO>>().withData(reportVoucherDTOS);
    }

    @ApiOperation(value = "Xuất excel báo cáo voucher")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )

    @GetMapping(V1 + root + "/excel")
    public void exportToExcel(HttpServletRequest httpRequest,
                                        @RequestParam(value = "fromProgramDate", required = false) Date fromProgramDate,
                                        @RequestParam(value = "toProgramDate", required = false) Date toProgramDate,
                                        @RequestParam(value = "fromUseDate", required = false) Date fromUseDate,
                                        @RequestParam(value = "toUseDate", required = false) Date toUseDate,
                                        @RequestParam(value = "voucherProgramName", required = false) String voucherProgramName,
                                        @RequestParam(value = "voucherKeywords", required = false) String voucherKeywords,
                                        @RequestParam(value = "customerKeywords", required = false) String customerKeywords,
                                        @RequestParam(value = "customerMobiPhone", required = false) String customerMobiPhone, HttpServletResponse response) throws IOException {

        ReportVoucherFilter filter = new ReportVoucherFilter(DateUtils.convertFromDate(fromProgramDate), DateUtils.convertFromDate(toProgramDate), DateUtils.convertFromDate(fromUseDate), DateUtils.convertFromDate(toUseDate), voucherProgramName,
                voucherKeywords, customerKeywords, customerMobiPhone, this.getShopId());
        this.closeStreamExcel(response, reportVoucherService.exportExcel(filter), "BC_voucher" + StringUtils.createExcelFileName());
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.EXPORT_EXCEL_REPORT_VOUCHER_SUCCESS);
        response.getOutputStream().flush();
    }
}
