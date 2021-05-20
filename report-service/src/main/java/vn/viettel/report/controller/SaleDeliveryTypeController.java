package vn.viettel.report.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.report.messaging.SaleDeliveryTypeFilter;
import vn.viettel.report.service.SaleDeliveryTypeService;

import java.io.ByteArrayInputStream;
import java.util.Date;

@RestController
public class SaleDeliveryTypeController extends BaseController {
    private final String root = "/reports/delivery-type";
    @Autowired
    SaleDeliveryTypeService saleDeliveryTypeService;

    @ApiOperation(value = "Xuất excel báo cáo đổi trả hàng hỏng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(V1 + root + "/excel")
    public ResponseEntity exportToExcel(
            @RequestParam(value = "fromDate", required = false) Date fromDate,
            @RequestParam(value = "toDate", required = false) Date toDate

    ) throws Exception {
        SaleDeliveryTypeFilter filter = new SaleDeliveryTypeFilter(fromDate, toDate, this.getShopId());
        ByteArrayInputStream in = saleDeliveryTypeService.exportExcel(filter);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=sale_delivery.xlsx");
        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
    }
}
