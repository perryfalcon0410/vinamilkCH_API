package vn.viettel.report.controller;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
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
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.StringUtils;
import vn.viettel.report.messaging.QuantitySalesReceiptFilter;
import vn.viettel.report.service.QuantitySalesReceiptService;
import vn.viettel.report.service.dto.TableDynamicDTO;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

@RestController
@Api("API báo cáo số lượng hóa đơn theo khách hàng")
public class QuantitySalesReceiptController extends BaseController{

        private final String root = "/reports/customers/quantity";

        @Autowired
        QuantitySalesReceiptService quantitySalesReceiptService;

        @GetMapping(V1 + root )
        @ApiOperation(value = "Danh sách dữ liệu báo cáo số lượng số hóa đơn theo khách hàng")
        @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
                @ApiResponse(code = 400, message = "Bad request"),
                @ApiResponse(code = 500, message = "Internal server error")}
        )
        public Response<TableDynamicDTO> findAmounts(HttpServletRequest request,
                                                     @RequestParam(value = "fromDate", required = false) Date fromDate,
                                                     @RequestParam(value = "toDate", required = false) Date toDate,
                                                     @ApiParam("Tìm theo nhóm khách hàng") @RequestParam(value = "customerTypeId", required = false, defaultValue = "") Long customerTypeId,
                                                     @ApiParam("Tìm theo họ tên hoặc mã khách hàng") @RequestParam(value = "keySearch", required = false, defaultValue = "") String nameOrCodeCustomer,
                                                     @ApiParam("Tìm theo số điện thoại của khách hàng") @RequestParam(value = "phoneNumber", required = false, defaultValue = "") String phoneNumber,
                                                     @ApiParam("Doanh số tối thiểu") @RequestParam(value = "fromAmount", required = false) Float fromQuantity,
                                                     @ApiParam("Doanh số tối đa") @RequestParam(value = "toAmount", required = false) Float toQuantity, Pageable pageable) {
            QuantitySalesReceiptFilter filter = new QuantitySalesReceiptFilter(this.getShopId(), DateUtils.convert2Local(fromDate), DateUtils.convert2Local(toDate), customerTypeId, nameOrCodeCustomer, phoneNumber, fromQuantity, toQuantity);
            TableDynamicDTO table = quantitySalesReceiptService.findQuantity(filter, pageable);
            LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.FIND_REPORT_SALE_ORDER_AMOUNT_SUCCESS);
            return new Response<TableDynamicDTO>().withData(table);
        }

        @GetMapping(V1 + root + "/excel")
        @ApiOperation(value = "Xuất excel báo cáo số lượng hóa đơn theo khách hàng")
        @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
                @ApiResponse(code = 400, message = "Bad request"),
                @ApiResponse(code = 500, message = "Internal server error")}
        )
        public ResponseEntity exportAmountExcel(HttpServletRequest request,
                                                @RequestParam(value = "fromDate", required = false) Date fromDate,
                                                @RequestParam(value = "toDate", required = false) Date toDate,
                                                @ApiParam("Tìm theo nhóm khách hàng") @RequestParam(value = "customerTypeId", required = false, defaultValue = "") Long customerTypeId,
                                                @ApiParam("Tìm theo họ tên hoặc mã khách hàng") @RequestParam(value = "keySearch", required = false, defaultValue = "") String nameOrCodeCustomer,
                                                @ApiParam("Tìm theo số điện thoại của khách hàng") @RequestParam(value = "phoneNumber", required = false, defaultValue = "") String phoneNumber,
                                                @ApiParam("Doanh số tối thiểu") @RequestParam(value = "fromAmount", required = false) Float fromQuantity,
                                                @ApiParam("Doanh số tối đa") @RequestParam(value = "toAmount", required = false) Float toQuantity) throws IOException {
            QuantitySalesReceiptFilter filter = new QuantitySalesReceiptFilter(this.getShopId(), DateUtils.convert2Local(fromDate), DateUtils.convert2Local(toDate), customerTypeId, nameOrCodeCustomer, phoneNumber, fromQuantity, toQuantity);

            ByteArrayInputStream in = quantitySalesReceiptService.exportExcel(filter);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=sale_order" + StringUtils.createExcelFileName());
            LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.EXPORT_EXCEL_REPORT_SALE_ORDER_AMOUNT_SUCCESS);
            return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
        }

}
