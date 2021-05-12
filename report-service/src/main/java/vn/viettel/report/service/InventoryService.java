package vn.viettel.report.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.messaging.InventoryImportExportFilter;
import vn.viettel.report.service.dto.ImportExportInventoryDTO;
import vn.viettel.report.service.dto.ImportExportInventoryTotalDTO;
import vn.viettel.report.service.dto.PrintInventoryDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public interface InventoryService {
    ByteArrayInputStream exportImportExcel(Long shopId) throws IOException;

    Response<CoverResponse<Page<ImportExportInventoryDTO>, ImportExportInventoryTotalDTO>>
            getReportInventoryImportExport(InventoryImportExportFilter filter, Pageable pageable);

    Response<PrintInventoryDTO> getDataPrint(InventoryImportExportFilter filter);
}
