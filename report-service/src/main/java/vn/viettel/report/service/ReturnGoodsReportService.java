package vn.viettel.report.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.messaging.ReturnGoodsReportsFilter;
import vn.viettel.report.service.dto.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public interface ReturnGoodsReportService {
    Response<CoverResponse<Page<ReturnGoodsDTO>, ReturnGoodsReportTotalDTO>> getReturnGoodsReport(ReturnGoodsReportsFilter filter, Pageable pageable);

    ByteArrayInputStream exportExcel(ReturnGoodsReportsFilter filter) throws IOException;

    Response<PromotionProductReportDTO> getDataPrint(ReturnGoodsReportsFilter filter);
}
