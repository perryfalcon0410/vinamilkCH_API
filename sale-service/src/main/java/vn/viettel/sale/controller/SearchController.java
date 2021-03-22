package vn.viettel.saleservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.db.entity.Product;
import vn.viettel.core.db.entity.ProductType;
import vn.viettel.core.db.entity.ReceiptOnline;
import vn.viettel.core.messaging.Response;
import vn.viettel.saleservice.service.SearchProductService;
import vn.viettel.saleservice.service.SearchReceiptOnlineService;

import java.util.Date;

@RestController
@RequestMapping("/api/sale")
public class SearchController {
    @Autowired
    SearchReceiptOnlineService searchReceiptOnlineService;

    @Autowired
    SearchProductService searchProductService;

    @GetMapping("/search/product-type/all")
    public Response<Page<ProductType>> getALlProductType(Pageable pageable) {
        return searchProductService.getAllProductType(pageable);
    }

    @GetMapping("/search/get-product-by-type/{proTypeId}")
    public Response<Page<Product>> getProductByProductTypeId(@PathVariable long proTypeId, Pageable pageable) {
        return searchProductService.getProductByProductTypeId(proTypeId, pageable);
    }

    @GetMapping("/search/get-product-by-name-or-code/{input}")
    public Response<Page<Product>> getProductByNameOrCode(@PathVariable String input, Pageable pageable) {
        return searchProductService.getProductByNameOrCode(input, pageable);
    }

    @GetMapping("/search/get-top-product")
    public Response<Page<Product>> getTopProduct(Pageable pageable) {
        return searchProductService.getTopProduct(pageable);
    }

    @GetMapping("/search/search-receipt-online")
    public Response<Page<ReceiptOnline>> searchReceiptOnline(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "status", required = false) Long status,
            @RequestParam(value = "fromDate", required = false) Date fromDate,
            @RequestParam(value = "toDate", required = false) Date toDate,
            Pageable pageable) {
        return searchReceiptOnlineService.searchReceiptOnline(code, status, fromDate, toDate, pageable);
    }
}
