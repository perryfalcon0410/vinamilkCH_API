package vn.viettel.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.sale.messaging.RedInvoiceFilter;
import vn.viettel.sale.messaging.SaleOrderFilter;
import vn.viettel.sale.service.RedInvoiceService;
import vn.viettel.sale.service.dto.RedInvoiceDTO;

import vn.viettel.sale.service.SaleOrderService;
import vn.viettel.sale.service.dto.SaleOrderDTO;


@RestController
@RequestMapping("/api/sale")
public class RedInvoiceController extends BaseController {
    @Autowired
    RedInvoiceService redInvoiceService;
    @Autowired
    SaleOrderService saleOrderService;

//    @RoleAdmin
    @GetMapping("/red-invoices")
    public Response<Page<RedInvoiceDTO>> findALlProductInfo(@RequestBody RedInvoiceFilter redInvoiceFilter,
                                                            Pageable pageable) {
        return redInvoiceService.getAll(redInvoiceFilter, pageable);
    }

//    @RoleAdmin
    @GetMapping("/bill-of-sale-list")
    public Response<Page<SaleOrderDTO>> findAllBillOfSaleList(@RequestBody SaleOrderFilter filter, Pageable pageable){
        return saleOrderService.getAllBillOfSaleList(filter, pageable);
    }

}
