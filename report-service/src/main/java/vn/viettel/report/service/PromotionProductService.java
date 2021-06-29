package vn.viettel.report.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.report.messaging.PromotionProductFilter;
import vn.viettel.report.service.dto.PromotionProductDTO;
import vn.viettel.report.service.dto.PromotionProductReportDTO;
import vn.viettel.report.service.dto.PromotionProductTotalDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public interface PromotionProductService {
    /*
     * Xuất excel báo cáo hàng khuyến mãi
     */
    ByteArrayInputStream exportExcel(PromotionProductFilter filter) throws IOException;

    /*
     * Dữ liệu in báo cáo hàng khuyến mãi
     */
    PromotionProductReportDTO getDataPrint(PromotionProductFilter filter);

    /*
     * Danh sách báo cáo hàng khuyến mãi
     */
    CoverResponse<Page<PromotionProductDTO>, PromotionProductTotalDTO> getReportPromotionProducts(
            PromotionProductFilter filter, Pageable pageable);
}