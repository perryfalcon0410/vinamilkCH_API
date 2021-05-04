package vn.viettel.report.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.messaging.ShopImportFilter;
import vn.viettel.report.service.ShopImportReportService;
import vn.viettel.report.service.dto.ShopImportDTO;

import java.util.List;

@RestController
@RequestMapping("api/report")
public class ShopImportReportController {
    @Autowired
    ShopImportReportService shopImportReportService;

    @GetMapping
    public Response<Page<ShopImportDTO>> getStockTotalReport(@RequestParam String fromDate,@RequestParam String toDate,@RequestParam(value = "productIds",required = false) List<Long> productIds,
                                                             @RequestParam(value = "importType",required = false) Integer importType,@RequestParam(value = "internalNumber",required = false)String internalNumber,
                                                             @RequestParam String fromOrderDate,@RequestParam String toOrderDate,Pageable pageable) {
        ShopImportFilter shopImportFilter = new ShopImportFilter(fromDate, toDate, productIds, importType,internalNumber,fromOrderDate,toOrderDate);
        return shopImportReportService.find(shopImportFilter,pageable);
    }
}
