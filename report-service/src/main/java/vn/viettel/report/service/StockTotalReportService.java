package vn.viettel.report.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.report.service.dto.StockTotalInfoDTO;
import vn.viettel.report.service.dto.StockTotalReportDTO;
import vn.viettel.report.service.dto.StockTotalReportPrintDTO;

import java.util.Date;

public interface StockTotalReportService {
    CoverResponse<Page<StockTotalReportDTO>, StockTotalInfoDTO> getStockTotalReport(Date stockDate, String productCodes, Long shopId, Pageable pageable);
    StockTotalReportPrintDTO print(Date stockDate, String productCodes, Long shopId);
}
