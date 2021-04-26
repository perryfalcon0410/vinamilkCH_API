package vn.viettel.customer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.customer.RptCusMemAmountDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.customer.service.RptCusMemAmountService;

@RestController
public class RptCusMemAmountController extends BaseController {
    @Autowired
    private RptCusMemAmountService rptCusMemAmountService;
    private final String root = "/customers/prt-cus-mem-amounts";

    @GetMapping(value = { V1 + root + "/customer-id/{id}"})
    public Response<RptCusMemAmountDTO> FindByCustomerId(@PathVariable Long id) {
        return rptCusMemAmountService.findByCustomerId(id);
    }
}
