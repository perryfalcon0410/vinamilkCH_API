package vn.viettel.report.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.service.dto.PromotionProductReportDTO;
import vn.viettel.report.service.dto.PromotionProductTotalDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

public interface PromotionProductService {
    ByteArrayInputStream exportExcel(Long shopId) throws IOException;

    Response<CoverResponse<Page<PromotionProductReportDTO>, PromotionProductTotalDTO>> getReportPromotionProducts(
            Long shopId, String onlineNumber, Date fromDate, Date toDate, String productIds, Pageable pageable);
}