package vn.viettel.report.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.messaging.ChangePriceReportRequest;
import vn.viettel.report.service.ChangePriceReportService;
import vn.viettel.report.service.dto.ChangePriceDTO;
import vn.viettel.report.service.dto.ChangePriceTotalDTO;
import vn.viettel.report.service.excel.ChangePriceReportExcel;
import vn.viettel.report.service.feign.ShopClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;

@RestController
public class ChangePriceReportController extends BaseController {
    private final String root = "/reports/changePrices";

    @Autowired
    ChangePriceReportService service;
    @Autowired
    ShopClient shopClient;

    @GetMapping(V1 + root)
    public Response<CoverResponse<Page<ChangePriceDTO>, ChangePriceTotalDTO>> index(@RequestParam(required = false) String code, @RequestParam(required = false) String fromTransDate,
                                                                                    @RequestParam(required = false) String toTransDate, @RequestParam(required = false) String fromOrderDate,
                                                                                    @RequestParam(required = false) String toOrderDate, @RequestParam(required = false) String ids, Pageable pageable) throws ParseException {
        return service.index(code, fromTransDate, toTransDate, fromOrderDate, toOrderDate, ids, pageable);
    }

    @PostMapping(value = { V1 + root + "/excel"})
    public ResponseEntity exportToExcel(@RequestBody ChangePriceReportRequest input) throws IOException {
        ShopDTO shop = shopClient.getShopByIdV1(this.getShopId()).getData();
        ChangePriceReportExcel exportExcel = new ChangePriceReportExcel(input, shop);

        ByteArrayInputStream in = exportExcel.export();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=report.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(in));
    }
}
