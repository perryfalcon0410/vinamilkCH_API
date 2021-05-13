package vn.viettel.sale.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.messaging.OrderProductRequest;
import vn.viettel.sale.messaging.ProductFilter;
import vn.viettel.sale.service.ProductService;
import vn.viettel.sale.service.dto.OrderProductDTO;
import vn.viettel.sale.service.dto.OrderProductsDTO;
import vn.viettel.sale.service.dto.ProductDTO;
import vn.viettel.sale.service.dto.ProductInfoDTO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Api(tags = "API sản phẩm sử dụng cho bán hàng, tồn kho")
public class ProductController extends BaseController {

    @Autowired
    ProductService productService;
    private final String root = "/sales/products";

    @GetMapping(value = { V1 + root + "/product-infos"})
    @ApiOperation(value = "Tìm kiếm các ngành hàng trong bán hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<Page<ProductInfoDTO>> findALlProductInfo(HttpServletRequest request,
                                         @RequestParam(name = "status", required = false) Integer status,
                                         @RequestParam(name = "type", required = false) Integer type,
                                         Pageable pageable) {
        Response<Page<ProductInfoDTO>> response = productService.findAllProductInfo(status, type, pageable);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.FIND_PRODUCT_INFOS_SUCCESS);
        return response;
    }

    @GetMapping(value = { V1 + root } )
    @ApiOperation(value = "Tìm kiếm sản phẩm, tìm kiếm sản phẩm theo ngành hàng trong bán hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<Page<OrderProductDTO>> findProducts(HttpServletRequest request,
                                        @RequestParam(name = "keyWord", required = false, defaultValue = "") String keyWord,
                                        @RequestParam(name = "catId", required = false) Long productInfoId,
                                        @RequestParam("customerTypeId") Long customerTypeId,
                                        @RequestParam(name = "status", required = false) Integer status,
                                        Pageable pageable) {
        ProductFilter productFilter = new ProductFilter(keyWord, customerTypeId, productInfoId, status);
        Response<Page<OrderProductDTO>> response = productService.findProducts(productFilter, pageable);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.FIND_PRODUCTS_SUCCESS);
        return response;
    }

    @GetMapping(value = { V1 + root + "/{id}"})
    @ApiOperation(value = "Chọn sản phẩm trong bán hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<OrderProductDTO> getProduct(HttpServletRequest request,
                                    @PathVariable Long id, @RequestParam("customerTypeId") Long customerTypeId) {
        Response<OrderProductDTO> response = productService.getProduct(id, customerTypeId, this.getShopId());
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_PRODUCT_SUCCESS);
        return response;
    }

    @GetMapping(value = { V1 + root + "/top-sale"})
    @ApiOperation(value = "Tìm kiếm sản phẩm bán chạy trong bán hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<Page<OrderProductDTO>> findProductsTopSale(HttpServletRequest request,
                                            @RequestParam(name = "keyWord", required = false, defaultValue = "") String keyWord,
                                            @RequestParam("customerTypeId") Long customerTypeId, Pageable pageable) {
        Response<Page<OrderProductDTO>> response = productService.findProductsTopSale(this.getShopId(), keyWord, customerTypeId, pageable);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.FIND_PRODUCTS_SUCCESS);
        return response;
    }

    @GetMapping(value = { V1 + root + "/top-sale/customer/{customerId}"})
    @ApiOperation(value = "Danh sách sản phẩm hay mua trong bán hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<Page<OrderProductDTO>> findProductsCustomerTopSale(HttpServletRequest request,
                                                                      @PathVariable Long customerId, Pageable pageable) {

        Response<Page<OrderProductDTO>> response = productService.findProductsCustomerTopSale(this.getShopId(), customerId, pageable);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.FIND_PRODUCTS_SUCCESS);
        return response;
    }

    @PostMapping(value = { V1 + root + "/change/customer-type/{customerTypeId}"})
    @ApiOperation(value = "Cập nhật giá của sản phẩm khi đổi loại khách hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<OrderProductsDTO> changeCustomerType(HttpServletRequest request,
                                                         @PathVariable Long customerTypeId,
                                                         @RequestBody List<OrderProductRequest> products) {
        Response<OrderProductsDTO> response = productService.changeCustomerType(customerTypeId, this.getShopId(), products);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.CHANGE_PRODUCTS_PRICE_SUCCESS);
        return response;
    }

    @GetMapping(value = { V1 + root + "/find"})
    @ApiOperation(value = "Tìm sản phẩm nhập hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<List<OrderProductDTO>> findProductsByKeyWord(HttpServletRequest request, @RequestParam(required = false)  String keyWord ) {
        Response<List<OrderProductDTO>> response = productService.findProductsByKeyWord(keyWord);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.FIND_PRODUCTS_SUCCESS);
        return response;
    }
    @GetMapping(value = { V1 + root + "/choose-product"})
    @ApiOperation(value = "Chọn sản phẩm")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<Page<ProductDTO>> find(@RequestParam(value = "productCodes", required = false) String productCodes,
                                           @RequestParam(value ="productName",required = false ) String productName,
                                           @RequestParam(value ="catId",required = false ) Long catId,Pageable pageable) {
        return productService.findProduct(productCodes,productName,catId,pageable);
    }
    @GetMapping(value = { V1 + root + "/all-product-cat"})
    @ApiOperation(value = "Lấy danh sách ngành hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<List<ProductInfoDTO>> getAllProductInfo() {
       return productService.getAllProductCat();
    }
}
