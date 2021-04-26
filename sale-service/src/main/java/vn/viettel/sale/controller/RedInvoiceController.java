package vn.viettel.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.sale.service.RedInvoiceService;
import vn.viettel.sale.service.SaleOrderService;
import vn.viettel.sale.service.dto.ProductDetailDTO;
import vn.viettel.sale.service.dto.RedInvoiceDTO;
import vn.viettel.sale.service.dto.RedInvoiceDataDTO;
import vn.viettel.sale.service.dto.SaleOrderDTO;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@RestController
public class RedInvoiceController extends BaseController {
    @Autowired
    RedInvoiceService redInvoiceService;
    @Autowired
    SaleOrderService saleOrderService;
    private final String root = "/sales";

    @RoleAdmin
    @GetMapping(value = { V1 + root + "/red-invoices"})
    public Response<Page<RedInvoiceDTO>> findALlProductInfo(@RequestParam(value = "searchKeywords", required = false) String searchKeywords,
                                                            @RequestParam(value = "fromDate", required = false) Date fromDate,
                                                            @RequestParam(value = "toDate", required = false) Date toDate,
                                                            @RequestParam(value = "String", required = false) String invoiceNumber,
                                                            Pageable pageable) {
        return redInvoiceService.getAll(searchKeywords, fromDate, toDate, invoiceNumber, pageable);
    }

    @GetMapping(value = { V1 + root + "/bill-of-sale-list"})
    public Response<Page<SaleOrderDTO>> getAllBillOfSaleList(@RequestParam(value = "searchKeywords", required = false) String searchKeywords,
                                                             @RequestParam(value = "fromDate", required = false) Date fromDate,
                                                             @RequestParam(value = "toDate", required = false) Date toDate,
                                                             @RequestParam(value = "String", required = false) String invoiceNumber,
                                                             Pageable pageable) {
        return saleOrderService.getAllBillOfSaleList(searchKeywords, invoiceNumber, fromDate, toDate, pageable);
    }

    @GetMapping(value = { V1 + root + "/show-invoice-details"})
    public Response<List<RedInvoiceDataDTO>> getDataInBillOfSale(@RequestParam(value = "orderCodeList", required = false) List<String> orderCodeList) {
        return redInvoiceService.getDataInBillOfSale(orderCodeList, this.getShopId());
    }

    @GetMapping(value = { V1 + root + "/show-info-product"})
    public Response<List<ProductDetailDTO>> getAllProductByOrderNumber(@RequestParam(value = "orderCode", required = false) String orderCode){
        return redInvoiceService.getAllProductByOrderNumber(orderCode);
    }

    @PostMapping(value = { V1 + root + "/create"})
    public Response<Object> create(@Valid @RequestBody RedInvoiceDataDTO redInvoiceDataDTO) {
        return redInvoiceService.create(redInvoiceDataDTO, this.getUserId(), this.getShopId());
    }
}
