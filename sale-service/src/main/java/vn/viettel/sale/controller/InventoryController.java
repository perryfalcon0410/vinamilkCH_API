package vn.viettel.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.db.entity.stock.StockCounting;
import vn.viettel.core.db.entity.stock.StockCountingDetail;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.sale.service.InventoryService;
import vn.viettel.sale.service.dto.StockCountingDTO;
import vn.viettel.sale.service.dto.StockCountingDetailDTO;
import vn.viettel.sale.service.dto.StockCountingExcel;
import vn.viettel.sale.service.dto.StockCountingImportDTO;
import vn.viettel.sale.service.dto.StockCountingDTO;
import vn.viettel.sale.service.dto.StockCountingDetailDTO;
import vn.viettel.sale.service.impl.StockCountingFilledExporter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;


import java.util.Date;

@RestController
@RequestMapping("/api/sale")
public class InventoryController extends BaseController {

    @Autowired
    InventoryService inventoryService;

    @RoleAdmin
    @GetMapping
    public Response<Page<StockCountingDTO>> find(@RequestParam(value = "stockCountingCode",required = false) String stockCountingCode,
             @RequestParam(value = "fromDate",required = false) Date fromDate,
             @RequestParam(value = "toDate",required = false) Date toDate, Pageable pageable) {
        return inventoryService.find(stockCountingCode,fromDate,toDate,pageable);
    }

    @RoleAdmin
    @GetMapping("/inventories")
    public Response<Page<StockCountingDetailDTO>> getAll(Pageable pageable) {
        return inventoryService.getAll(pageable);
    }

    @RoleAdmin
    @GetMapping("/inventory/{id}")
    public Response<Page<StockCountingDetailDTO>> getStockCountingDetails(@PathVariable Long id, Pageable pageable) {
        return inventoryService.getByStockCountingId(id, pageable);
    }

    @RoleAdmin
    @GetMapping("/inventory/import-excel")
    public Response<StockCountingImportDTO> importExcel(@RequestBody List<StockCountingDetailDTO> stockCountingDetails,
                                                        @RequestParam String path) {
        return inventoryService.importExcel(stockCountingDetails, path);
    }

    @RoleAdmin
    @PutMapping("/inventory/{id}")
    public Response<List<StockCountingDetail>> updateStockCounting(@PathVariable Long id,
                                                                   @RequestBody List<StockCountingDetailDTO> details) {
        return inventoryService.updateStockCounting(id, this.getUserId(), details);
    }

    @RoleAdmin
    @GetMapping(value = "/filled-stock/export")
    public ResponseEntity stockCountingReport(@RequestBody List<StockCountingExcel> listFail) throws IOException {
        List<StockCountingExcel> stockCountingExcels = listFail;

        StockCountingFilledExporter stockCountingFilledExporter = new StockCountingFilledExporter(stockCountingExcels);
        ByteArrayInputStream in = stockCountingFilledExporter.export();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=Stock_Counting_Filled.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(in));
    }

    @RoleAdmin
    @PostMapping("inventory")
    public StockCounting createStockCounting(@RequestBody List<StockCountingDetailDTO> stockCountingDetails,
                                             @RequestParam Boolean override) {
        return inventoryService.createStockCounting(stockCountingDetails, this.getUserId(), this.getShopId(), override);
    }

}
