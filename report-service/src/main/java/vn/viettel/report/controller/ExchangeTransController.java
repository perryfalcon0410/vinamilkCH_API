package vn.viettel.report.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.common.CategoryDataDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.util.DateUtils;
import vn.viettel.report.messaging.ExchangeTransFilter;
import vn.viettel.report.service.dto.ExchangeTransTotalDTO;
import vn.viettel.report.service.ExchangeTransReportService;
import vn.viettel.report.service.dto.ExchangeTransReportDTO;
import vn.viettel.report.service.dto.TableDynamicDTO;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@RestController
public class ExchangeTransController extends BaseController {
    private final String root = "/reports/exchange-trans";

    @Autowired
    ExchangeTransReportService exchangeTransReportService;

    @ApiOperation(value = "Xuất excel báo cáo đổi trả hàng hỏng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
    @ApiResponse(code = 400, message = "Bad request"),
    @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(V1 + root + "/excel")
    public ResponseEntity exportToExcel(@RequestParam(value = "transCode", required = false) String transCode,
                                        @RequestParam(value = "fromDate", required = false) Date fromDate,
                                        @RequestParam(value = "toDate", required = false) Date toDate,
                                        @RequestParam(value = "reason", required = false) String reason,
                                        @RequestParam(value = "productKW", required = false) String productKW
                                        ) throws IOException {
        ExchangeTransFilter filter = new ExchangeTransFilter(transCode, DateUtils.convert2Local(fromDate), DateUtils.convert2Local(toDate), reason, productKW, this.getShopId());
        ByteArrayInputStream in = exchangeTransReportService.exportExcel(filter);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=exchange_trans.xlsx");
        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
    }

    @ApiOperation(value = "Danh sách hàng hỏng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
    @ApiResponse(code = 400, message = "Bad request"),
    @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(V1 + root)
    public Response<TableDynamicDTO> getReportExchangeTrans (
                                        HttpServletRequest request,
                                        @RequestParam(value = "transCode", required = false) String transCode,
                                        @RequestParam(value = "fromDate", required = false) Date fromDate,
                                        @RequestParam(value = "toDate", required = false) Date toDate,
                                        @RequestParam(value = "reason", required = false) String reason,
                                        @RequestParam(value = "productKW", required = false) String productKW, Pageable pageable) {
        ExchangeTransFilter filter = new ExchangeTransFilter(transCode, DateUtils.convert2Local(fromDate), DateUtils.convert2Local(toDate), reason, productKW, this.getShopId());
        TableDynamicDTO response = exchangeTransReportService.getExchangeTransReport(filter, pageable);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.FIND_REPORT_PROMOTION_PRODUCTS_SUCCESS);
        return new Response<TableDynamicDTO>().withData(response);
    }

    @ApiOperation(value = "Danh sách lý do trả hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(V1 + root + "/reason-exchange")
    public Response<List<CategoryDataDTO>> listReasonExchange() {
        Response<List<CategoryDataDTO>> response = new Response<>();
        return response.withData(exchangeTransReportService.listReasonExchange());
    }

    @ApiOperation(value = "Danh sách hàng hỏng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(V1 + root + "/testCall")
    public Response<TableDynamicDTO> testCall (
            HttpServletRequest request,
            @RequestParam(value = "transCode", required = false) String transCode,
            @RequestParam(value = "fromDate", required = false) Date fromDate,
            @RequestParam(value = "toDate", required = false) Date toDate,
            @RequestParam(value = "reason", required = false) String reason,
            @RequestParam(value = "productKW", required = false) String productKW) {
        ExchangeTransFilter filter = new ExchangeTransFilter(transCode, DateUtils.convert2Local(fromDate), DateUtils.convert2Local(toDate), reason, productKW, this.getShopId());
        Response<TableDynamicDTO> response = new Response<>();
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.FIND_REPORT_PROMOTION_PRODUCTS_SUCCESS);
        return response.withData(exchangeTransReportService.callProcedure(filter));
    }
}
