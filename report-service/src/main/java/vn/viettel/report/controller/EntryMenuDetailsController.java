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
import vn.viettel.report.messaging.EntryMenuDetailsReportsFilter;
import vn.viettel.report.service.EntryMenuDetailsReportService;
import vn.viettel.report.service.dto.EntryMenuDetailsDTO;
import vn.viettel.report.service.dto.ReportTotalDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@RestController
public class EntryMenuDetailsController extends BaseController {
    private final String root = "/reports/entryMenuDetails";

    @Autowired
    EntryMenuDetailsReportService entryMenuDetailsReportService;

    @RoleAdmin
    @GetMapping(V1 + root)
    public Response<CoverResponse<Page<EntryMenuDetailsDTO>, ReportTotalDTO>> getReportReturnGoods(
            @RequestParam(value = "fromDate", required = false) Date fromDate,
            @RequestParam(value = "toDate", required = false) Date toDate,
            Pageable pageable) {
        EntryMenuDetailsReportsFilter filter = new EntryMenuDetailsReportsFilter(this.getShopId(), fromDate, toDate);

        return entryMenuDetailsReportService.getEntryMenuDetailsReport(filter, pageable);
    }

    @RoleAdmin
    @GetMapping(V1 + root + "/export")
    public ResponseEntity exportToExcel(
            @RequestParam(value = "fromDate", required = false) Date fromDate,
            @RequestParam(value = "toDate", required = false) Date toDate) throws IOException {

        EntryMenuDetailsReportsFilter filter = new EntryMenuDetailsReportsFilter(this.getShopId(), fromDate, toDate);
        ByteArrayInputStream in = entryMenuDetailsReportService.exportExcel(filter);
        HttpHeaders headers = new HttpHeaders();
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        headers.add("Content-Disposition", "attachment; filename=DB_Bang_ke_chi_tiet_hoa_don-nhap_hang_" + date + ".xlsx");

        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
    }
//
//    @RoleAdmin
//    @GetMapping(V1 + root + "/print")
//    public Response<CoverResponse<List<ReturnGoodsReportDTO>, ReturnGoodsReportTotalDTO>> getDataPrint(
//            @RequestParam(value = "reciept", required = false) String reciept,
//            @RequestParam(value = "fromDate", required = false) Date fromDate,
//            @RequestParam(value = "toDate", required = false) Date toDate,
//            @RequestParam(value = "reason", required = false) String reason,
//            @RequestParam(value = "productIds", required = false) String productIds) {
//        ReturnGoodsReportsFilter filter = new ReturnGoodsReportsFilter(this.getShopId(), reciept, fromDate, toDate, reason, productIds);
//
//        return returnGoodsReportService.getDataPrint(filter);
//    }
}
