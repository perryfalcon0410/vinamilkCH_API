package vn.viettel.report.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.StringUtils;
import vn.viettel.report.messaging.ExchangeTransFilter;
import vn.viettel.report.messaging.SaleCategoryFilter;
import vn.viettel.report.service.SaleByCategoryReportService;
import vn.viettel.report.service.dto.SaleByCategoryPrintDTO;
import vn.viettel.report.service.dto.SalesByCategoryReportDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@RestController
public class SaleByCategoryController extends BaseController {
    private final String root = "/reports/sale-category";

    @Autowired
    SaleByCategoryReportService saleByCategoryReportService;

    @ApiOperation(value = "Xuất excel báo cáo doanh so theo ngành hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(V1 + root + "/excel")
    public void exportToExcel(@RequestParam(value = "customerKW", required = false, defaultValue = "") String customerKW,
                                      @RequestParam(value = "customerPhone", required = false, defaultValue = "") String customerPhone,
                                      @RequestParam(value = "fromDate", required = false) Date fromDate,
                                      @RequestParam(value = "toDate", required = false) Date toDate,
                                      @RequestParam(value = "customerType", required = false) Long customerType, HttpServletResponse response) throws IOException {
        SaleCategoryFilter filter = new SaleCategoryFilter(customerKW,customerPhone,DateUtils.convert2Local(fromDate), DateUtils.convert2Local(toDate),customerType,this.getShopId());
        ByteArrayInputStream in = saleByCategoryReportService.exportExcel(filter);
        response.setContentType("application/octet-stream");
        response.addHeader("Content-Disposition", "attachment; filename=BC_doanh_so_theo_nganh_hang_" + StringUtils.createExcelFileName());
        FileCopyUtils.copy(in, response.getOutputStream());
        IOUtils.closeQuietly(in);
        response.getOutputStream().flush();
    }

    @ApiOperation(value = "Danh sách doanh số theo ngành hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(V1 + root)
    public Response<SalesByCategoryReportDTO> getReportSaleByCategory (
            HttpServletRequest request,
            @RequestParam(value = "customerKW", required = false, defaultValue = "") String customerKW,
            @RequestParam(value = "customerPhone", required = false, defaultValue = "") String customerPhone,
            @RequestParam(value = "fromDate", required = false) Date fromDate,
            @RequestParam(value = "toDate", required = false) Date toDate,
            @RequestParam(value = "customerType", required = false) Long customerType, Pageable pageable) {
        SaleCategoryFilter filter = new SaleCategoryFilter(customerKW,customerPhone,DateUtils.convert2Local(fromDate), DateUtils.convert2Local(toDate),customerType,this.getShopId());
        SalesByCategoryReportDTO response = saleByCategoryReportService.getSaleByCategoryReport(filter, pageable);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.FIND_REPORT_PROMOTION_PRODUCTS_SUCCESS);
        return new Response<SalesByCategoryReportDTO>().withData(response);
    }

    @GetMapping(V1 + root + "/print")
    public Response<SaleByCategoryPrintDTO> printReportSaleByCategory (
            HttpServletRequest request,
            @RequestParam(value = "customerKW", required = false, defaultValue = "") String customerKW,
            @RequestParam(value = "customerPhone", required = false, defaultValue = "") String customerPhone,
            @RequestParam(value = "fromDate", required = false) Date fromDate,
            @RequestParam(value = "toDate", required = false) Date toDate,
            @RequestParam(value = "customerType", required = false) Long customerType, Pageable pageable) {
        SaleCategoryFilter filter = new SaleCategoryFilter(customerKW,customerPhone,DateUtils.convert2Local(fromDate), DateUtils.convert2Local(toDate),customerType,this.getShopId());
        SaleByCategoryPrintDTO response = saleByCategoryReportService.print(filter);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.FIND_REPORT_PROMOTION_PRODUCTS_SUCCESS);
        return new Response<SaleByCategoryPrintDTO>().withData(response);
    }
}
