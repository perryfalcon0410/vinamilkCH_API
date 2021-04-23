//package vn.viettel.report.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.io.InputStreamResource;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import vn.viettel.core.controller.BaseController;
//import vn.viettel.core.db.entity.common.Shop;
//import vn.viettel.core.messaging.Response;
//import vn.viettel.core.security.anotation.RoleAdmin;
//import vn.viettel.report.repository.ShopRepository;
//import vn.viettel.report.service.StockCountingReportService;
//import vn.viettel.report.service.dto.StockCountingReportDTO;
//import vn.viettel.report.excel.StockCountingReportExcelExporter;
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.util.Date;
//import java.util.List;
//
//@RestController
//@RequestMapping("api/report/stock-counting")
//public class StockCountingReportController extends BaseController {
//
//    @Autowired
//    StockCountingReportService service;
//
//    @Autowired
//    ShopRepository shopRepository;
//
//    @PostMapping
//    public Response<Page<StockCountingReportDTO>> find(@RequestParam(required = false) Date countingDate,
//                                                       @RequestParam(required = false) Long productId, Pageable pageable) {
//
//        return service.find(countingDate, productId, pageable);
//    }
//
//    @RoleAdmin
//    @GetMapping(value = "/export")
//    public ResponseEntity excelCustomersReport(@RequestBody List<StockCountingReportDTO> stockCountingList) throws IOException {
//        Shop shop = shopRepository.findById(this.getShopId()).get();
//
//        StockCountingReportExcelExporter stockCountingExcelExporter = new StockCountingReportExcelExporter(stockCountingList, shop);
//        ByteArrayInputStream in = stockCountingExcelExporter.export();
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Disposition", "attachment; filename=customers.xlsx");
//
//        return ResponseEntity
//                .ok()
//                .headers(headers)
//                .body(new InputStreamResource(in));
//    }
//
//}
