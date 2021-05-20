package vn.viettel.sale.controller;

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
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.messaging.*;
import vn.viettel.sale.service.ProductService;
import vn.viettel.sale.service.RedInvoiceService;
import vn.viettel.sale.service.SaleOrderService;
import vn.viettel.sale.service.dto.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@RestController
public class RedInvoiceController extends BaseController {
    @Autowired
    RedInvoiceService redInvoiceService;
    @Autowired
    SaleOrderService saleOrderService;
    @Autowired
    ProductService productService;
    private final String root = "/sales";

    @ApiOperation(value = "Danh sách hóa đơn đỏ")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root + "/red-invoices"})
    public Response<CoverResponse<Page<RedInvoiceDTO>, TotalRedInvoice>> findALlProductInfo(HttpServletRequest httpRequest,
                                                                                            @RequestParam(value = "searchKeywords", required = false) String searchKeywords,
                                                                                            @RequestParam(value = "fromDate", required = false) Date fromDate,
                                                                                            @RequestParam(value = "toDate", required = false) Date toDate,
                                                                                            @RequestParam(value = "invoiceNumber", required = false) String invoiceNumber,
                                                                                            Pageable pageable) {
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.SEARCH_RED_INVOICE_SUCCESS);
        return redInvoiceService.getAll(this.getShopId(), searchKeywords, fromDate, toDate, invoiceNumber, pageable);
    }

    @ApiOperation(value = "Danh sách hóa đơn bán hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root + "/red-invoices/bill-of-sale-list"})
    public Response<Page<SaleOrderDTO>> getAllBillOfSaleList(@RequestParam(value = "searchKeywords", required = false) String searchKeywords,
                                                             @RequestParam(value = "fromDate", required = false) Date fromDate,
                                                             @RequestParam(value = "toDate", required = false) Date toDate,
                                                             @RequestParam(value = "invoiceNumber", required = false) String invoiceNumber,
                                                             Pageable pageable) {
        RedInvoiceFilter redInvoiceFilter = new RedInvoiceFilter(searchKeywords,invoiceNumber,toDate,fromDate);
        return saleOrderService.getAllBillOfSaleList(redInvoiceFilter, pageable);
    }

    @ApiOperation(value = "Danh sách sản phẩm và thông tin người mua hàng từ hóa đơn bán hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root + "/red-invoices/show-invoice-details"})
    public Response<CoverResponse<List<RedInvoiceDataDTO>, TotalRedInvoiceResponse>> getDataInBillOfSale(@RequestParam(value = "orderCodeList", required = false) List<String> orderCodeList) {
        return redInvoiceService.getDataInBillOfSale(orderCodeList, this.getShopId());
    }

    @ApiOperation(value = "Danh sách sản phẩm từ hóa đơn bán hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root + "/red-invoices/show-info-product"})
    public Response<List<ProductDetailDTO>> getAllProductByOrderNumber(@RequestParam(value = "orderCode", required = false) String orderCode){
        return redInvoiceService.getAllProductByOrderNumber(orderCode);
    }

    @ApiOperation(value = "Tạo hóa đơn đỏ")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @PostMapping(value = { V1 + root + "/red-invoices/create"})
    public Response<String> create(@Valid @RequestBody RedInvoiceNewDataDTO redInvoiceNewDataDTO) {
        return redInvoiceService.create(redInvoiceNewDataDTO, this.getUserId(), this.getShopId());
    }

    @ApiOperation(value = "Tìm kiếm sản phẩm")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = {V1 + root + "/red-invoices/search-product"})
    public Response<List<ProductDataSearchDTO>> searchProduct(@RequestParam(value = "keyWord", required = false) String keyWord){
        return productService.findAllProduct(keyWord);
    }


    @ApiOperation(value = "Danh sách in hóa đơn đỏ")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = {V1 + root + "/dvkh-dddt"})
    public Response<List<RedInvoicePrint>> printRedInvoice(HttpServletRequest httpRequest,
                                                           @RequestParam(value = "ids", required = false) List<Long> ids){
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.GET_DATA_PRINT_RED_INVOICE_SUCCESS);
        return redInvoiceService.lstRedInvocePrint(ids);
    }

    @ApiOperation(value = "Xóa hóa đơn đỏ")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @DeleteMapping(value = {V1 + root + "/red-invoices/delete"})
    public Response<String> delete(HttpServletRequest httpRequest,
                                  @RequestParam(value = "ids" , required = false) List<Long> ids){
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.GET_DATA_PRINT_RED_INVOICE_SUCCESS);
        return redInvoiceService.deleteByIds(ids);
    }
}
