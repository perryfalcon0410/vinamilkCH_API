package vn.viettel.sale.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.util.StringUtils;
import vn.viettel.sale.messaging.*;
import vn.viettel.sale.service.ProductService;
import vn.viettel.sale.service.RedInvoiceService;
import vn.viettel.sale.service.SaleOrderService;
import vn.viettel.sale.service.dto.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
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
    @GetMapping(value = {V1 + root + "/red-invoices"})
    public Response<CoverResponse<Page<RedInvoiceDTO>, TotalRedInvoice>> findALlProductInfo(HttpServletRequest httpRequest,
                                                                                            @RequestParam(value = "searchKeywords", required = false) String searchKeywords,
                                                                                            @RequestParam(value = "fromDate") Date fromDate,
                                                                                            @RequestParam(value = "toDate") Date toDate,
                                                                                            @RequestParam(value = "invoiceNumber", required = false) String invoiceNumber,
                                                                                            @SortDefault.SortDefaults({
                                                                                                    @SortDefault(sort = "printDate", direction = Sort.Direction.DESC),
                                                                                                    @SortDefault(sort = "invoiceNumber", direction = Sort.Direction.DESC)
                                                                                            })
                                                                                                    Pageable pageable) {
        CoverResponse<Page<RedInvoiceDTO>, TotalRedInvoice> response = redInvoiceService.getAll(this.getShopId(httpRequest), searchKeywords, fromDate, toDate, invoiceNumber, pageable);
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.SEARCH_RED_INVOICE_SUCCESS);
        return new Response<CoverResponse<Page<RedInvoiceDTO>, TotalRedInvoice>>().withData(response);
    }

    @ApiOperation(value = "Danh sách hóa đơn bán hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = {V1 + root + "/red-invoices/bill-of-sale-list"})
    public Response<Page<SaleOrderDTO>> getAllBillOfSaleList(
            HttpServletRequest httpRequest,
            @ApiParam(value = "Tìm theo tên,số điện thoại khách hàng")
            @RequestParam(value = "searchKeywords", required = false) String searchKeywords,
            @ApiParam(value = "Tìm theo mã hóa đơn ")
            @RequestParam(value = "invoiceNumber", required = false) String invoiceNumber,
            @RequestParam(value = "fromDate", required = false) Date fromDate,
            @RequestParam(value = "toDate", required = false) Date toDate,
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "orderDate", direction = Sort.Direction.DESC),
                    @SortDefault(sort = "orderNumber", direction = Sort.Direction.ASC)
            })
            Pageable pageable) {
        RedInvoiceFilter redInvoiceFilter = new RedInvoiceFilter(searchKeywords, invoiceNumber, DateUtils.convertFromDate(fromDate), DateUtils.convertToDate(toDate));
        Page<SaleOrderDTO> saleOrderDTOS = saleOrderService.getAllBillOfSaleList(redInvoiceFilter, this.getShopId(httpRequest), pageable);
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.SEARCH_RED_INVOICE_SUCCESS);
        return new Response<Page<SaleOrderDTO>>().withData(saleOrderDTOS);
    }

    @ApiOperation(value = "Danh sách sản phẩm và thông tin người mua hàng từ hóa đơn bán hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = {V1 + root + "/red-invoices/show-invoice-details"})
    public Response<CoverResponse<List<RedInvoiceDataDTO>, TotalRedInvoiceResponse>> getDataInBillOfSale(
            HttpServletRequest httpRequest,
            @RequestParam(value = "orderCodeList", required = false) List<String> orderCodeList) {
        CoverResponse<List<RedInvoiceDataDTO>, TotalRedInvoiceResponse> response = redInvoiceService.getDataInBillOfSale(orderCodeList, this.getShopId(httpRequest));
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.GET_DATA_INVOICE_DETAILS_SUCCESS);
        return new Response<CoverResponse<List<RedInvoiceDataDTO>, TotalRedInvoiceResponse>>().withData(response);
    }

    @ApiOperation(value = "Danh sách sản phẩm từ hóa đơn bán hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = {V1 + root + "/red-invoices/show-info-product"})
    public Response<List<ProductDetailDTO>> getAllProductByOrderNumber(
            HttpServletRequest httpRequest,
            @RequestParam(value = "orderCode", required = false) String orderCode) {
        List<ProductDetailDTO> productDetailDTOS = redInvoiceService.getAllProductByOrderNumber(orderCode);
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.GET_DATA_PRODUCT_SUCCESS);
        return new Response<List<ProductDetailDTO>>().withData(productDetailDTOS);
    }

    @ApiOperation(value = "Tạo hóa đơn đỏ")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @PostMapping(value = {V1 + root + "/red-invoices/create"})
    public Response<RedInvoiceDTO> create(
            HttpServletRequest httpRequest,
            @Valid @RequestBody RedInvoiceNewDataDTO redInvoiceNewDataDTO) {
        RedInvoiceDTO invoiceDTO = redInvoiceService.create(redInvoiceNewDataDTO, this.getUserId(httpRequest), this.getShopId(httpRequest));
        Response<RedInvoiceDTO> response = new Response();
        response.setStatusValue("Thêm mới hóa đơn đỏ thành công");
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.CREATE_RED_INVOICE_SUCCESS);
        return response.withData(invoiceDTO);
    }

    @ApiOperation(value = "Tìm kiếm sản phẩm")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = {V1 + root + "/red-invoices/search-product"})
    public Response<List<ProductDataSearchDTO>> searchProduct(
            HttpServletRequest httpRequest,
            @ApiParam(value = "Tìm theo tên, mã")
            @RequestParam(value = "keyWord", required = false) String keyWord) {
        List<ProductDataSearchDTO> productDataSearchDTOS = productService.findAllProduct(getShopId(httpRequest), keyWord);
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.SEARCH_PRODUCT_SUCCESS);
        return new Response<List<ProductDataSearchDTO>>().withData(productDataSearchDTOS);
    }


    @ApiOperation(value = "Xuất excel hóa đơn đỏ")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(V1 + root + "/excel")
    public void exportToExcel(HttpServletRequest httpRequest,
                                        @ApiParam(value = "Ex: 101,102,103,1044")
                                        @RequestParam(value = "ids") String ids,
                                        @ApiParam(value = "1-DVKH, 2-HDDT")
                                        @RequestParam(value = "type") Integer type, HttpServletResponse response) throws IOException {
        this.closeStreamExcel(response, redInvoiceService.exportExcel(ids, type), "Hoa_Don_Vat_");
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.EXPORT_EXCEL_REPORT_VOUCHER_SUCCESS);
        response.getOutputStream().flush();
    }

    @ApiOperation(value = "Xóa hóa đơn đỏ")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @DeleteMapping(value = {V1 + root + "/red-invoices/delete"})
    public Response<ResponseMessage> delete(HttpServletRequest httpRequest,
                                            @RequestParam(value = "ids", required = false) List<Long> ids) {
        ResponseMessage message = redInvoiceService.deleteByIds(ids);
        Response response = new Response();
        response.setStatusValue(message.statusCodeValue());
        response.setStatusCode(message.statusCode());
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.DELETE_RED_INVOICE_SUCCESS);
        return response;
    }

    @PutMapping(value = {V1 + root + "/red-invoices/update"})
    public Response<ResponseMessage> update(@Valid @RequestBody List<RedInvoiceRequest> redInvoiceRequests, HttpServletRequest httpRequest) {
        ResponseMessage message = redInvoiceService.updateRed(redInvoiceRequests, this.getUserId(httpRequest), this.getShopId(httpRequest));
        Response response = new Response();
        response.setStatusValue(message.statusCodeValue());
        response.setStatusCode(message.statusCode());
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.UPDATE_RED_INVOICE_SUCCESS);
        return response;
    }


    @ApiOperation(value = "Thông tin in hóa đơn đỏ")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = {V1 + root + "/red-invoices/print/{id}"})
    public Response<CoverResponse<List<ProductDataResponse>, PrintDataRedInvoiceResponse>> getDataPrint(
            HttpServletRequest httpRequest,
            @PathVariable Long id) {
        CoverResponse<List<ProductDataResponse>, PrintDataRedInvoiceResponse> response = redInvoiceService.getDataPrint(id, this.getShopId(httpRequest));
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.GET_DATA_INVOICE_DETAILS_SUCCESS);
        return new Response<CoverResponse<List<ProductDataResponse>, PrintDataRedInvoiceResponse>>().withData(response);
    }
}
