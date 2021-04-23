//package vn.viettel.report.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.web.bind.annotation.*;
//import vn.viettel.core.controller.BaseController;
//import vn.viettel.core.messaging.Response;
//import vn.viettel.core.security.anotation.RoleAdmin;
//import vn.viettel.report.service.ProductService;
//import vn.viettel.report.service.dto.ProductDTO;
//import vn.viettel.report.service.dto.ProductInfoDTO;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/report/products")
//public class ProductController extends BaseController {
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
//}
