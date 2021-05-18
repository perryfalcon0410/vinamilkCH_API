package vn.viettel.report.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.report.service.CustomerNotTradeService;

import java.util.Date;

@RestController
public class CustomerNotTradeReportController extends BaseController {
    @Autowired
    CustomerNotTradeService service;

    private final String root = "/reports/customers";

    @GetMapping(V1 + root + "/not-trade")
    public Object getCustomerNotTrade(@RequestParam Date fromDate, @RequestParam Date toDate,
                                      @RequestParam Boolean isPaging, Pageable pageable) {
        return service.index(fromDate, toDate, isPaging, pageable);
    }
}
