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
import vn.viettel.report.messaging.EntryMenuDetailsReportsRequest;
import vn.viettel.report.service.EntryMenuDetailsReportService;
import vn.viettel.report.service.dto.EntryMenuDetailsDTO;
import vn.viettel.report.service.dto.ReportDateDTO;
import vn.viettel.report.service.dto.ReportTotalDTO;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@RestController
@Api(tags = "API báo cáo bảng kê chi tiết đơn nhập hàng")
public class EntryMenuDetailsController extends BaseController {
    private final String root = "/reports/entryMenuDetails";

    @Autowired
    EntryMenuDetailsReportService entryMenuDetailsReportService;


    @GetMapping(V1 + root)
    @ApiOperation(value = "Danh sách dữ liệu báo cáo bảng kê chi tiết đơn nhập hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<CoverResponse<Page<EntryMenuDetailsDTO>, ReportTotalDTO>> getReportEntryMenuDetail(
            HttpServletRequest request,
            @RequestParam(value = "fromDate", required = false) Date fromDate,
            @RequestParam(value = "toDate", required = false) Date toDate,
            Pageable pageable) {
        EntryMenuDetailsReportsRequest filter = new EntryMenuDetailsReportsRequest(this.getShopId(), fromDate, toDate);
        CoverResponse<Page<EntryMenuDetailsDTO>, ReportTotalDTO> response = entryMenuDetailsReportService.getEntryMenuDetailsReport(filter, pageable);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.SEARCH_REPORT_ENTRY_MENU_DETAILS_SUCCESS);
        return new Response<CoverResponse<Page<EntryMenuDetailsDTO>,ReportTotalDTO>>().withData(response);
    }

    @RoleAdmin
    @GetMapping(V1 + root + "/export")
    @ApiOperation(value = "Xuất excel báo cáo bảng kê chi tiết đơn nhập hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public ResponseEntity exportToExcel(
            HttpServletRequest request,
            @RequestParam(value = "fromDate", required = false) Date fromDate,
            @RequestParam(value = "toDate", required = false) Date toDate) throws IOException {

        EntryMenuDetailsReportsRequest filter = new EntryMenuDetailsReportsRequest(this.getShopId(), fromDate, toDate);
        ByteArrayInputStream in = entryMenuDetailsReportService.exportExcel(filter);
        HttpHeaders headers = new HttpHeaders();
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        headers.add("Content-Disposition", "attachment; filename=DB_Bang_ke_chi_tiet_hoa_don-nhap_hang_Filled_" + date + ".xlsx");
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.EXPORT_EXCEL_REPORT_ENTRY_MENU_DETAILS_SUCCESS);
        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
    }


    @GetMapping(V1 + root + "/print")
    @ApiOperation(value = "In báo cáo bảng kê chi tiết đơn nhập hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<CoverResponse<List<EntryMenuDetailsDTO>, ReportDateDTO>> getDataPrint(
            HttpServletRequest request,
            @RequestParam(value = "fromDate", required = false) Date fromDate,
            @RequestParam(value = "toDate", required = false) Date toDate) {
        EntryMenuDetailsReportsRequest filter = new EntryMenuDetailsReportsRequest(this.getShopId(), fromDate, toDate);
        CoverResponse<List<EntryMenuDetailsDTO> , ReportDateDTO> response = entryMenuDetailsReportService.getEntryMenuDetails(filter);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_DATA_PRINT_REPORT_ENTRY_MENU_DETAILS_SUCCESS);
        return new Response<CoverResponse<List<EntryMenuDetailsDTO> , ReportDateDTO>>().withData(response);
    }
}
