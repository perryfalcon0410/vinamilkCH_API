package vn.viettel.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.sale.messaging.StockCountingFilter;
import vn.viettel.sale.service.InventoryService;
import vn.viettel.sale.service.dto.StockCountingDTO;
import vn.viettel.sale.service.dto.StockCountingDetailDTO;

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

}
