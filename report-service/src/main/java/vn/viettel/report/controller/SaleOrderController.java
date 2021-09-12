package vn.viettel.report.controller;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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
import vn.viettel.report.messaging.SaleOrderAmountFilter;
import vn.viettel.report.service.SaleOrderAmountService;
import vn.viettel.report.service.dto.TableDynamicDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@RestController
@Api("API báo cáo doanh số hóa đơn theo khách hàng")
public class SaleOrderController extends BaseController {
    private final String root = "/reports/customers/sales";

    @Autowired
    SaleOrderAmountService saleOrderAmountService;

    @GetMapping(V1 + root + "/amount")
    @ApiOperation(value = "Danh sách dữ liệu báo cáo doanh số hóa đơn theo khách hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<TableDynamicDTO> findAmounts(HttpServletRequest request,
                       @RequestParam(value = "fromDate") Date fromDate,
                       @RequestParam(value = "toDate") Date toDate,
                       @ApiParam("Tìm theo nhóm khách hàng") @RequestParam(value = "customerTypeId", required = false) Long customerTypeId,
                       @ApiParam("Tìm theo họ tên hoặc mã khách hàng") @RequestParam(value = "keySearch", required = false, defaultValue = "") String nameOrCodeCustomer,
                       @ApiParam("Tìm theo số điện thoại của khách hàng") @RequestParam(value = "phoneNumber", required = false, defaultValue = "") String phoneNumber,
                       @ApiParam("Doanh số tối thiểu") @RequestParam(value = "fromAmount", required = false) Double fromAmount,
                       @ApiParam("Doanh số tối đa") @RequestParam(value = "toAmount", required = false) Double toAmount, Pageable pageable) {
        SaleOrderAmountFilter filter = new SaleOrderAmountFilter(this.getShopId(), DateUtils.convertFromDate(fromDate), DateUtils.convertToDate(toDate), customerTypeId, nameOrCodeCustomer, phoneNumber, fromAmount, toAmount);
        // SaleOrderAmountFilter filter = new SaleOrderAmountFilter(this.getShopId(), DateUtils.convertFromDate(fromDate), DateUtils.convertFromDate(toDate), customerTypeId, nameOrCodeCustomer, phoneNumber, fromAmount, toAmount);
        TableDynamicDTO table = saleOrderAmountService.findAmounts(filter, pageable);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.FIND_REPORT_SALE_ORDER_AMOUNT_SUCCESS);
        return new Response<TableDynamicDTO>().withData(table);
    }

    @GetMapping(V1 + root + "/amount/excel")
    @ApiOperation(value = "Xuất excel báo cáo doanh số hóa đơn theo khách hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public void exportAmountExcel(HttpServletRequest request,
                    @RequestParam(value = "fromDate") Date fromDate,
                    @RequestParam(value = "toDate") Date toDate,
                    @ApiParam("Tìm theo nhóm khách hàng") @RequestParam(value = "customerTypeId", required = false) Long customerTypeId,
                    @ApiParam("Tìm theo họ tên hoặc mã khách hàng") @RequestParam(value = "keySearch", required = false, defaultValue = "") String nameOrCodeCustomer,
                    @ApiParam("Tìm theo số điện thoại của khách hàng") @RequestParam(value = "phoneNumber", required = false, defaultValue = "") String phoneNumber,
                    @ApiParam("Doanh số tối thiểu") @RequestParam(value = "fromAmount", required = false) Double fromAmount,
                    @ApiParam("Doanh số tối đa") @RequestParam(value = "toAmount", required = false) Double toAmount,
                                                                        HttpServletResponse response) throws IOException {
        SaleOrderAmountFilter filter = new SaleOrderAmountFilter(this.getShopId(), DateUtils.convertFromDate(fromDate), DateUtils.convertToDate(toDate), customerTypeId, nameOrCodeCustomer, phoneNumber, fromAmount, toAmount);
     //   SaleOrderAmountFilter filter = new SaleOrderAmountFilter(this.getShopId(), DateUtils.convertFromDate(fromDate), DateUtils.convertFromDate(toDate), customerTypeId, nameOrCodeCustomer, phoneNumber, fromAmount, toAmount);
        this.closeStreamExcel(response, saleOrderAmountService.exportExcel(filter), "report_" + StringUtils.createExcelFileName());
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.EXPORT_EXCEL_REPORT_SALE_ORDER_AMOUNT_SUCCESS);
        response.getOutputStream().flush();
    }

}
