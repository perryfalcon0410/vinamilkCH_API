package vn.viettel.report.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import vn.viettel.report.messaging.ExportGoodFilter;
import vn.viettel.report.messaging.PrintGoods;
import vn.viettel.report.messaging.PromotionProductFilter;
import vn.viettel.report.messaging.TotalReport;
import vn.viettel.report.service.ReportExportGoodsService;
import vn.viettel.report.service.dto.ExportGoodsDTO;
import vn.viettel.report.service.dto.PrintGoodDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@RestController
public class ReportExportGoodsController extends BaseController {
    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    ReportExportGoodsService reportExportGoodsService;

    private final String root = "/reports/export-goods";

    @GetMapping(value = { V1 + root})
    public Response<CoverResponse<Page<ExportGoodsDTO>, TotalReport>> getAllExportGood(@RequestParam(value = "fromExportDate", required = false) Date fromExportDate,
                                                                                       @RequestParam(value = "toExportDate", required = false) Date toExportDate,
                                                                                       @RequestParam(value = "fromOrderDate", required = false) Date fromOrderDate,
                                                                                       @RequestParam(value = "toOrderDate", required = false) Date toOrderDate,
                                                                                       @RequestParam(value = "lstProduct", required = false) String lstProduct,
                                                                                       @RequestParam(value = "lstExportType", required = false) String lstExportType,
                                                                                       @RequestParam(value = "searchKeywords", required = false) String searchKeywords, Pageable pageable) {
        ExportGoodFilter exportGoodFilter = new ExportGoodFilter(this.getShopId(), fromExportDate, toExportDate, fromOrderDate,
                toOrderDate, lstProduct, lstExportType, searchKeywords);
        return reportExportGoodsService.index(exportGoodFilter, pageable);
    }

    @RoleAdmin
    @GetMapping(V1 + root + "/excel")
    public ResponseEntity exportToExcel(@RequestParam(value = "fromExportDate", required = false) Date fromExportDate,
                                        @RequestParam(value = "toExportDate", required = false) Date toExportDate,
                                        @RequestParam(value = "fromOrderDate", required = false) Date fromOrderDate,
                                        @RequestParam(value = "toOrderDate", required = false) Date toOrderDate,
                                        @RequestParam(value = "lstProduct", required = false) String lstProduct,
                                        @RequestParam(value = "lstExportType", required = false) String lstExportType,
                                        @RequestParam(value = "searchKeywords", required = false) String searchKeywords) throws IOException {

        ExportGoodFilter exportGoodFilter = new ExportGoodFilter(this.getShopId(), fromExportDate, toExportDate, fromOrderDate,
                toOrderDate, lstProduct, lstExportType, searchKeywords);
        ByteArrayInputStream in = reportExportGoodsService.exportExcel(exportGoodFilter);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=XH_Filled_Date.xlsx");

        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
    }

    @GetMapping(value = { V1 + root + "/print"})
    public Response<CoverResponse<PrintGoods, TotalReport>> getDataToPrint(@RequestParam(value = "fromExportDate", required = false) Date fromExportDate,
                                                                           @RequestParam(value = "toExportDate", required = false) Date toExportDate,
                                                                           @RequestParam(value = "fromOrderDate", required = false) Date fromOrderDate,
                                                                           @RequestParam(value = "toOrderDate", required = false) Date toOrderDate,
                                                                           @RequestParam(value = "lstProduct", required = false) String lstProduct,
                                                                           @RequestParam(value = "lstExportType", required = false) String lstExportType,
                                                                           @RequestParam(value = "searchKeywords", required = false) String searchKeywords) {
        ExportGoodFilter exportGoodFilter = new ExportGoodFilter(this.getShopId(), fromExportDate, toExportDate, fromOrderDate,
                toOrderDate, lstProduct, lstExportType, searchKeywords);
        return reportExportGoodsService.getDataToPrint(exportGoodFilter);
    }
}
