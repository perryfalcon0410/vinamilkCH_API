package vn.viettel.saleservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.db.entity.Product;
import vn.viettel.core.db.entity.ProductType;
import vn.viettel.core.db.entity.ReceiptOnline;
import vn.viettel.core.messaging.Response;
import vn.viettel.saleservice.service.SearchService;
import vn.viettel.saleservice.service.dto.ReceiptSearch;

import java.util.List;

@RestController
@RequestMapping("/api/sale")
public class SearchController {
    @Autowired
    SearchService searchService;

    @GetMapping("/search/product-type/all")
    public Response<List<ProductType>> getALlProductType() {
        return searchService.getAllProductType();
    }

    @GetMapping("/search/get-product-by-type/{proTypeId}")
    public Response<List<Product>> getProductByProductTypeId(@PathVariable long proTypeId) {
        return searchService.getProductByProductTypeId(proTypeId);
    }

    @GetMapping("/search/get-product-by-name-or-code/{input}")
    public Response<List<Product>> getProductByNameOrCode(@PathVariable String input) {
        return searchService.getProductByNameOrCode(input);
    }

    @GetMapping("/search/get-top-product")
    public Response<List<Product>> getTopProduct() {
        return searchService.getTopProduct();
    }

    @GetMapping("/search/search-receipt-online")
    public Response<List<ReceiptOnline>> searchReceiptOnline(@RequestBody ReceiptSearch searchInfo) {
        return searchService.searchReceiptOnline(searchInfo);
    }
}
