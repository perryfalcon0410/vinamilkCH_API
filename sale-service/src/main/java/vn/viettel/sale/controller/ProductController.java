package vn.viettel.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.db.entity.common.ProductInfo;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.sale.messaging.ProductFilter;
import vn.viettel.sale.messaging.ProductInfoFilter;
import vn.viettel.sale.service.ProductService;
import vn.viettel.sale.service.dto.ProductDTO;

@RestController
@RequestMapping("/api/sale/product")
public class ProductController extends BaseController {

    @Autowired
    ProductService productService;

    @RoleAdmin
    @GetMapping
    public Response<Page<ProductDTO>> findProductByNameOrCode(@RequestBody ProductFilter filter,
                                                              Pageable pageable) {
        return productService.findProductsByNameOrCode(filter, pageable);
    }
    @RoleAdmin
    @GetMapping("/info")
    public Response<Page<ProductInfo>> findALlProductInfo(@RequestBody ProductInfoFilter filter,
                                                          Pageable pageable) {
        return productService.findAllProductInfo(filter, pageable);
    }
    @RoleAdmin
    @GetMapping("/by-info")
    public Response<Page<ProductDTO>> findProductsByProductInfo(@RequestBody ProductInfoFilter filter,
                                                                 Pageable pageable) {
        return productService.findProductByProductInfo(filter, pageable);
    }

    @RoleAdmin
    @GetMapping("/{id}")
    public Response<ProductDTO> getProduct(@PathVariable Long id, @RequestParam("customerTypeId") Long customerTypeId) {

        return productService.getProduct(id, customerTypeId);
    }


    @RoleAdmin
    @GetMapping("/top-sale")
    public  Response<Page<ProductDTO>> findProductsTopSale(@RequestBody ProductFilter filter, Pageable pageable) {

        return productService.findProductsTopSale(filter, pageable);
    }

}
