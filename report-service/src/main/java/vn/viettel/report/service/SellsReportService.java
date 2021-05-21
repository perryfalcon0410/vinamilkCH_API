package vn.viettel.report.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.messaging.SellsReportsFilter;
import vn.viettel.report.service.dto.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;


public interface SellsReportService {

    Response<CoverResponse<Page<SellDTO>, SellTotalDTO>> getSellReport(SellsReportsFilter filter, Pageable pageable);

    ByteArrayInputStream exportExcel(SellsReportsFilter filter) throws IOException;

    Response<CoverResponse<List<SellDTO>, ReportDateDTO>> getDataPrint(SellsReportsFilter filter);
}
