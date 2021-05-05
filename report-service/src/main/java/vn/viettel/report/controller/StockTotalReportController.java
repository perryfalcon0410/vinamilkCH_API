package vn.viettel.report.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.report.service.StockTotalReportService;
import vn.viettel.report.service.dto.StockTotalReportDTO;

import java.util.List;

@RestController
public class StockTotalReportController extends BaseController {
    @Autowired
    StockTotalReportService stockTotalReportService;

    private final String root = "/reports/stock-total";

    @GetMapping(value = { V1 + root})
    public List<StockTotalReportDTO> getStockTotalReport(@RequestParam String stockDate) {
        return stockTotalReportService.getStockTotalReport(stockDate);
    }
}
