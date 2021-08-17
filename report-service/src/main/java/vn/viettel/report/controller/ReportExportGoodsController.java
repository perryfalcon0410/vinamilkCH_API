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
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.StringUtils;
import vn.viettel.report.messaging.ShopExportFilter;
import vn.viettel.report.messaging.PrintGoodFilter;
import vn.viettel.report.messaging.TotalReport;
import vn.viettel.report.service.ReportExportGoodsService;
import vn.viettel.report.service.dto.PrintShopExportDTO;
import vn.viettel.report.service.dto.ShopExportDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
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
    public Response<CoverResponse<Page<ShopExportDTO>, TotalReport>> getAllExportGood(HttpServletRequest httpRequest,
                                                                                      @RequestParam(value = "fromExportDate", required = false) Date fromExportDate,
                                                                                      @RequestParam(value = "toExportDate", required = false) Date toExportDate,
                                                                                      @RequestParam(value = "lstProduct", required = false, defaultValue = "") String productCodes,
                                                                                      @RequestParam(value = "lstExportType", required = false) String importType,
                                                                                      @RequestParam(value = "searchKeywords", required = false) String searchKeywords,
                                                                                      @RequestParam(value = "fromOrderDate", required = false) Date fromOrderDate,
                                                                                      @RequestParam(value = "toOrderDate", required = false) Date toOrderDate, Pageable pageable) {
        ShopExportFilter shopExportFilter = new ShopExportFilter(DateUtils.convertFromDate(fromExportDate), DateUtils.convertFromDate(toExportDate), productCodes, importType, searchKeywords,
                DateUtils.convertFromDate(fromOrderDate), DateUtils.convertFromDate(toOrderDate), this.getShopId());
        CoverResponse<Page<ShopExportDTO>, TotalReport> response = reportExportGoodsService.index(shopExportFilter, pageable);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.SEARCH_REPORT_EXPORT_GOODS_SUCCESS);
        return new Response<CoverResponse<Page<ShopExportDTO>, TotalReport>>().withData(response);
    }

    @ApiOperation(value = "Xuất excel báo cáo xuất hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(V1 + root + "/excel")
    public void exportToExcel(HttpServletRequest httpRequest,
                              @RequestParam(value = "fromExportDate", required = false) Date fromExportDate,
                              @RequestParam(value = "toExportDate", required = false) Date toExportDate,
                              @RequestParam(value = "lstProduct", required = false, defaultValue = "") String productCodes,
                              @RequestParam(value = "lstExportType", required = false) String importType,
                              @RequestParam(value = "searchKeywords", required = false) String searchKeywords,
                              @RequestParam(value = "fromOrderDate", required = false) Date fromOrderDate,
                              @RequestParam(value = "toOrderDate", required = false) Date toOrderDate, HttpServletResponse response) throws IOException {

        ShopExportFilter shopExportFilter = new ShopExportFilter(DateUtils.convertFromDate(fromExportDate), DateUtils.convertFromDate(toExportDate), productCodes, importType, searchKeywords,
                DateUtils.convertFromDate(fromOrderDate), DateUtils.convertFromDate(toOrderDate), this.getShopId());
        ByteArrayInputStream in = reportExportGoodsService.exportExcel(shopExportFilter);

        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.EXPORT_EXCEL_REPORT_EXPORT_GOODS_SUCCESS);
        response.setContentType("application/octet-stream");
        response.addHeader("Content-Disposition", "attachment; filename=BC_xuat_hang_" + StringUtils.createExcelFileName());
        FileCopyUtils.copy(in, response.getOutputStream());
        IOUtils.closeQuietly(in);
        response.getOutputStream().flush();
    }

    @ApiOperation(value = "Danh sách in báo cáo xuất hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root + "/print"})
    public Response<PrintShopExportDTO> getDataToPrint(HttpServletRequest httpRequest,
                                                       @RequestParam(value = "fromExportDate", required = false) Date fromExportDate,
                                                       @RequestParam(value = "toExportDate", required = false) Date toExportDate,
                                                       @RequestParam(value = "lstProduct", required = false, defaultValue = "") String productCodes,
                                                       @RequestParam(value = "lstExportType", required = false) String importType,
                                                       @RequestParam(value = "searchKeywords", required = false) String searchKeywords,
                                                       @RequestParam(value = "fromOrderDate", required = false) Date fromOrderDate,
                                                       @RequestParam(value = "toOrderDate", required = false) Date toOrderDate) {
        ShopExportFilter shopExportFilter = new ShopExportFilter(DateUtils.convertFromDate(fromExportDate), DateUtils.convertFromDate(toExportDate), productCodes, importType, searchKeywords,
                DateUtils.convertFromDate(fromOrderDate), DateUtils.convertFromDate(toOrderDate), this.getShopId());
        PrintShopExportDTO coverResponse = reportExportGoodsService.getDataToPrint(shopExportFilter);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.GET_DATA_PRINT_REPORT_EXPORT_GOODS_SUCCESS);
        return new Response<PrintShopExportDTO>().withData(coverResponse);
    }
}
