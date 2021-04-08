package vn.viettel.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.db.entity.common.ProductInfo;
import vn.viettel.core.db.entity.sale.RedInvoice;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.sale.service.RedInvoiceService;
import vn.viettel.sale.service.dto.RedInvoiceDTO;

import java.util.Date;

@RestController
@RequestMapping("/api/sale")
public class RedInvoiceController extends BaseController {
    @Autowired
    RedInvoiceService redInvoiceService;

//    @RoleAdmin
    @GetMapping("/red-invoices")
    public Response<Page<RedInvoiceDTO>> findALlProductInfo(@RequestParam(name = "searchKeywords", required = false) String searchKeywords,
                                                            @RequestParam(name = "fromDate", required = false) Date fromDate,
                                                            @RequestParam(name = "toDate", required = false) Date toDate,
                                                            @RequestParam(name = "invoiceNumber", required = false) String invoiceNumber,
                                                            Pageable pageable) {
        return redInvoiceService.getAll(searchKeywords, fromDate, toDate, invoiceNumber, pageable);
    }
}