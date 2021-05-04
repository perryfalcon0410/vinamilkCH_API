package vn.viettel.report.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.report.service.StockTotalReportService;
import vn.viettel.report.service.dto.StockTotalReportDTO;

import java.util.List;

@RestController
@RequestMapping("api/v1/reports/stock-total")
public class StockTotalReportController {
    @Autowired
    StockTotalReportService stockTotalReportService;

    @GetMapping
    public List<StockTotalReportDTO> getStockTotalReport(@RequestParam String stockDate) {
        return stockTotalReportService.getStockTotalReport(stockDate);
    }
}
