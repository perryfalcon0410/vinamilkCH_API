package vn.viettel.report.controller;

import io.swagger.annotations.*;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import vn.viettel.report.messaging.QuantitySalesReceiptFilter;
import vn.viettel.report.service.QuantitySalesReceiptService;
import vn.viettel.report.service.dto.TableDynamicDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

@RestController
@Api("API báo cáo số lượng hóa đơn theo khách hàng")
public class QuantitySalesReceiptController extends BaseController{

        private final String root = "/reports/customers/quantity";

        @Autowired
        QuantitySalesReceiptService quantitySalesReceiptService;

    @GetMapping(V1 + root)
    @ApiOperation(value = "Danh sách dữ liệu báo cáo số hóa đơn theo khách hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<TableDynamicDTO> findQuantity(HttpServletRequest request,
                                                 @RequestParam(value = "fromDate") Date fromDate,
                                                 @RequestParam(value = "toDate") Date toDate,
                                                 @ApiParam("Tìm theo nhóm khách hàng") @RequestParam(value = "customerTypeId", required = false) Long customerTypeId,
                                                 @ApiParam("Tìm theo họ tên hoặc mã khách hàng") @RequestParam(value = "keySearch", required = false, defaultValue = "") String nameOrCodeCustomer,
                                                 @ApiParam("Tìm theo số điện thoại của khách hàng") @RequestParam(value = "phoneNumber", required = false, defaultValue = "") String phoneNumber,
                                                 @ApiParam("Số hóa đơn tối thiểu") @RequestParam(value = "fromQuantity", required = false) Long fromQuantity,
                                                 @ApiParam("Số hóa đơn tối đa") @RequestParam(value = "toQuantity", required = false) Long toQuantity, Pageable pageable) {
        QuantitySalesReceiptFilter filter = new QuantitySalesReceiptFilter(this.getShopId(), DateUtils.convertFromDate(fromDate), DateUtils.convertToDate(toDate), customerTypeId, nameOrCodeCustomer, phoneNumber, fromQuantity, toQuantity);
        TableDynamicDTO table = quantitySalesReceiptService.findQuantity(filter, pageable);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.FIND_REPORT_SALE_ORDER_AMOUNT_SUCCESS);
        return new Response<TableDynamicDTO>().withData(table);
    }

    @GetMapping(V1 + root + "/excel")
    @ApiOperation(value = "Xuất excel báo cáo số hóa đơn theo khách hàng")
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
                                  @ApiParam("Số hóa đơn tối thiểu") @RequestParam(value = "fromQuantity", required = false) Long fromQuantity,
                                  @ApiParam("Số hóa đơn tối đa") @RequestParam(value = "toQuantity", required = false) Long toQuantity,
                                  HttpServletResponse response) throws IOException {
        QuantitySalesReceiptFilter filter = new QuantitySalesReceiptFilter(this.getShopId(), DateUtils.convertFromDate(fromDate), DateUtils.convertToDate(toDate), customerTypeId, nameOrCodeCustomer, phoneNumber, fromQuantity, toQuantity);
        this.closeStreamExcel(response, quantitySalesReceiptService.exportExcel(filter), "report_" + StringUtils.createExcelFileName());
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.EXPORT_EXCEL_REPORT_SALE_ORDER_AMOUNT_SUCCESS);
        response.getOutputStream().flush();
    }

}
