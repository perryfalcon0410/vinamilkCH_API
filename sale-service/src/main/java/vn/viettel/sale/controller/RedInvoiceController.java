package vn.viettel.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.sale.messaging.*;
import vn.viettel.sale.service.ProductService;
import vn.viettel.sale.service.RedInvoiceService;
import vn.viettel.sale.service.SaleOrderService;
import vn.viettel.sale.service.dto.*;

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

    @GetMapping(value = { V1 + root + "/red-invoices"})
    public Response<CoverResponse<Page<RedInvoiceDTO>, TotalRedInvoice>> findALlProductInfo(@RequestParam(value = "searchKeywords", required = false) String searchKeywords,
                                                                                            @RequestParam(value = "fromDate", required = false) Date fromDate,
                                                                                            @RequestParam(value = "toDate", required = false) Date toDate,
                                                                                            @RequestParam(value = "invoiceNumber", required = false) String invoiceNumber,
                                                                                            Pageable pageable) {
        return redInvoiceService.getAll(this.getShopId(), searchKeywords, fromDate, toDate, invoiceNumber, pageable);
    }

    @GetMapping(value = { V1 + root + "/bill-of-sale-list"})
    public Response<Page<SaleOrderDTO>> getAllBillOfSaleList(@RequestParam(value = "searchKeywords", required = false) String searchKeywords,
                                                             @RequestParam(value = "fromDate", required = false) Date fromDate,
                                                             @RequestParam(value = "toDate", required = false) Date toDate,
                                                             @RequestParam(value = "invoiceNumber", required = false) String invoiceNumber,
                                                             Pageable pageable) {
        RedInvoiceFilter redInvoiceFilter = new RedInvoiceFilter(searchKeywords,invoiceNumber,toDate,fromDate);
        return saleOrderService.getAllBillOfSaleList(redInvoiceFilter, pageable);
    }

    @GetMapping(value = { V1 + root + "/show-invoice-details"})
    public Response<CoverResponse<List<RedInvoiceDataDTO>, TotalRedInvoiceResponse>> getDataInBillOfSale(@RequestParam(value = "orderCodeList", required = false) List<String> orderCodeList) {
        return redInvoiceService.getDataInBillOfSale(orderCodeList, this.getShopId());
    }

    @GetMapping(value = { V1 + root + "/show-info-product"})
    public Response<List<ProductDetailDTO>> getAllProductByOrderNumber(@RequestParam(value = "orderCode", required = false) String orderCode){
        return redInvoiceService.getAllProductByOrderNumber(orderCode);
    }

    @PostMapping(value = { V1 + root + "/create"})
    public Response<Object> create(@Valid @RequestBody RedInvoiceNewDataDTO redInvoiceNewDataDTO) {
        return redInvoiceService.create(redInvoiceNewDataDTO, this.getUserId(), this.getShopId());
    }

    @PostMapping(value = {V1 + root + "/search-product"})
    public Response<List<ProductDataSearchDTO>> searchProduct(@RequestBody ProductRequest request){
        return productService.findAllProduct(request);
    }

    @GetMapping(value = {V1 + root + "/dvkh-dddt"})
    public Response<List<RedInvoicePrint>> printRedInvoice(@RequestParam(value = "ids", required = false) List<Long> ids){
        return redInvoiceService.lstRedInvocePrint(ids);
    }
}
