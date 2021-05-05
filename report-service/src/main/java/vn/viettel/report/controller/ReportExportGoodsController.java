package vn.viettel.report.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.report.service.ReportExportGoodsService;
import vn.viettel.report.service.dto.ExportGoodsDTO;

import java.util.Date;

@RestController
public class ReportExportGoodsController extends BaseController {
    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    ReportExportGoodsService reportExportGoodsService;

    private final String root = "/reports/export-goods";

    @RoleAdmin
    @GetMapping(value = { V1 + root})
    public Response<Page<ExportGoodsDTO>> getAllExportGood(@RequestParam(value = "fromExportDate", required = false) Date fromExportDate,
                                                           @RequestParam(value = "toExportDate", required = false) Date toExportDate,
                                                           @RequestParam(value = "fromOrderDate", required = false) Date fromOrderDate,
                                                           @RequestParam(value = "toOrderDate", required = false) Date toOrderDate,
                                                           @RequestParam(value = "lstProduct", required = false) String lstProduct,
                                                           @RequestParam(value = "lstExportType", required = false) String lstExportType,
                                                           @RequestParam(value = "searchKeywords", required = false) String searchKeywords, Pageable pageable) {
        return reportExportGoodsService.index(fromExportDate, toExportDate, fromOrderDate, toOrderDate, lstProduct, lstExportType, searchKeywords, pageable);
    }
}
