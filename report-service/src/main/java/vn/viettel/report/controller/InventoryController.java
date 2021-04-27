package vn.viettel.report.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.report.service.InventoryService;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
public class InventoryController extends BaseController {
    private final String root = "/reports/inventories";

    @Autowired
    InventoryService inventoryService;

    @RoleAdmin
    @GetMapping(V1 + root + "/import-export/excel")
    public ResponseEntity exportToExcel() throws IOException {
        ByteArrayInputStream in = inventoryService.exportImportExcel(this.getShopId());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=inventory.xlsx");

        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
    }

}
