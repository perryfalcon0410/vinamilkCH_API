package vn.viettel.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.sale.entities.ProductInfo;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.sale.messaging.ProductFilter;
import vn.viettel.sale.messaging.OrderProductRequest;
import vn.viettel.sale.service.ProductService;
import vn.viettel.sale.service.dto.ComboProductDTO;
import vn.viettel.sale.service.dto.OrderProductsDTO;
import vn.viettel.sale.service.dto.ProductDTO;

import java.util.List;

@RestController
@RequestMapping("/api/sale/products")
public class ProductController extends BaseController {

    @Autowired
    ProductService productService;

    @RoleAdmin
    @GetMapping("/product-infos")
    public Response<Page<ProductInfo>> findALlProductInfo(@RequestParam(name = "status", required = false) Integer status,
                                                          @RequestParam(name = "type", required = false) Integer type,
                                                          Pageable pageable) {
        return productService.findAllProductInfo(status, type, pageable);
    }

    @RoleAdmin
    @GetMapping
    public Response<Page<ProductDTO>> findProducts(@RequestParam(name = "keyWord", required = false, defaultValue = "") String keyWord,
                                                   @RequestParam(name = "catId", required = false) Long productInfoId,
                                                   @RequestParam("customerTypeId") Long customerTypeId,
                                                   @RequestParam(name = "status", required = false) Integer status,
                                                   Pageable pageable) {
        ProductFilter productFilter = new ProductFilter(keyWord, customerTypeId, productInfoId, status);
        return productService.findProducts(productFilter, pageable);
    }

    @RoleAdmin
    @GetMapping("/{id}")
    public Response<ProductDTO> getProduct(@PathVariable Long id, @RequestParam("customerTypeId") Long customerTypeId) {
        return productService.getProduct(id, customerTypeId, this.getShopId());
    }

    @RoleAdmin
    @GetMapping("/top-sale")
    public Response<Page<ProductDTO>> findProductsTopSale(@RequestParam(name = "keyWord", required = false, defaultValue = "") String keyWord,
                                                           @RequestParam("customerTypeId") Long customerTypeId, Pageable pageable) {
        return productService.findProductsTopSale(this.getShopId(), keyWord, customerTypeId, pageable);
    }

    @RoleAdmin
    @PostMapping("/change/customer-type/{customerTypeId}")
    public Response<OrderProductsDTO> changeCustomerType(@PathVariable Long customerTypeId,
                                                         @RequestBody List<OrderProductRequest> products) {
        return productService.changeCustomerType(customerTypeId, this.getShopId(), products);
    }
    @GetMapping("/find")
    public Response<List<ProductDTO>> findProductsByKeyWord(@RequestParam(name = "keyWord", required = false) String keyWord) {
        return productService.findProductsByKeyWord(keyWord);
    }


}
