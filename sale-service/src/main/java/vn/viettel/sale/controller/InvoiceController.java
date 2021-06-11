package vn.viettel.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.sale.service.ReportProductTransService;
import vn.viettel.sale.service.dto.ReportProductTransDTO;

@RestController
public class InvoiceController extends BaseController {

    @Autowired
    ReportProductTransService reportProductTransService;

    private final String root = "/sales/invoices";

//    @RoleAdmin
    @GetMapping(V1 + root + "/product-trans/{id}")
    public Response<ReportProductTransDTO> findComboProducts(@PathVariable Long id, @RequestParam Integer receiptType ) {
        return reportProductTransService.getInvoice(this.getShopId(), id, receiptType);
    }

}
