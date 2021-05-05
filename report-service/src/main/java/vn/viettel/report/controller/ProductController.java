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
import vn.viettel.report.service.ProductService;
import vn.viettel.report.service.PromotionProductService;
import vn.viettel.report.service.dto.ProductDTO;
import vn.viettel.report.service.dto.ProductInfoDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@RestController
public class ProductController extends BaseController {
    private final String root = "/reports/products";

    @Autowired
    PromotionProductService promotionProductService;

//    @Autowired
//    ProductService productService;
//    @RoleAdmin
//    @GetMapping
//    public Response<Page<ProductDTO>> find(@RequestParam(value = "productCodes", required = false) List<String> productCodes,
//                                           @RequestParam(value ="productName",required = false ) String productName,
//                                           @RequestParam(value ="catId",required = false ) Long catId, Pageable pageable) {
//
//        return productService.findProduct(productCodes,productName,catId, pageable);
//    }
//    @RoleAdmin
//    @GetMapping("product-cat")
//    public Response<List<ProductInfoDTO>> getAllProductInfo() {
//        return productService.getAllProductCat();
//    }


    @RoleAdmin
    @GetMapping(V1 + root + "/promotions/excel")
    public ResponseEntity exportToExcel() throws IOException {
        ByteArrayInputStream in = promotionProductService.exportExcel(this.getShopId());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=promotion_product.xlsx");

        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
    }

    @RoleAdmin
    @GetMapping(V1 + root + "/promotions/datas")
    public Response<List<Object>> callStoreProcedure() {
        return promotionProductService.callStoreProcedure(this.getShopId());
    }

}
