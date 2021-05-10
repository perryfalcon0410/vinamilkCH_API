package vn.viettel.report.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.messaging.ShopImportFilter;
import vn.viettel.report.service.dto.ShopImportDTO;
import vn.viettel.report.service.dto.ShopImportTotalDTO;

import java.util.List;


public interface ShopImportReportService {
    Response<CoverResponse<Page<ShopImportDTO>, ShopImportTotalDTO>> find (ShopImportFilter filter, Pageable pageable);
    Response<List<ShopImportDTO>> callProcedure(ShopImportFilter filter);
}
