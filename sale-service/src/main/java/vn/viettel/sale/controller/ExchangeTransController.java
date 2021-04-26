package vn.viettel.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;

import vn.viettel.core.dto.common.CategoryDataDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.sale.entities.ExchangeTrans;
import vn.viettel.sale.messaging.ExchangeTransRequest;
import vn.viettel.sale.service.ExchangeTranService;
import vn.viettel.sale.service.dto.ExchangeTransDTO;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@RestController
public class ExchangeTransController extends BaseController {
    @Autowired
    ExchangeTranService service;
    private final String root = "/sales/exchangetrans";

    @RoleAdmin
    @GetMapping(value = { V1 + root + "/reasons"})
    public Response<List<CategoryDataDTO>> getAllReason() {
        return service.getReasons();
    }

    @RoleAdmin
    @GetMapping(value = { V1 + root})
    public Response<Page<ExchangeTransDTO>> getAllExchangeTrans(@RequestParam Long formId, @RequestParam Long ctrlId,
                                                                @RequestParam(required = false) String transCode,
                                                                @RequestParam(required = false) Date fromDate,
                                                                @RequestParam(required = false) Date toDate, @RequestParam(required = false) Long reasonId, Pageable pageable) {
        return service.getAllExchange(this.getRoleId(), this.getShopId(), formId, ctrlId,
                transCode, fromDate, toDate, reasonId, pageable);
    }

    @RoleAdmin
    @PostMapping(value = { V1 + root + "/create"})
    public Response<ExchangeTrans> create(@Valid @RequestBody ExchangeTransRequest request) {
        return service.create(request, this.getUserId());
    }
}
