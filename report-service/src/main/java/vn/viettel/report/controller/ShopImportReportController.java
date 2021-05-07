package vn.viettel.report.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.report.excel.ShopImportReport;
import vn.viettel.report.messaging.ShopImportFilter;
import vn.viettel.report.service.ShopImportReportService;
import vn.viettel.report.service.dto.ShopImportDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/report")
public class ShopImportReportController extends BaseController {
    @Autowired
    ShopImportReportService shopImportReportService;

    @GetMapping
    public Response<Page<ShopImportDTO>> getStockTotalReport(@RequestParam String fromDate,@RequestParam String toDate,@RequestParam(value = "productIds",required = false) String productIds,
                                                             @RequestParam(value = "importType",required = false) Integer importType,@RequestParam(value = "internalNumber",required = false)String internalNumber,
                                                             @RequestParam String fromOrderDate,@RequestParam String toOrderDate,Pageable pageable) {
        ShopImportFilter shopImportFilter = new ShopImportFilter(fromDate, toDate, productIds, importType,internalNumber,fromOrderDate,toOrderDate);
        return shopImportReportService.find(shopImportFilter,pageable);
    }
/*    @RoleAdmin
    @PostMapping(value = { V1 + "/excel"})
    public ResponseEntity exportToExcel( ) throws IOException {


        ShopImportReport shopImportReport = new ShopImportReport();
        ByteArrayInputStream in = shopImportReport.export();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=PoDetail.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(in));
    }*/
}
