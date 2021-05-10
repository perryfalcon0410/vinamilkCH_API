package vn.viettel.report.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.service.ChangePriceReportService;
import vn.viettel.report.service.dto.ChangePriceDTO;
import vn.viettel.report.service.dto.ChangePriceTotalDTO;

import java.text.ParseException;

@RestController
public class ChangePriceReportController extends BaseController {
    private final String root = "/reports/changePrices";

    @Autowired
    ChangePriceReportService service;

    @GetMapping(V1 + root)
    public Response<CoverResponse<Page<ChangePriceDTO>, ChangePriceTotalDTO>> index(@RequestParam(required = false) String code, @RequestParam(required = false) String fromTransDate,
                                                                                    @RequestParam(required = false) String toTransDate, @RequestParam(required = false) String fromOrderDate,
                                                                                    @RequestParam(required = false) String toOrderDate, @RequestParam(required = false) String ids, Pageable pageable) throws ParseException {
        return service.index(code, fromTransDate, toTransDate, fromOrderDate, toOrderDate, ids, pageable);
    }
}
