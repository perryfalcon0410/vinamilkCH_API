package vn.viettel.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.db.entity.common.ProductInfo;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.sale.service.ProductService;
import vn.viettel.sale.service.dto.ProductDTO;

@RestController
@RequestMapping("/api/sale/product")
public class ProductController extends BaseController {

    @Autowired
    ProductService productService;

    @RoleAdmin
    @GetMapping("/info")
    public Response<Page<ProductInfo>> findALlProductInfo(@RequestParam(name = "status", required = false) Integer status,
                                                          @RequestParam(name = "type", required = false) Integer type,
                                                          Pageable pageable) {
        return productService.findAllProductInfo(status, type, pageable);
    }

    @RoleAdmin
    @GetMapping("/find-by-product-info")
    public Response<Page<ProductDTO>> findProductsByProductInfo( @RequestParam("productInfoId") Long productInfoId,
                                                                 @RequestParam("customerTypeId") Long customerTypeId,
                                                                 @RequestParam(value = "status", required = false) Integer status,
                                                                 Pageable pageable) {
        return productService.findProductByProductInfo(productInfoId, customerTypeId, status, pageable);
    }

    @RoleAdmin
    @GetMapping("/{id}")
    public Response<ProductDTO> getProduct(@PathVariable Long id, @RequestParam("customerTypeId") Long customerTypeId) {

        return productService.getProduct(id, customerTypeId);
    }

    @RoleAdmin
    @GetMapping("/find")
    public Response<Page<ProductDTO>> findProductByNameOrCode( @RequestParam(name = "ke(yWord", required = false) String keyWord,
                                                               @RequestParam("customerTypeId") Long customerTypeId,
                                                               @RequestParam(name = "status", required = false) Integer status,
                                                               Pageable pageable) {
        return productService.findProductsByNameOrCode(keyWord, customerTypeId, status, pageable);
    }

    @RoleAdmin
    @GetMapping("/top-sale")
    public  Response<Page<ProductDTO>> findProductsTopSale(@RequestParam(name = "shopId", required = false) Long shopId,
                                                           @RequestParam("customerId") Long customerId, Pageable pageable) {

        return productService.findProductsTopSale(shopId, customerId, pageable);
    }

}
