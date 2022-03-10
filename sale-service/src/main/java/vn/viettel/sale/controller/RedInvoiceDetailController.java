package vn.viettel.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.service.ProductService;
import vn.viettel.sale.service.RedInvoiceDetailService;
import vn.viettel.sale.service.dto.RedInvoiceDetailDTO;

import java.util.List;

@RestController
public class RedInvoiceDetailController extends BaseController {
    @Autowired
    RedInvoiceDetailService redInvoiceDetailService;
    private final String root = "/sales";

    public void setService(RedInvoiceDetailService service){
        if(redInvoiceDetailService == null) redInvoiceDetailService = service;
    }

    @GetMapping(value = { V1 + root + "/red-invoice-detail/{id}"})
    public Response<List<RedInvoiceDetailDTO>> getRedInvoiceDetailByRedInvoiceId(@PathVariable(name = "id") Long id) {
        List<RedInvoiceDetailDTO> response = redInvoiceDetailService.getRedInvoiceDetailByRedInvoiceId(id);
        return new Response<List<RedInvoiceDetailDTO>>().withData(response);
    }
}
