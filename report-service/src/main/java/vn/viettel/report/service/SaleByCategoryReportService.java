package vn.viettel.report.service;

import org.springframework.data.domain.Pageable;
import vn.viettel.report.messaging.SaleCategoryFilter;
import vn.viettel.report.service.dto.SalesByCategoryReportDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public interface SaleByCategoryReportService {
    SalesByCategoryReportDTO getSaleByCategoryReport(SaleCategoryFilter filter, Pageable pageable);
    ByteArrayInputStream exportExcel(SaleCategoryFilter filter) throws IOException;
}
