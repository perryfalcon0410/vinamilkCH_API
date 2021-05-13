package vn.viettel.report.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.messaging.InOutAdjustmentFilter;
import vn.viettel.report.messaging.ShopImportFilter;
import vn.viettel.report.service.InOutAdjustmentService;
import vn.viettel.report.service.ShopImportReportService;
import vn.viettel.report.service.dto.InOutAdjusmentDTO;
import vn.viettel.report.service.dto.ShopImportDTO;
import vn.viettel.report.service.dto.ShopImportTotalDTO;
import vn.viettel.report.service.excel.InOutAdjustmentExcel;
import vn.viettel.report.service.excel.ShopImportExcel;
import vn.viettel.report.service.feign.ShopClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
public class InOutAdjustmentController extends BaseController {
    @Autowired
    ShopClient shopClient;
    @Autowired
    InOutAdjustmentService inOutAdjustmentService;
    private final String root = "/reports/in-out-adjustment";
    @GetMapping(V1 + root )
    @ApiOperation(value = "Xuất excel báo cáo nhập xuất điều chỉnh")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<Page<InOutAdjusmentDTO>> find (@RequestParam String fromDate, @RequestParam String toDate, @RequestParam(value = "productCodes",required = false) String productCodes, Pageable pageable) {
        InOutAdjustmentFilter filter = new InOutAdjustmentFilter(fromDate, toDate, productCodes);
        return inOutAdjustmentService.find(filter,pageable);
    }
    @PostMapping(value = { V1 + root+ "/excel"})
    @ApiOperation(value = "Xuất excel báo cáo nhập xuất điều chỉnh")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public ResponseEntity exportToExcel(@RequestBody InOutAdjustmentFilter filter,Pageable pageable) throws IOException {
        ShopDTO shop = shopClient.getShopByIdV1(this.getShopId()).getData();
        Page<InOutAdjusmentDTO> data = inOutAdjustmentService.find(filter,pageable).getData();
        InOutAdjustmentExcel inOutAdjustmentExcel = new InOutAdjustmentExcel(data.getContent(),shop,filter);
        ByteArrayInputStream in = inOutAdjustmentExcel.export();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=xndc.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(in));
    }
}
