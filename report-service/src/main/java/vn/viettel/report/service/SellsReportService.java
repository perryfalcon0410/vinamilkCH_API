package vn.viettel.report.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.report.messaging.SellsReportsRequest;
import vn.viettel.report.messaging.UserDataResponse;
import vn.viettel.report.service.dto.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;


public interface SellsReportService {

    CoverResponse<Page<SellDTO>, SellTotalDTO> getSellReport(SellsReportsRequest filter, Pageable pageable);

    ByteArrayInputStream exportExcel(SellsReportsRequest filter) throws IOException;

    CoverResponse<List<SellDTO>, ReportDateDTO> getDataPrint(SellsReportsRequest filter);

    List<UserDataResponse> getDataUser(Long shopId);
}
