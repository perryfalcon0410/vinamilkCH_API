package vn.viettel.report.controller;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.report.messaging.CustomerNotTradeFilter;
import vn.viettel.report.messaging.CustomerTradeFilter;
import vn.viettel.report.service.CustomerNotTradeService;
import vn.viettel.report.service.dto.CustomerNotTradePrintDTO;
import vn.viettel.report.service.dto.CustomerReportDTO;
import vn.viettel.report.service.dto.CustomerTradeDTO;
import vn.viettel.report.service.dto.CustomerTradeTotalDTO;
import vn.viettel.report.service.excel.CustomerNotTradeExcel;
import vn.viettel.report.service.feign.ShopClient;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@RestController
@Api(tags = "API báo cáo khách hàng")
public class    CustomerNotTradeReportController extends BaseController {
    @Autowired
    CustomerNotTradeService service;
    @Autowired
    ShopClient shopClient;

    private final String root = "/reports/customers";

    @GetMapping(V1 + root + "/not-trade")
    public Object getCustomerNotTrade(HttpServletRequest httpRequest, @RequestParam Date fromDate, @RequestParam Date toDate,
                                      @RequestParam Boolean isPaging, Pageable pageable) {
        CustomerNotTradeFilter filter = new CustomerNotTradeFilter(fromDate, toDate, this.getShopId(httpRequest));
        return service.index(filter, isPaging, pageable);
    }

    @GetMapping(value = { V1 + root + "/not-trade/excel"})
    public void exportToExcel(HttpServletRequest request, @RequestParam(required = false) Date fromDate,
                                        @RequestParam(required = false) Date toDate,
            HttpServletResponse response, Pageable pageable) throws IOException{
        ShopDTO shop = shopClient.getShopByIdV1(this.getShopId(request)).getData();
        if(shop == null) throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
        CustomerNotTradeFilter filter = new CustomerNotTradeFilter(fromDate, toDate, this.getShopId(request));
        Response<List<CustomerReportDTO>> listData = (Response<List<CustomerReportDTO>>) service.index(filter, false, pageable);
        CustomerNotTradeExcel exportExcel = new CustomerNotTradeExcel(listData.getData(), shop, shop.getParentShop(), fromDate, toDate);
        this.closeStreamExcel(response, exportExcel.export(), "report_");
        response.getOutputStream().flush();
    }

    @ApiOperation(value = "In danh sách báo cáo khách hàng không giao")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(V1 + root + "/print")
    public Response<CustomerNotTradePrintDTO> print(HttpServletRequest httpRequest, @RequestParam Date fromDate,
                                                    @RequestParam Date toDate){
        CustomerNotTradeFilter filter = new CustomerNotTradeFilter(fromDate, toDate, this.getShopId(httpRequest));
        CustomerNotTradePrintDTO response = service.printCustomerNotTrade(filter);
        return new Response<CustomerNotTradePrintDTO>().withData(response);
    }

    @GetMapping(V1 + root + "/trade")
    @ApiOperation(value = "Danh sách khách hàng có giao dịch")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<CoverResponse<Page<CustomerTradeDTO>, CustomerTradeTotalDTO>> findCustomerTrades(HttpServletRequest request, @ApiParam("Tìm theo mã, họ tên khách hàng") @RequestParam(required = false, defaultValue = "") String keySearch,
                                                             @ApiParam("Tìm theo mã khu vực") @RequestParam(required = false) String areaCode,
                                                             @ApiParam("Tìm theo loại khách hàng") @RequestParam(required = false) Integer customerType,
                                                             @ApiParam("Tìm theo trạng thái khách hàng") @RequestParam(required = false) Integer customerStatus,
                                                             @ApiParam("Tìm theo số điện thoại khách hàng") @RequestParam(required = false, defaultValue = "") String customerPhone,
                                                             @ApiParam("Tìm theo thời gian tạo khách hàng nhỏ nhất") @RequestParam(required = false)  Date fromCreateDate,
                                                             @ApiParam("Tìm theo thời gian tạo khách hàng lớn nhất") @RequestParam(required = false)  Date toCreateDate,
                                                             @ApiParam("Tìm theo thời gian mua hàng nhỏ nhất") @RequestParam(required = false)  Date fromPurchaseDate,
                                                             @ApiParam("Tìm theo thời gian mua hàng lớn nhất") @RequestParam(required = false)  Date toPurchaseDate, Pageable pageable) {

        CustomerTradeFilter filter = new CustomerTradeFilter(this.getShopId(request), keySearch, areaCode, customerType,
                customerStatus, customerPhone).withCreateAt(DateUtils.convertFromDate(fromCreateDate), DateUtils.convertFromDate(toCreateDate))
                .withPurchaseAt(DateUtils.convertFromDate(fromPurchaseDate), DateUtils.convertFromDate(toPurchaseDate));

        CoverResponse<Page<CustomerTradeDTO>, CustomerTradeTotalDTO> response = service.findCustomerTrades(filter, pageable);
        LogFile.logToFile(appName, getUsername(request), LogLevel.INFO, request, LogMessage.FIND_REPORT_CUSTOMER_TRADE_SUCCESS);
        return new Response<CoverResponse<Page<CustomerTradeDTO>, CustomerTradeTotalDTO>>().withData(response);
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
                                                               @ApiParam("Tìm theo thời gian mua hàng lớn nhất") @RequestParam(required = false) Date toPurchaseDate, HttpServletResponse response) throws IOException {
        CustomerTradeFilter filter = new CustomerTradeFilter(this.getShopId(request), keySearch, areaCode, customerType,
                customerStatus, customerPhone).withCreateAt(DateUtils.convertFromDate(fromCreateDate), DateUtils.convertFromDate(toCreateDate))
                .withPurchaseAt(DateUtils.convertFromDate(fromPurchaseDate), DateUtils.convertFromDate(toPurchaseDate));
        this.closeStreamExcel(response, service.customerTradesExportExcel(filter), "filename=Danh_sach_khach_hang_");
        LogFile.logToFile(appName, getUsername(request), LogLevel.INFO, request, LogMessage.EXPORT_EXCEL_CUSTOMER_TRADE_SUCCESS);
        response.getOutputStream().flush();
    }

}
