package vn.viettel.report.service;

import org.springframework.data.domain.Pageable;
import vn.viettel.core.dto.common.CategoryDataDTO;
import vn.viettel.report.messaging.ExchangeTransFilter;
import vn.viettel.report.service.dto.ExchangeTransReportDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public interface ExchangeTransReportService {
    ByteArrayInputStream exportExcel(ExchangeTransFilter filter) throws IOException;
    List<CategoryDataDTO> listReasonExchange();
    ExchangeTransReportDTO getExchangeTransReport(ExchangeTransFilter filter, Pageable pageable);
    ExchangeTransReportDTO callProcedure(ExchangeTransFilter filter);
}
