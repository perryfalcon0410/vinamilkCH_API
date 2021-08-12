package vn.viettel.report.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.report.messaging.ShopExportFilter;
import vn.viettel.report.messaging.PrintGoodFilter;
import vn.viettel.report.messaging.TotalReport;
import vn.viettel.report.service.dto.PrintShopExportDTO;
import vn.viettel.report.service.dto.ShopExportDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public interface ReportExportGoodsService {

    CoverResponse<Page<ShopExportDTO>, TotalReport> index(ShopExportFilter filter, Pageable pageable);

    ByteArrayInputStream exportExcel(ShopExportFilter shopExportFilter) throws IOException;

    PrintShopExportDTO getDataToPrint(ShopExportFilter filter);
}
