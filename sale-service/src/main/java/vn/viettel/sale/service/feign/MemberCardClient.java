package vn.viettel.sale.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.viettel.core.dto.customer.MemberCardDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;


@Service
@FeignClientAuthenticate(name = "customer-service")
public interface MemberCardClient {
    @GetMapping(value = { "/api/v1/customers/membercards/customer/{id}"})
    Response<MemberCardDTO> getByCustomerId( @PathVariable Long id);

}
