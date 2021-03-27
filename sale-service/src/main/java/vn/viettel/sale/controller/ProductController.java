package vn.viettel.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.db.entity.common.Product;
import vn.viettel.core.db.entity.common.ProductInfo;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.service.ProductService;
import vn.viettel.sale.service.dto.ProductDTO;

@RestController
@RequestMapping("/api/sale")
public class ProductController extends BaseController {

    @Autowired
    ProductService productService;

    @GetMapping("/product-info/all")
    public Response<Page<ProductInfo>> findALlProductInfo(@RequestParam(name = "status", required = false) Integer status,
                                                          @RequestParam(name = "type", required = false) Integer type,
                                                          Pageable pageable) {
        return productService.findAllProductInfo(status, type, pageable);
    }

    @GetMapping("/product/find-by-product-info")
    public Response<Page<ProductDTO>> findProductsByProductInfo( @RequestParam("productInfoId") Long productInfoId,
                                                                 @RequestParam("customerTypeId") Long customerTypeId,
                                                                 @RequestParam(value = "status", required = false) Integer status,
                                                                 Pageable pageable) {
        return productService.findProductByProductInfo(productInfoId, customerTypeId, status, pageable);
    }

    @GetMapping("/product/find")
    public Response<Page<ProductDTO>> findProductByNameOrCode( @RequestParam(name = "keyWord", required = false) String keyWord,
                                                               @RequestParam("customerTypeId") Long customerTypeId,
                                                               @RequestParam(name = "status", required = false) Integer status,
                                                               Pageable pageable) {
        return productService.findProductsByNameOrCode(keyWord, customerTypeId, status, pageable);
    }

//    @GetMapping("/search/search-receipt-online")
//    public Response<Page<ReceiptOnline>> searchReceiptOnline(
//            @RequestParam(value = "code", required = false) String code,
//            @RequestParam(value = "status", required = false) Long status,
//            @RequestParam(value = "fromDate", required = false) Date fromDate,
//            @RequestParam(value = "toDate", required = false) Date toDate,
//            Pageable pageable) {
//        return searchReceiptOnlineService.searchReceiptOnline(code, status, fromDate, toDate, pageable);
//    }
}
