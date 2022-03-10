package vn.viettel.sale.controller;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.messaging.OrderProductRequest;
import vn.viettel.sale.messaging.ProductFilter;
import vn.viettel.sale.service.OnlineOrderService;
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

    public void setService(ProductService service){
        if(productService == null) productService = service;
    }

    @GetMapping(value = {V1 + root + "/product-infos"})
    @ApiOperation(value = "Tìm kiếm các ngành hàng trong bán hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<Page<ProductInfoDTO>> findALlProductInfo(HttpServletRequest request,
                                                             @ApiParam("Trạng thái hoạt động")
                                                             @RequestParam(name = "status", required = false) Integer status,
                                                             @ApiParam("Loại ngành hàng")
                                                             @RequestParam(name = "type", required = false) Integer type,
                                                             Pageable pageable) {
        Page<ProductInfoDTO> response = productService.findAllProductInfo(status, type, pageable);
        LogFile.logToFile(appName, getUsername(request), LogLevel.INFO, request, LogMessage.FIND_PRODUCT_INFOS_SUCCESS);
        return new Response<Page<ProductInfoDTO>>().withData(response);
    }

    @GetMapping(value = {V1 + root})
    @ApiOperation(value = "Tìm kiếm sản phẩm, tìm kiếm sản phẩm theo ngành hàng trong bán hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<Page<OrderProductDTO>> findProducts(HttpServletRequest request,
                                                        @ApiParam("Tìm kiếm theo tên hoặc mã sản phẩm")
                                                        @RequestParam(name = "keyWord", required = false, defaultValue = "") String keyWord,
                                                        @ApiParam("Id ngành hàng")
                                                        @RequestParam(name = "catId", required = false) Long productInfoId,
                                                        @ApiParam("Id loại khách hàng")
                                                        @RequestParam(value = "customerId") Long customerId,
                                                        @ApiParam("Trạng thái hoạt động của sản phẩm")
                                                        @RequestParam(name = "status", required = false) Integer status,
                                                        Pageable pageable) {
        if (customerId == null) throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
        ProductFilter productFilter = new ProductFilter(this.getShopId(request), keyWord, customerId, productInfoId, status);
        Page<OrderProductDTO> response = productService.findProducts(productFilter, pageable);
        LogFile.logToFile(appName, getUsername(request), LogLevel.INFO, request, LogMessage.FIND_PRODUCTS_SUCCESS);
        return new Response<Page<OrderProductDTO>>().withData(response);
    }


    @GetMapping(value = {V1 + root + "/top-sale"})
    @ApiOperation(value = "Tìm kiếm sản phẩm bán chạy trong bán hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<Page<OrderProductDTO>> findProductsTopSale(HttpServletRequest request,
                                                               @ApiParam("Tìm kiếm theo tên hoặc mã sản phẩm") @RequestParam(name = "keyWord", required = false) String keyWord,
                                                               @ApiParam("Id khách hàng") @RequestParam("customerId") Long customerId,
                                                               @ApiParam("Kiểm tra tồn kho ") @RequestParam(name = "checkStockTotal", required = false, defaultValue = "1") Integer checkStocktotal,
                                                               @ApiParam("Quét mã vạch ") @RequestParam(name = "checkBarcode", required = false) Boolean checkBarcode,
                                                               Pageable pageable) {
        if (customerId == null) throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
        Page<OrderProductDTO> response = productService.findProductsTopSale(this.getShopId(request), keyWord, customerId, checkStocktotal,checkBarcode, pageable);
        LogFile.logToFile(appName, getUsername(request), LogLevel.INFO, request, LogMessage.FIND_PRODUCTS_SUCCESS);
        return new Response<Page<OrderProductDTO>>().withData(response);
    }

    @GetMapping(value = {V1 + root + "/top-sale/month"})
    @ApiOperation(value = "Tìm kiếm sản phẩm bán chạy trong tháng của cửa hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<Page<OrderProductDTO>> findProductsTopSale(HttpServletRequest request,
                                                               @ApiParam("Id khách hàng")
                                                               @RequestParam("customerId") Long customerId, Pageable pageable) {
        if (customerId == null) throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
        Page<OrderProductDTO> response = productService.findProductsMonth(this.getShopId(request), customerId, pageable);
        LogFile.logToFile(appName, getUsername(request), LogLevel.INFO, request, LogMessage.FIND_PRODUCTS_SUCCESS);
        return new Response<Page<OrderProductDTO>>().withData(response);
    }

    @GetMapping(value = {V1 + root + "/top-sale/customer/{customerId}"})
    @ApiOperation(value = "Danh sách sản phẩm hay mua trong bán hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<Page<OrderProductDTO>> findProductsCustomerTopSale(HttpServletRequest request,
                                                                       @ApiParam("Id khách hàng") @PathVariable Long customerId, Pageable pageable) {
        Page<OrderProductDTO> response = productService.findProductsCustomerTopSale(this.getShopId(request), customerId, pageable);
        LogFile.logToFile(appName, getUsername(request), LogLevel.INFO, request, LogMessage.FIND_PRODUCTS_SUCCESS);
        return new Response<Page<OrderProductDTO>>().withData(response);
    }

    @PostMapping(value = {V1 + root + "/change/customer-type/{customerTypeId}"})
    @ApiOperation(value = "Cập nhật giá của sản phẩm khi đổi loại khách hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<OrderProductsDTO> changeCustomerType(HttpServletRequest request,
                                                         @ApiParam("Id loại khách hàng")
                                                         @PathVariable Long customerTypeId,
                                                         @ApiParam("Danh sách sản phẩm cần đổi lại giá")
                                                         @RequestBody List<OrderProductRequest> products) {
        if(products.isEmpty()) throw new ValidateException(ResponseMessage.PRODUCT_ORDER_NOT_EMPTY);
        OrderProductsDTO response = productService.changeCustomerType(customerTypeId, this.getShopId(request), products);
        LogFile.logToFile(appName, getUsername(request), LogLevel.INFO, request, LogMessage.CHANGE_PRODUCTS_PRICE_SUCCESS);
        return new Response<OrderProductsDTO>().withData(response);
    }

    @GetMapping(value = {V1 + root + "/find"})
    @ApiOperation(value = "Tìm sản phẩm nhập hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<List<OrderProductDTO>> findProductsByKeyWord(HttpServletRequest request, @RequestParam(required = false) String keyWord,
            @RequestParam(required = false) Long customerId) {
        List<OrderProductDTO> response = productService.findProductsByKeyWord(getShopId(request), customerId, keyWord);
        LogFile.logToFile(appName, getUsername(request), LogLevel.INFO, request, LogMessage.FIND_PRODUCTS_SUCCESS);
        return new Response<List<OrderProductDTO>>().withData(response);
    }

    @GetMapping(value = {V1 + root + "/choose-product"})
    @ApiOperation(value = "Chọn sản phẩm")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<Page<ProductDTO>> find(HttpServletRequest request, @RequestParam(value = "productCode", required = false) String productCode,
                                           @RequestParam(value = "productName", required = false) String productName,
                                           @RequestParam(value = "catId", required = false) Long catId, Pageable pageable) {
        Page<ProductDTO> response = productService.findProduct(getShopId(request), productCode, productName, catId, pageable);
        LogFile.logToFile(appName, getUsername(request), LogLevel.INFO, request, LogMessage.FIND_PRODUCTS_SUCCESS);
        return new Response<Page<ProductDTO>>().withData(response);
    }

    @GetMapping(value = {V1 + root + "/all-product-cat"})
    @ApiOperation(value = "Lấy danh sách ngành hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<List<ProductInfoDTO>> getAllProductInfo() {
        return productService.getAllProductCat();
    }

    @GetMapping(value = {V1 + root + "/barcode"})
    @ApiOperation(value = "Tìm theo mã vạch sản phẩm")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<List<OrderProductDTO>> getByBarcode(HttpServletRequest request, @RequestParam String barcode, @RequestParam Long customerId) {
        List<OrderProductDTO> response = productService.getByBarcode(this.getShopId(request), barcode, customerId);
        LogFile.logToFile(appName, getUsername(request), LogLevel.INFO, request, LogMessage.FIND_PRODUCTS_SUCCESS);
        return new Response<List<OrderProductDTO>>().withData(response);
    }
}