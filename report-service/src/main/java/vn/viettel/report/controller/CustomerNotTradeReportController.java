package vn.viettel.report.controller;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.StringUtils;
import vn.viettel.report.messaging.CustomerTradeFilter;
import vn.viettel.report.service.CustomerNotTradeService;
import vn.viettel.report.service.dto.CustomerNotTradePrintDTO;
import vn.viettel.report.service.dto.CustomerReportDTO;
import vn.viettel.report.service.dto.CustomerTradeDTO;
import vn.viettel.report.service.dto.StockTotalReportPrintDTO;
import vn.viettel.report.service.excel.CustomerNotTradeExcel;
import vn.viettel.report.service.feign.ShopClient;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@RestController
@Api(tags = "API báo cáo khách hàng")
public class CustomerNotTradeReportController extends BaseController {
    @Autowired
    CustomerNotTradeService service;
    @Autowired
    ShopClient shopClient;

    private final String root = "/reports/customers";

    @GetMapping(V1 + root + "/not-trade")
    public Object getCustomerNotTrade(@RequestParam Date fromDate, @RequestParam Date toDate,
                                      @RequestParam Boolean isPaging, Pageable pageable) {
        return service.index(fromDate, toDate, isPaging, pageable, this.getShopId());
    }

    @GetMapping(value = { V1 + root + "/not-trade/excel"})
    public void exportToExcel(HttpServletRequest request, @RequestParam(required = false) Date fromDate,
                                        @RequestParam(required = false) Date toDate,
            HttpServletResponse response, Pageable pageable) throws IOException{
        ShopDTO shop = shopClient.getShopByIdV1(this.getShopId()).getData();
        Response<List<CustomerReportDTO>> listData = (Response<List<CustomerReportDTO>>) service.index(fromDate, toDate, false, pageable, this.getShopId());

        CustomerNotTradeExcel exportExcel = new CustomerNotTradeExcel(listData.getData(), shop, fromDate, toDate);

        ByteArrayInputStream in = exportExcel.export();
        response.setContentType("application/octet-stream");
        response.addHeader("Content-Disposition", "attachment; filename=report_x_" + StringUtils.createExcelFileName());
        FileCopyUtils.copy(in, response.getOutputStream());
        response.getOutputStream().flush();
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.EXPORT_EXCEL_CUSTOMER_NOT_TRADE_SUCCESS);

    }

    @ApiOperation(value = "In danh sách báo cáo khách hàng không giao")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(V1 + root + "/print")
    public Response<CustomerNotTradePrintDTO> print(@RequestParam Date fromDate,
                                                    @RequestParam Date toDate){
        CustomerNotTradePrintDTO response = service.printCustomerNotTrade(fromDate, toDate, this.getShopId());
        return new Response<CustomerNotTradePrintDTO>().withData(response);
    }

    @GetMapping(V1 + root + "/trade")
    @ApiOperation(value = "Danh sách khách hàng có giao dịch")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<Page<CustomerTradeDTO>> findCustomerTrades(HttpServletRequest request, @ApiParam("Tìm theo mã, họ tên khách hàng") @RequestParam(required = false, defaultValue = "") String keySearch,
                                                             @ApiParam("Tìm theo mã khu vực") @RequestParam(required = false) String areaCode,
                                                             @ApiParam("Tìm theo loại khách hàng") @RequestParam(required = false) Integer customerType,
                                                             @ApiParam("Tìm theo trạng thái khách hàng") @RequestParam(required = false) Integer customerStatus,
                                                             @ApiParam("Tìm theo số điện thoại khách hàng") @RequestParam(required = false, defaultValue = "") String customerPhone,
                                                             @ApiParam("Tìm theo thời gian tạo khách hàng nhỏ nhất") @RequestParam(required = false)  Date fromCreateDate,
                                                             @ApiParam("Tìm theo thời gian tạo khách hàng lớn nhất") @RequestParam(required = false)  Date toCreateDate,
                                                             @ApiParam("Tìm theo thời gian mua hàng nhỏ nhất") @RequestParam(required = false)  Date fromPurchaseDate,
                                                             @ApiParam("Tìm theo thời gian mua hàng lớn nhất") @RequestParam(required = false)  Date toPurchaseDate,
                                                             @ApiParam("Tìm theo doanh số tối thiểu") @RequestParam(required = false) Float fromSaleAmount,
                                                             @ApiParam("Tìm theo doanh số tối đa") @RequestParam(required = false) Float toSaleAmount,
                                                             @ApiParam("Tìm doanh số có thời gian từ") @RequestParam(required = false) Date fromSaleDate,
                                                             @ApiParam("Tìm doanh số có thời gian đến") @RequestParam(required = false) Date toSaleDate, Pageable pageable) {

        CustomerTradeFilter filter = new CustomerTradeFilter(this.getShopId(), keySearch, areaCode, customerType,
                customerStatus, customerPhone).withCreateAt(DateUtils.convertFromDate(fromCreateDate), DateUtils.convertToDate(toCreateDate))
                .withPurchaseAt(DateUtils.convertFromDate(fromPurchaseDate), DateUtils.convertToDate(toPurchaseDate))
                .withSaleAmount(fromSaleAmount, toSaleAmount)
                .withSaleAt(DateUtils.convertFromDate(fromSaleDate), DateUtils.convertToDate(toSaleDate));

        Page<CustomerTradeDTO> response = service.findCustomerTrades(filter, pageable);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.FIND_REPORT_CUSTOMER_TRADE_SUCCESS);
        return new Response<Page<CustomerTradeDTO>>().withData(response);
    }

    @GetMapping(V1 + root + "/trade/excel")
    @ApiOperation(value = "Xuất excel danh sách khách hàng có giao dịch")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public void customerTradesExportExcel(HttpServletRequest request, @ApiParam("Tìm theo mã, họ tên khách hàng") @RequestParam(required = false, defaultValue = "") String keySearch,
                                                               @ApiParam("Tìm theo mã khu vực") @RequestParam(required = false) String areaCode,
                                                               @ApiParam("Tìm theo loại khách hàng") @RequestParam(required = false) Integer customerType,
                                                               @ApiParam("Tìm theo trạng thái khách hàng") @RequestParam(required = false) Integer customerStatus,
                                                               @ApiParam("Tìm theo số điện thoại khách hàng") @RequestParam(required = false, defaultValue = "") String customerPhone,
                                                               @ApiParam("Tìm theo thời gian tạo khách hàng nhỏ nhất") @RequestParam(required = false) Date fromCreateDate,
                                                               @ApiParam("Tìm theo thời gian tạo khách hàng lớn nhất") @RequestParam(required = false) Date toCreateDate,
                                                               @ApiParam("Tìm theo thời gian mua hàng nhỏ nhất") @RequestParam(required = false) Date fromPurchaseDate,
                                                               @ApiParam("Tìm theo thời gian mua hàng lớn nhất") @RequestParam(required = false) Date toPurchaseDate,
                                                               @ApiParam("Tìm theo doanh số tối thiểu") @RequestParam(required = false) Float fromSaleAmount,
                                                               @ApiParam("Tìm theo doanh số tối đa") @RequestParam(required = false) Float toSaleAmount,
                                                               @ApiParam("Tìm doanh số có thời gian từ") @RequestParam(required = false) Date fromSaleDate,
                                                               @ApiParam("Tìm doanh số có thời gian đến") @RequestParam(required = false) Date toSaleDate, HttpServletResponse response) throws IOException {
        CustomerTradeFilter filter = new CustomerTradeFilter(this.getShopId(), keySearch, areaCode, customerType,
                customerStatus, customerPhone).withCreateAt(DateUtils.convertFromDate(fromCreateDate), DateUtils.convertToDate(toCreateDate))
                .withPurchaseAt(DateUtils.convertFromDate(fromPurchaseDate), DateUtils.convertToDate(toPurchaseDate))
                .withSaleAmount(fromSaleAmount, toSaleAmount)
                .withSaleAt(DateUtils.convertFromDate(fromSaleDate), DateUtils.convertToDate(toSaleDate));

        ByteArrayInputStream in = service.customerTradesExportExcel(filter);

        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.EXPORT_EXCEL_CUSTOMER_TRADE_SUCCESS);
        response.setContentType("application/octet-stream");
        response.addHeader("Content-Disposition", "attachment; filename=Danh_sach_khach_hang_khong_gd_" + StringUtils.createExcelFileName());
        FileCopyUtils.copy(in, response.getOutputStream());
        response.getOutputStream().flush();
    }

}
