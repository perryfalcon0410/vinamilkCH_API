package vn.viettel.report.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.report.messaging.ExchangeTransFilter;
import vn.viettel.report.service.ExchangeTransReportService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

@RestController
public class ExchangeTransController extends BaseController {
    private final String root = "/reports/exchangeTrans";

    @Autowired
    ExchangeTransReportService exchangeTransReportService;

    @ApiOperation(value = "Xuất excel báo cáo đổi trả hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(V1 + root + "/exchange-trans/excel")
    public ResponseEntity exportToExcel(@RequestParam(value = "fromDate", required = false) Date fromDate,
                                        @RequestParam(value = "toDate", required = false) Date toDate
                                        ) throws IOException {
        ExchangeTransFilter filter = new ExchangeTransFilter(fromDate, toDate, this.getShopId());
        ByteArrayInputStream in = exchangeTransReportService.exportExcel(filter);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=exchange_trans.xlsx");

        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
    }
}
