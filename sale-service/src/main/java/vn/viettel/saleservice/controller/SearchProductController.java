package vn.viettel.saleservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.db.entity.Product;
import vn.viettel.core.db.entity.ProductType;
import vn.viettel.core.messaging.Response;
import vn.viettel.saleservice.service.SearchProductService;

import java.util.List;

@RestController
@RequestMapping("/api/search-product")
public class SearchProductController {
    @Autowired
    SearchProductService searchService;

    @GetMapping("/product-type/all")
    public Response<List<ProductType>> getALlProductType() {
        return searchService.getAllProductType();
    }

    @GetMapping("/get-product-by-type/{proTypeId}")
    public Response<List<Product>> getProductByProductTypeId(@PathVariable long proTypeId) {
        return searchService.getProductByProductTypeId(proTypeId);
    }

    @GetMapping("/get-product-by-name-or-code/{input}")
    public Response<List<Product>> getProductByNameOrCode(@PathVariable String input) {
        return searchService.getProductByNameOrCode(input);
    }
}
