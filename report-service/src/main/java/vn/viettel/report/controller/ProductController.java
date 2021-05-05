package vn.viettel.report.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.report.service.PromotionProductService;
import vn.viettel.report.service.dto.PromotionProductReportDTO;
import vn.viettel.report.service.dto.PromotionProductTotalDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

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
    public Response<CoverResponse<Page<PromotionProductReportDTO>, PromotionProductTotalDTO>> getReportPromotionProducts(
            @RequestParam(value = "onlineNumber", required = false, defaultValue = "") String onlineNumber,
            @RequestParam(value = "fromDate", required = false) Date fromDate,
            @RequestParam(value = "toDate", required = false) Date toDate,
            @RequestParam(value = "productIds", required = false) String productIds, Pageable pageable) {
        return promotionProductService.getReportPromotionProducts(this.getShopId(), onlineNumber, fromDate, toDate, productIds, pageable);
    }

}
