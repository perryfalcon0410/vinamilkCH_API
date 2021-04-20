package vn.viettel.customer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.customer.RptCusMemAmountDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.customer.entities.RptCusMemAmount;
import vn.viettel.customer.service.RptCusMemAmountService;

@RestController
@RequestMapping("/api/customers/prt-cus-mem-amounts")
public class RptCusMemAmountController extends BaseController {
    @Autowired
    RptCusMemAmountService rptCusMemAmountService;

    @GetMapping("/customer-id/{id}")
    public Response<RptCusMemAmountDTO> FindByCustomerId(@PathVariable Long id) {
        return rptCusMemAmountService.findByCustomerId(id);
    }
}
