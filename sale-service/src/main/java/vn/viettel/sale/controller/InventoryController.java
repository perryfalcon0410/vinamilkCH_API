package vn.viettel.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.customer.service.impl.CustomerExcelExporter;
import vn.viettel.sale.messaging.StockCountingFilter;
import vn.viettel.sale.service.InventoryService;
import vn.viettel.sale.service.dto.ExchangeTransExcel;
import vn.viettel.sale.service.dto.StockCountingDTO;
import vn.viettel.sale.service.dto.StockCountingDetailDTO;
import vn.viettel.sale.service.impl.ExchangeTransExporter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/sale/inventory")
public class InventoryController extends BaseController {

    @Autowired
    InventoryService inventoryService;

    @RoleAdmin
    @GetMapping
    public Response<Page<StockCountingDTO>> find(@RequestBody StockCountingFilter filter, Pageable pageable) {
        return inventoryService.find(filter,pageable);
    }

    @RoleAdmin
    @GetMapping("/{id}")
    public Response<Page<StockCountingDetailDTO>> getStockCountingDetails(@PathVariable Long id, Pageable pageable) {
        return inventoryService.getByStockCountingId(id, pageable);
    }

    @RoleAdmin
    @GetMapping("/import-excel/{id}")
    public Response<Page<StockCountingDetailDTO>> importExcel(@PathVariable Long id, @RequestParam String path, Pageable pageable) {
        return inventoryService.importExcel(id, path, pageable);
    }

    @RoleAdmin
    @GetMapping(value = "/export")
    public ResponseEntity stockCountingReport(List<ExchangeTransExcel> listFail) throws IOException {
        List<ExchangeTransExcel> exchangeTransExcels = listFail;

        ExchangeTransExporter exchangeTransExporter = new ExchangeTransExporter(exchangeTransExcels);
        ByteArrayInputStream in = exchangeTransExporter.export();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=Stock_Counting_Filled.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(in));
    }

}
