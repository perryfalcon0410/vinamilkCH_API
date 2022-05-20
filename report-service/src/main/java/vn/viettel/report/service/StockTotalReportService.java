package vn.viettel.report.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import vn.viettel.core.dto.report.ReportStockAggregatedDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.report.messaging.StockTotalFilter;
import vn.viettel.report.service.dto.StockTotalInfoDTO;
import vn.viettel.report.service.dto.StockTotalReportDTO;
import vn.viettel.report.service.dto.StockTotalReportPrintDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface StockTotalReportService {
    CoverResponse<Page<StockTotalReportDTO>, StockTotalInfoDTO> getStockTotalReport(StockTotalFilter filter, Pageable pageable);
    StockTotalReportPrintDTO print(StockTotalFilter filter);
    ByteArrayInputStream exportExcel(StockTotalFilter filter) throws IOException;
    public Long getStockAggregated(Long shopId, Long productId, LocalDate rptDate);
    public Long getImportPast(Long shopId, Long productId, LocalDate rptBegDate, LocalDate rptDate);
    public Long getExportPast(Long shopId, Long productId, LocalDate rptBegDate, LocalDate rptDate);
    public Long getCumulativeConsumption(Long shopId, Long productId, LocalDate rptBegDate, LocalDate rptDate);
}
