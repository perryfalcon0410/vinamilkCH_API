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
import vn.viettel.report.messaging.PrintGoodFilter;
import vn.viettel.report.messaging.TotalReport;
import vn.viettel.report.service.ReportExportGoodsService;
import vn.viettel.report.service.dto.ExportGoodsDTO;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

@RestController
public class ReportExportGoodsController extends BaseController {

    @Autowired
    ReportExportGoodsService reportExportGoodsService;

        private final String root = "/reports/export-goods";

    @ApiOperation(value = "Danh sách báo cáo xuất hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root})
    public Response<CoverResponse<Page<ExportGoodsDTO>, TotalReport>> getAllExportGood(HttpServletRequest httpRequest,
                                                                                       @RequestParam(value = "fromExportDate", required = false) Date fromExportDate,
                                                                                       @RequestParam(value = "toExportDate", required = false) Date toExportDate,
                                                                                       @RequestParam(value = "fromOrderDate", required = false) Date fromOrderDate,
                                                                                       @RequestParam(value = "toOrderDate", required = false) Date toOrderDate,
                                                                                       @RequestParam(value = "lstProduct", required = false) String lstProduct,
                                                                                       @RequestParam(value = "lstExportType", required = false) String lstExportType,
                                                                                       @RequestParam(value = "searchKeywords", required = false) String searchKeywords, Pageable pageable) {
        ExportGoodFilter exportGoodFilter = new ExportGoodFilter(this.getShopId(), fromExportDate, toExportDate, fromOrderDate,
                toOrderDate, lstProduct, lstExportType, searchKeywords);
        CoverResponse<Page<ExportGoodsDTO>, TotalReport> response = reportExportGoodsService.index(exportGoodFilter, pageable);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.SEARCH_REPORT_EXPORT_GOODS_SUCCESS);
        return new Response<CoverResponse<Page<ExportGoodsDTO>, TotalReport>>().withData(response);
    }

    @ApiOperation(value = "Xuất excel báo cáo xuất hàng")
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

        ExportGoodFilter exportGoodFilter = new ExportGoodFilter(this.getShopId(), fromExportDate, toExportDate, fromOrderDate,
                toOrderDate, lstProduct, lstExportType, searchKeywords);
        ByteArrayInputStream in = reportExportGoodsService.exportExcel(exportGoodFilter);
        HttpHeaders headers = new HttpHeaders();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        String fileName = "CuaHangXuatHang_"+dateFormat.format(timestamp)+".xlsx";
        headers.add("Content-Disposition", "attachment; filename=" + fileName);

        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.EXPORT_EXCEL_REPORT_EXPORT_GOODS_SUCCESS);
        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
    }

    @ApiOperation(value = "Danh sách in báo cáo xuất hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root + "/print"})
    public Response<CoverResponse<PrintGoodFilter, TotalReport>> getDataToPrint(HttpServletRequest httpRequest,
                                                                                @RequestParam(value = "fromExportDate", required = false) Date fromExportDate,
                                                                                @RequestParam(value = "toExportDate", required = false) Date toExportDate,
                                                                                @RequestParam(value = "fromOrderDate", required = false) Date fromOrderDate,
                                                                                @RequestParam(value = "toOrderDate", required = false) Date toOrderDate,
                                                                                @RequestParam(value = "lstProduct", required = false) String lstProduct,
                                                                                @RequestParam(value = "lstExportType", required = false) String lstExportType,
                                                                                @RequestParam(value = "searchKeywords", required = false) String searchKeywords) {
        ExportGoodFilter exportGoodFilter = new ExportGoodFilter(this.getShopId(), fromExportDate, toExportDate, fromOrderDate,
                toOrderDate, lstProduct, lstExportType, searchKeywords);
        CoverResponse<PrintGoodFilter, TotalReport> coverResponse = reportExportGoodsService.getDataToPrint(exportGoodFilter);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.GET_DATA_PRINT_REPORT_EXPORT_GOODS_SUCCESS);
        return new Response<CoverResponse<PrintGoodFilter, TotalReport>>().withData(coverResponse);
    }
}
