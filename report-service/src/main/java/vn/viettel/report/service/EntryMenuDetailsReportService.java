package vn.viettel.report.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.messaging.EntryMenuDetailsReportsFilter;
import vn.viettel.report.service.dto.EntryMenuDetailsDTO;
import vn.viettel.report.service.dto.ReportDateDTO;
import vn.viettel.report.service.dto.ReportTotalDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public interface EntryMenuDetailsReportService {
    CoverResponse<Page<EntryMenuDetailsDTO>, ReportTotalDTO> getEntryMenuDetailsReport(EntryMenuDetailsReportsFilter filter, Pageable pageable);

    ByteArrayInputStream exportExcel(EntryMenuDetailsReportsFilter filter) throws IOException;

    CoverResponse<List<EntryMenuDetailsDTO>, ReportDateDTO> getEntryMenuDetails(EntryMenuDetailsReportsFilter filter);

}
