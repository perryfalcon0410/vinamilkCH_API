package vn.viettel.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.sale.messaging.ComboProductTranFilter;
import vn.viettel.sale.messaging.ComboProductTranRequest;
import vn.viettel.sale.messaging.TotalResponse;
import vn.viettel.sale.service.ComboProductTransService;
import vn.viettel.sale.service.dto.ComboProductTranDTO;

import javax.validation.Valid;
import java.util.Date;

@RestController
public class ComboProductTransController extends BaseController {

    @Autowired
    ComboProductTransService comboProductTransService;
    private final String root = "/sales/combo-product-trans";

    @RoleAdmin
    @GetMapping(value = { V1 + root })
    public Response<CoverResponse<Page<ComboProductTranDTO>, TotalResponse>> getComboProductTrans(@RequestParam(value = "transCode", required = false, defaultValue = "") String transCode,
                                                                                           @RequestParam(value = "transType", required = false) Integer transType,
                                                                                           @RequestParam(value = "fromDate", required = false) Date fromDate,
                                                                                           @RequestParam(value = "toDate", required = false) Date toDate,
                                                                                           Pageable pageable) {

        ComboProductTranFilter filter = new ComboProductTranFilter(this.getShopId(), transCode, transType, fromDate, toDate);

        return comboProductTransService.findAll(filter, pageable);
    }

    @RoleAdmin
    @PostMapping(value = { V1 + root} )
    public Response<ComboProductTranDTO> create(@Valid @RequestBody ComboProductTranRequest request) {
        return comboProductTransService.create(request, this.getShopId(), this.getUserId());
    }

    @RoleAdmin
    @GetMapping(value = { V1 + root + "/{id}"})
    public Response<ComboProductTranDTO> getComboProductTran(@PathVariable Long id) {
        return comboProductTransService.getComboProductTrans(id);
    }
}
