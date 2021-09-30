package vn.viettel.report.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import vn.viettel.report.messaging.SellsReportsRequest;
import vn.viettel.report.messaging.UserDataResponse;
import vn.viettel.report.service.SellsReportService;
import vn.viettel.report.service.dto.ReportDateDTO;
import vn.viettel.report.service.dto.SellDTO;
import vn.viettel.report.service.dto.SellTotalDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;


@RestController
@Api(tags = "API báo cáo bán hàng")
public class SellReportController extends BaseController {
    private final String root = "/reports/sells";

    @Autowired
    SellsReportService sellsReportService;

    @GetMapping(V1 + root)
    @ApiOperation(value = "Danh sách dữ liệu báo cáo bán hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<CoverResponse<Page<SellDTO>, SellTotalDTO>> getReportSells(
            HttpServletRequest request,
            @RequestParam(value = "orderNumber", required = false) String orderNumber,
            @RequestParam(value = "fromDate") Date fromDate,
            @RequestParam(value = "toDate") Date toDate,
            @RequestParam(value = "productKW", required = false) String productKW,
            @RequestParam(value = "collecter", required = false) Integer collecter,
            @RequestParam(value = "salesChannel", required = false) Integer salesChannel,
            @RequestParam(value = "customerKW", required = false) String customerKW,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(value = "fromInvoiceSales", required = false) Double fromInvoiceSales,
            @RequestParam(value = "toInvoiceSales", required = false) Double toInvoiceSales,
            Pageable pageable) {
        SellsReportsRequest filter = new SellsReportsRequest(this.getShopId(request), orderNumber, DateUtils.convertFromDate(fromDate), DateUtils.convertFromDate(toDate), productKW, collecter, salesChannel, customerKW, phoneNumber, fromInvoiceSales, toInvoiceSales);
        CoverResponse<Page<SellDTO>, SellTotalDTO> response = sellsReportService.getSellReport(filter, pageable);
        LogFile.logToFile(appName, getUsername(request), LogLevel.INFO, request, LogMessage.SEARCH_REPORT_SELLS_SUCCESS);
        return new Response<CoverResponse<Page<SellDTO>, SellTotalDTO>>().withData(response);
    }


    @GetMapping(V1 + root + "/export")
    @ApiOperation(value = "Xuất excel dữ liệu báo cáo bán hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public void exportToExcel(
            HttpServletRequest request,
            @RequestParam(value = "orderNumber", required = false) String orderNumber,
            @RequestParam(value = "fromDate") Date fromDate,
            @RequestParam(value = "toDate") Date toDate,
            @RequestParam(value = "productKW", required = false) String productKW,
            @RequestParam(value = "collecter", required = false) Integer collecter,
            @RequestParam(value = "salesChannel", required = false) Integer salesChannel,
            @RequestParam(value = "customerKW", required = false) String customerKW,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(value = "fromInvoiceSales", required = false) Double fromInvoiceSales,
            @RequestParam(value = "toInvoiceSales", required = false) Double toInvoiceSales, HttpServletResponse response) throws IOException, InterruptedException {

        SellsReportsRequest filter = new SellsReportsRequest(this.getShopId(request), orderNumber, DateUtils.convertFromDate(fromDate), DateUtils.convertFromDate(toDate), productKW, collecter, salesChannel, customerKW, phoneNumber, fromInvoiceSales, toInvoiceSales);
        this.closeStreamExcel(response, sellsReportService.exportExcel(filter), "Bao_Cao_Ban_Hang_Filled_");
        LogFile.logToFile(appName, getUsername(request), LogLevel.INFO, request, LogMessage.EXPORT_EXCEL_REPORT_SELLS_SUCCESS);
        response.getOutputStream().flush();
    }

    @GetMapping(V1 + root + "/print")
    @ApiOperation(value = "In báo cáo bán hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<CoverResponse<List<SellDTO>, ReportDateDTO>> getDataPrint(
            HttpServletRequest request,
            @RequestParam(value = "orderNumber", required = false) String orderNumber,
            @RequestParam(value = "fromDate") Date fromDate,
            @RequestParam(value = "toDate") Date toDate,
            @RequestParam(value = "productKW", required = false) String productKW,
            @RequestParam(value = "collecter", required = false) Integer collecter,
            @RequestParam(value = "salesChannel", required = false) Integer salesChannel,
            @RequestParam(value = "customerKW", required = false) String customerKW,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(value = "fromInvoiceSales", required = false) Double fromInvoiceSales,
            @RequestParam(value = "toInvoiceSales", required = false) Double toInvoiceSales) {
        SellsReportsRequest filter = new SellsReportsRequest(this.getShopId(request), orderNumber, DateUtils.convertFromDate(fromDate), DateUtils.convertFromDate(toDate), productKW, collecter, salesChannel, customerKW, phoneNumber, fromInvoiceSales, toInvoiceSales);
        CoverResponse<List<SellDTO>, ReportDateDTO> response = sellsReportService.getDataPrint(filter);
        LogFile.logToFile(appName, getUsername(request), LogLevel.INFO, request, LogMessage.GET_DATA_PRINT_REPORT_SELLS_SUCCESS);
        return new Response<CoverResponse<List<SellDTO>, ReportDateDTO>>().withData(response);
    }


    @GetMapping(V1 + root + "/get-data-user")
    @ApiOperation(value = "api lấy thông tin nhân viên cửa hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<List<UserDataResponse>> getDataUser(
            HttpServletRequest request) {
        List<UserDataResponse> response = sellsReportService.getDataUser(this.getShopId(request));
        LogFile.logToFile(appName, getUsername(request), LogLevel.INFO, request, LogMessage.GET_DATA_PRINT_REPORT_SELLS_SUCCESS);
        return new Response<List<UserDataResponse>>().withData(response);
    }

}

