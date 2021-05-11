package vn.viettel.report.controller;
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
import vn.viettel.report.excel.ShopImportReport;
import vn.viettel.report.messaging.ShopImportFilter;
import vn.viettel.report.service.ShopImportReportService;
import vn.viettel.report.service.dto.ShopImportDTO;
import vn.viettel.report.service.dto.ShopImportTotalDTO;
import vn.viettel.report.service.feign.ShopClient;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping
public class ShopImportReportController extends BaseController {
    private final String root = "/reports/shop-import";
    @Autowired
    ShopImportReportService shopImportReportService;
    @Autowired
    ShopClient shopClient;


    @GetMapping(V1 + root)
    public Response<CoverResponse<Page<ShopImportDTO>, ShopImportTotalDTO>> getStockTotalReport(@RequestParam String fromDate, @RequestParam String toDate, @RequestParam(value = "productIds",required = false) String productIds,
                                                                                                @RequestParam(value = "importType",required = false) Integer importType, @RequestParam(value = "internalNumber",required = false)String internalNumber,
                                                                                                @RequestParam String fromOrderDate, @RequestParam String toOrderDate, Pageable pageable) {
        ShopImportFilter shopImportFilter = new ShopImportFilter(fromDate, toDate, productIds, importType,internalNumber,fromOrderDate,toOrderDate);
        return shopImportReportService.find(shopImportFilter,pageable);
    }
    @PostMapping(value = { V1 + root+ "/excel"})
    public ResponseEntity exportToExcel(@RequestBody ShopImportFilter filter ) throws IOException {
        ShopDTO shop = shopClient.getShopByIdV1(this.getShopId()).getData();
        CoverResponse<List<ShopImportDTO>, ShopImportTotalDTO> data = shopImportReportService.dataExcel(filter).getData();
        ShopImportReport shopImportReport = new ShopImportReport(data,shop,filter);
        ByteArrayInputStream in = shopImportReport.export();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=report.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(in));
    }
}
