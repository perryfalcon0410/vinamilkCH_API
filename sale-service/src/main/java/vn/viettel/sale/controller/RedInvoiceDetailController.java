package vn.viettel.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.sale.entities.RedInvoiceDetail;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.service.RedInvoiceDetailService;

@RestController
public class RedInvoiceDetailController extends BaseController {
    @Autowired
    RedInvoiceDetailService redInvoiceDetailService;
    private final String root = "/sales";

    @GetMapping(value = { V1 + root + "red-invoice-detail/{id}"})
    public Response<RedInvoiceDetail> getRedInvoiceDetailByRedInvoiceId(@PathVariable(name = "id") Long id) {
        return redInvoiceDetailService.getRedInvoiceDetailByRedInvoiceId(id);
    }
}
