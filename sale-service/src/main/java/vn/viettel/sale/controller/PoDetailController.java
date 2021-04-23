package vn.viettel.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.sale.entities.PoDetail;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.service.PoDetailService;

import javax.validation.Valid;

@RestController
@RequestMapping("api/sale/po-details")
public class PoDetailController extends BaseController {
    @Autowired
    PoDetailService poDetailService;

    @GetMapping("/po-confirm-id/{id}")
    public Response<Page<PoDetail>> getAllbyPoConFirmId(@Valid  @PathVariable Long id, Pageable pageable)
    {
        return poDetailService.getAllByPoConfirmId(id, pageable);
    }

}
