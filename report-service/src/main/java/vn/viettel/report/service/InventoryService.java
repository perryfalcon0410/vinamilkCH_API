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
import java.text.ParseException;

public interface InventoryService {
    /*
     * Xuất excel báo cáo xuất nhập tồn
     */
    ByteArrayInputStream exportImportExcel(InventoryImportExportFilter filter) throws IOException;

    /*
     * Danh sách xuất nhập tồn
     */
    CoverResponse<Page<ImportExportInventoryDTO>, ImportExportInventoryTotalDTO>
            getReportInventoryImportExport(InventoryImportExportFilter filter, Pageable pageable);

    /*
     * Dữ liệu in báo cáo xuất nhập tồn
     */
    PrintInventoryDTO getDataPrint(InventoryImportExportFilter filter) throws ParseException;
}
