package vn.viettel.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.db.entity.common.CategoryData;
import vn.viettel.core.db.entity.stock.ExchangeTrans;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.sale.messaging.ExchangeTransRequest;
import vn.viettel.sale.service.ExchangeTranService;
import vn.viettel.sale.service.dto.ExchangeTransDTO;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/sale/exchangetrans")
public class ExchangeTransController extends BaseController {
    @Autowired
    ExchangeTranService service;

    @RoleAdmin
    @GetMapping("reasons")
    public Response<List<CategoryData>> getAllReason() {
        return service.getReasons();
    }

    @RoleAdmin
    @GetMapping
    public Response<Page<ExchangeTransDTO>> getAllExchangeTrans(@RequestParam Long formId, @RequestParam Long ctrlId,
                                                                @RequestParam(required = false) String transCode,
                                                                @RequestParam(required = false) Date fromDate,
                                                                @RequestParam(required = false) Date toDate, @RequestParam(required = false) Long reasonId, Pageable pageable) {
        return service.getAllExchange(this.getRoleId(), this.getShopId(), formId, ctrlId,
                transCode, fromDate, toDate, reasonId, pageable);
    }
    @RoleAdmin
    @PostMapping("/create")
    public Response<ExchangeTrans> create(@Valid @RequestBody ExchangeTransRequest request) {
        return service.create(request, this.getUserId());
    }
}
