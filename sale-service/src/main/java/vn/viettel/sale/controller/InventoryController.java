package vn.viettel.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.messaging.Response;

import vn.viettel.sale.messaging.StockCountingFilter;
import vn.viettel.sale.service.InventoryService;

import vn.viettel.sale.service.dto.StockCountingDTO;

@RestController
@RequestMapping("/api/sale/inventory")
public class InventoryController extends BaseController {

    @Autowired
    InventoryService inventoryService;
    @GetMapping
    public Response<Page<StockCountingDTO>> find(@RequestBody StockCountingFilter filter, Pageable pageable) {
        return inventoryService.find(filter,pageable);
    }

}
