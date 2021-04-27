package vn.viettel.report.service;

import vn.viettel.report.service.dto.StockTotalReportDTO;

import java.util.List;

public interface StockTotalReportService {
    List<StockTotalReportDTO> getStockTotalReport(String stockDate);
}
