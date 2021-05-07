package vn.viettel.report.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.messaging.ExportGoodFilter;
import vn.viettel.report.messaging.TotalReport;
import vn.viettel.report.service.dto.ExportGoodsDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public interface ReportExportGoodsService {
    Response<CoverResponse<Page<ExportGoodsDTO>, TotalReport>> index(ExportGoodFilter filter, Pageable pageable);

    ByteArrayInputStream exportExcel(ExportGoodFilter exportGoodFilter) throws IOException;
}
