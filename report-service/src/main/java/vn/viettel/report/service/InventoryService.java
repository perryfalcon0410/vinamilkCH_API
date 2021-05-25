package vn.viettel.report.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.report.messaging.InventoryImportExportFilter;
import vn.viettel.report.service.dto.ImportExportInventoryDTO;
import vn.viettel.report.service.dto.ImportExportInventoryTotalDTO;
import vn.viettel.report.service.dto.PrintInventoryDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public interface InventoryService {
    ByteArrayInputStream exportImportExcel(InventoryImportExportFilter filter) throws IOException;

    CoverResponse<Page<ImportExportInventoryDTO>, ImportExportInventoryTotalDTO>
            getReportInventoryImportExport(InventoryImportExportFilter filter, Pageable pageable);

    PrintInventoryDTO getDataPrint(InventoryImportExportFilter filter);
}
