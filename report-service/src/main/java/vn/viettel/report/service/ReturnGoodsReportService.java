package vn.viettel.report.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.messaging.ReturnGoodsReportsFilter;
import vn.viettel.report.service.dto.PromotionProductDTO;
import vn.viettel.report.service.dto.PromotionProductTotalDTO;
import vn.viettel.report.service.dto.ReturnGoodsReportDTO;
import vn.viettel.report.service.dto.ReturnGoodsReportTotalDTO;

public interface ReturnGoodsReportService {
    Response<CoverResponse<Page<ReturnGoodsReportDTO>, ReturnGoodsReportTotalDTO>> getReturnGoodsReport(ReturnGoodsReportsFilter filter, Pageable pageable);
}
