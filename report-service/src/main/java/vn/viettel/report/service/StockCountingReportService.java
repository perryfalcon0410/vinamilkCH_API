package vn.viettel.report.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.service.dto.StockCountingReportDTO;

import java.util.Date;

public interface StockCountingReportService {
    Response<Page<StockCountingReportDTO>> find (Date countingDate, Long productId, Pageable pageable);
}
