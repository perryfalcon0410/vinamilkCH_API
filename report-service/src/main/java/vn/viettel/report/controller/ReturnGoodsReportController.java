package vn.viettel.report.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.report.messaging.ReturnGoodsReportsFilter;
import vn.viettel.report.service.ReturnGoodsReportService;
import vn.viettel.report.service.dto.ReturnGoodsReportDTO;
import vn.viettel.report.service.dto.ReturnGoodsReportTotalDTO;

import java.util.Date;

@RestController
public class ReturnGoodsReportController extends BaseController {
    private final String root = "/reports/returnGoods";

    @Autowired
    ReturnGoodsReportService getReportPromotionProducts;

    @RoleAdmin
    @GetMapping(V1 + root )
    public Response<CoverResponse<Page<ReturnGoodsReportDTO>, ReturnGoodsReportTotalDTO>> getReportReturnGoods(
            @RequestParam(value = "reciept", required = false) String reciept,
            @RequestParam(value = "fromDate", required = false) Date fromDate,
            @RequestParam(value = "toDate", required = false) Date toDate,
            @RequestParam(value = "reason", required = false) String reason,
            @RequestParam(value = "productIds", required = false) String productIds,
            Pageable pageable) {
        ReturnGoodsReportsFilter filter = new ReturnGoodsReportsFilter(this.getShopId(), reciept, fromDate, toDate, reason, productIds);

        return getReportPromotionProducts.getReturnGoodsReport(filter, pageable);
    }
}
