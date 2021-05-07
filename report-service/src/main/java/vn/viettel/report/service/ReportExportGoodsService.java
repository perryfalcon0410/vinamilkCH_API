package vn.viettel.report.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.messaging.TotalReport;
import vn.viettel.report.service.dto.ExportGoodsDTO;

import java.util.Date;
import java.util.List;

public interface ReportExportGoodsService {
    Response<CoverResponse<Page<ExportGoodsDTO>, TotalReport>> index(Long shopId,Date fromExportDate, Date toExportDate
            , Date fromOrderDate, Date toOrderDate, String lstProduct, String lstExportType, String searchKeywords, Pageable pageable);
}
