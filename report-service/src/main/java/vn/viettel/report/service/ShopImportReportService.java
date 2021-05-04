package vn.viettel.report.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.messaging.ShopImportFilter;
import vn.viettel.report.service.dto.ShopImportDTO;


public interface ShopImportReportService {
    Response<Page<ShopImportDTO>> find ( ShopImportFilter filter,Pageable pageable);
}
