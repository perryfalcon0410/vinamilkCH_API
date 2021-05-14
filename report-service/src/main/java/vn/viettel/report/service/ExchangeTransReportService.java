package vn.viettel.report.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.messaging.ExchangeTransFilter;
import vn.viettel.report.messaging.ExchangeTransTotal;
import vn.viettel.report.service.dto.ExchangeTransReportDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public interface ExchangeTransReportService {
    ByteArrayInputStream exportExcel(ExchangeTransFilter filter) throws IOException;
    Response<CoverResponse<Page<ExchangeTransReportDTO>, ExchangeTransTotal>> getExchangeTransReport(ExchangeTransFilter filter, Pageable pageable);
}
