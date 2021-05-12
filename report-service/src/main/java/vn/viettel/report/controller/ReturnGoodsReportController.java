package vn.viettel.report.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.report.messaging.ReturnGoodsReportsFilter;
import vn.viettel.report.service.ReturnGoodsReportService;
import vn.viettel.report.service.dto.ReturnGoodsDTO;
import vn.viettel.report.service.dto.ReturnGoodsReportDTO;
import vn.viettel.report.service.dto.ReportTotalDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@RestController
public class ReturnGoodsReportController extends BaseController {
    private final String root = "/reports/returnGoods";

    @Autowired
    ReturnGoodsReportService returnGoodsReportService;

    @RoleAdmin
    @GetMapping(V1 + root)
    public Response<CoverResponse<Page<ReturnGoodsDTO>, ReportTotalDTO>> getReportReturnGoods(
            @RequestParam(value = "reciept", required = false) String reciept,
            @RequestParam(value = "fromDate", required = false) Date fromDate,
            @RequestParam(value = "toDate", required = false) Date toDate,
            @RequestParam(value = "reason", required = false) String reason,
            @RequestParam(value = "productIds", required = false) String productIds,
            Pageable pageable) {
        ReturnGoodsReportsFilter filter = new ReturnGoodsReportsFilter(this.getShopId(), reciept, fromDate, toDate, reason, productIds);

        return returnGoodsReportService.getReturnGoodsReport(filter, pageable);
    }

    @RoleAdmin
    @GetMapping(V1 + root + "/export")
    public ResponseEntity exportToExcel(
            @RequestParam(value = "reciept", required = false) String reciept,
            @RequestParam(value = "fromDate", required = false) Date fromDate,
            @RequestParam(value = "toDate", required = false) Date toDate,
            @RequestParam(value = "reason", required = false) String reason,
            @RequestParam(value = "productIds", required = false) String productIds) throws IOException {

        ReturnGoodsReportsFilter filter = new ReturnGoodsReportsFilter(this.getShopId(), reciept, fromDate, toDate, reason, productIds);
        ByteArrayInputStream in = returnGoodsReportService.exportExcel(filter);
        HttpHeaders headers = new HttpHeaders();
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        headers.add("Content-Disposition", "attachment; filename=DB_Hang_tra_lai_Filled_" + date + ".xlsx");

        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
    }

    @RoleAdmin
    @GetMapping(V1 + root + "/print")
    public Response<CoverResponse<List<ReturnGoodsReportDTO>, ReportTotalDTO>> getDataPrint(
            @RequestParam(value = "reciept", required = false) String reciept,
            @RequestParam(value = "fromDate", required = false) Date fromDate,
            @RequestParam(value = "toDate", required = false) Date toDate,
            @RequestParam(value = "reason", required = false) String reason,
            @RequestParam(value = "productIds", required = false) String productIds) {
        ReturnGoodsReportsFilter filter = new ReturnGoodsReportsFilter(this.getShopId(), reciept, fromDate, toDate, reason, productIds);

        return returnGoodsReportService.getDataPrint(filter);
    }
}
