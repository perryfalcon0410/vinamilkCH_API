package vn.viettel.promotion.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.db.entity.voucher.RptCusMemAmount;
import vn.viettel.core.messaging.Response;
import vn.viettel.promotion.service.RptCusMemAmountService;

@RestController
@RequestMapping("/api/promotion")
public class RptCusMemAmountController extends BaseController {
    @Autowired
    RptCusMemAmountService rptCusMemAmountService;

    @GetMapping("/rpt-cus-mem-amount-by-customer-id/{id}")
    public Response<RptCusMemAmount> FindByCustomerId(@PathVariable Long id) {
        return rptCusMemAmountService.findByCustomerId(id);
    }
}
