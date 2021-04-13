package vn.viettel.report.controller;

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
import vn.viettel.report.messaging.ProductImportRequest;
import vn.viettel.report.service.ProductService;
import vn.viettel.report.service.dto.ProductDTO;
import vn.viettel.report.service.impl.ExportExcel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/report/products")
public class ProductController extends BaseController {
    @Autowired
    ProductService productService;
    @RoleAdmin
    @GetMapping
    public Response<Page<ProductDTO>> find(@RequestParam(value = "productCodes", required = false) List<String> productCodes, Pageable pageable) {

        return productService.findProduct(productCodes, pageable);
    }
    @RoleAdmin
    @GetMapping("/export-excel")
    public ResponseEntity exportToExcel() throws IOException {
        ExportExcel exportExcel = new ExportExcel();
        ByteArrayInputStream in = exportExcel.export();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=teamplate.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(in));
    }
    @RoleAdmin
    @PostMapping("/import-excel")
    public Response<List<ProductImportRequest>> importExcel(@RequestParam String path) {
        return productService.importExcel(path);
    }
}
