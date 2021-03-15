package vn.viettel.saleservice.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.viettel.core.db.entity.Customer;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;
import vn.viettel.saleservice.service.dto.CustomerResponse;

@Service
@FeignClientAuthenticate(name = "customer-service")
public interface CustomerClient {
    @GetMapping("/api/customer/findById/{id}")
    Customer findById(@PathVariable long id);
    @GetMapping("/api/customer/getById/{id}")
    Response<CustomerResponse> getById(@PathVariable  long id);
}
