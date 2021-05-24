package vn.viettel.report.service;

import vn.viettel.core.dto.common.CategoryDataDTO;
import vn.viettel.core.messaging.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.report.messaging.ExchangeTransFilter;
import vn.viettel.report.service.dto.ExchangeTransTotalDTO;
import vn.viettel.report.service.dto.ExchangeTransReportDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public interface ExchangeTransReportService {
    ByteArrayInputStream exportExcel(ExchangeTransFilter filter) throws IOException;
    Response<List<CategoryDataDTO>> listReasonExchange();
    CoverResponse<Page<ExchangeTransReportDTO>, ExchangeTransTotalDTO> getExchangeTransReport(ExchangeTransFilter filter, Pageable pageable);
}
