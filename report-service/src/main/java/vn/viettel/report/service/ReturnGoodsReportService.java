package vn.viettel.report.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.messaging.ReturnGoodsReportsFilter;
import vn.viettel.report.service.dto.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public interface ReturnGoodsReportService {
    CoverResponse<Page<ReturnGoodsDTO>, ReportTotalDTO> getReturnGoodsReport(ReturnGoodsReportsFilter filter, Pageable pageable);

    ByteArrayInputStream exportExcel(ReturnGoodsReportsFilter filter) throws IOException;

    CoverResponse<List<ReturnGoodsReportDTO>, ReportTotalDTO> getDataPrint(ReturnGoodsReportsFilter filter);
}
