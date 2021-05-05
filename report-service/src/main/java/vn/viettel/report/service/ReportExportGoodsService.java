package vn.viettel.report.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.service.dto.ExportGoodsDTO;

import java.util.Date;
import java.util.List;

public interface ReportExportGoodsService {
    Response<Page<ExportGoodsDTO>> index(Date fromExportDate, Date toExportDate
            , Date fromOrderDate, Date toOrderDate, String lstProduct, String lstExportType, String searchKeywords, Pageable pageable);
}
