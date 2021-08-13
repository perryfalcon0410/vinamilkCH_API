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
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.StringUtils;
import vn.viettel.report.messaging.ReturnGoodsReportsRequest;
import vn.viettel.report.service.ReturnGoodsReportService;
import vn.viettel.report.service.dto.ReportPrintIndustryTotalDTO;
import vn.viettel.report.service.dto.ReportPrintTotalDTO;
import vn.viettel.report.service.dto.ReturnGoodsDTO;
import vn.viettel.report.service.dto.ReportTotalDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
            @RequestParam(value = "reciept", required = false,defaultValue = "") String reciept,
            @RequestParam(value = "fromDate") Date fromDate,
            @RequestParam(value = "toDate") Date toDate,
            @RequestParam(value = "reason", required = false) String reason,
            @RequestParam(value = "productKW", required = false, defaultValue = "") String productKW,
            Pageable pageable) {
        ReturnGoodsReportsRequest filter = new ReturnGoodsReportsRequest(this.getShopId(),
                reciept.toUpperCase(Locale.ROOT), DateUtils.convertFromDate(fromDate), DateUtils.convertFromDate(toDate), reason, productKW.toUpperCase(Locale.ROOT));
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
    public void exportToExcel(
            HttpServletRequest request,
            @RequestParam(value = "reciept", required = false,defaultValue = "") String reciept,
            @RequestParam(value = "fromDate") Date fromDate,
            @RequestParam(value = "toDate") Date toDate,
            @RequestParam(value = "reason", required = false) String reason,
            @RequestParam(value = "productKW", required = false,defaultValue = "") String productKW, HttpServletResponse response) throws IOException, ParseException {

        ReturnGoodsReportsRequest filter = new ReturnGoodsReportsRequest(this.getShopId(),
                reciept.toUpperCase(Locale.ROOT), DateUtils.convertFromDate(fromDate), DateUtils.convertFromDate(toDate), reason, productKW.toUpperCase(Locale.ROOT));
        ByteArrayInputStream in = returnGoodsReportService.exportExcel(filter);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.EXPORT_EXCEL_REPORT_RETURN_GOODS_SUCCESS);
        response.setContentType("application/octet-stream");
        response.addHeader("Content-Disposition", "attachment; filename=DB_Hang_tra_lai_Filled_" + StringUtils.createExcelFileName());
        FileCopyUtils.copy(in, response.getOutputStream());
        in.close();
        response.getOutputStream().flush();
    }


    @GetMapping(V1 + root + "/print")
    @ApiOperation(value = "In dữ liệu báo cáo hàng trả lại")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<ReportPrintIndustryTotalDTO> getDataPrint(
            HttpServletRequest request,
            @RequestParam(value = "reciept", required = false,defaultValue = "") String reciept,
            @RequestParam(value = "fromDate") Date fromDate,
            @RequestParam(value = "toDate") Date toDate,
            @RequestParam(value = "reason", required = false) String reason,
            @RequestParam(value = "productKW", required = false,defaultValue = "") String productKW) {
        ReturnGoodsReportsRequest filter = new ReturnGoodsReportsRequest(this.getShopId(),
                reciept.toUpperCase(Locale.ROOT), DateUtils.convertFromDate(fromDate), DateUtils.convertFromDate(toDate), reason, productKW.toUpperCase(Locale.ROOT));
        ReportPrintIndustryTotalDTO response = returnGoodsReportService.getDataPrint(filter);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_DATA_PRINT_REPORT_RETURN_GOODS_SUCCESS);
        return new Response<ReportPrintIndustryTotalDTO>().withData(response);
    }
}
