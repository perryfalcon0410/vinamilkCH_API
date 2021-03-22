package vn.viettel.saleservice.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;
import vn.viettel.saleservice.service.dto.CustomerDTO;

@Service
@FeignClientAuthenticate(name = "customer-service")
public interface CustomerClient {
    @GetMapping("/api/customer/edit/{id}")
    Response<CustomerDTO> getCustomerById(@PathVariable(name = "id") Long id) ;
}
