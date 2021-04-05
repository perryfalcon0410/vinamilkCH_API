package vn.viettel.customer.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.viettel.core.db.entity.voucher.RptCusMemAmount;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

@Service
@FeignClientAuthenticate(name = "promotion-service")
public interface RptCusMemAmountClient {

    @GetMapping("/rpt-cus-mem-amount-by-customer-id/{id}")
    Response<RptCusMemAmount> FindByCustomerId(@PathVariable Long id);
}
