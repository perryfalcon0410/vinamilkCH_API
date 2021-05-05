package vn.viettel.report.service;

import vn.viettel.core.messaging.Response;
import vn.viettel.report.service.dto.PromotionProductReportDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public interface PromotionProductService {
    ByteArrayInputStream exportExcel(Long shopId) throws IOException;

    Response<List<PromotionProductReportDTO>> callStoreProcedure(Long shopId, String onlineNumber, Date fromDate, Date toDate, String productIds);
}