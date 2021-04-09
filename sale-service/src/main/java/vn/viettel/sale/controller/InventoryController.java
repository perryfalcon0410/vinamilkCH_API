package vn.viettel.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.db.entity.stock.StockCountingDetail;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.sale.messaging.StockCountingFilter;
import vn.viettel.sale.service.InventoryService;
import vn.viettel.sale.service.dto.StockCountingExcel;
import vn.viettel.sale.service.dto.StockCountingImportDTO;
import vn.viettel.sale.service.dto.StockCountingDTO;
import vn.viettel.sale.service.dto.StockCountingDetailDTO;

import java.util.List;

@RestController
@RequestMapping("/api/sale")
public class InventoryController extends BaseController {

    @Autowired
    InventoryService inventoryService;

    @RoleAdmin
    @GetMapping
    public Response<Page<StockCountingDTO>> find(@RequestBody StockCountingFilter filter, Pageable pageable) {
        return inventoryService.find(filter,pageable);
    }

    @RoleAdmin
    @GetMapping("/inventories")
    public Response<Page<StockCountingExcel>> getAll(Pageable pageable) {
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

}
