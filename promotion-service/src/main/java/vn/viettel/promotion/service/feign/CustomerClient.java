package vn.viettel.promotion.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.customer.MemberCustomerDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

@Service
@FeignClientAuthenticate(name = "customer-service")
public interface CustomerClient {

    @GetMapping("/api/v1/customers/{id}")
    Response<CustomerDTO> getCustomerByIdV1(@PathVariable(name = "id") Long id);

}
