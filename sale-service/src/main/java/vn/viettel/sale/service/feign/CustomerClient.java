package vn.viettel.sale.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.messaging.CustomerRequest;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

import javax.validation.Valid;
import java.util.List;

@Service
@FeignClientAuthenticate(name = "customer-service")
public interface CustomerClient {

    @GetMapping("/api/v1/customers/{id}")
    Response<CustomerDTO> getCustomerById(@PathVariable(name = "id") Long id);

    @GetMapping("/api/v1/customers/ids-customer-by-keyword")
    Response<List<Long>> getIdCustomerBySearchKeyWords(@RequestParam("searchKeywords") String searchKeywords);

    @GetMapping("/api/v1/customers/phone/{phone}")
    Response<CustomerDTO> getCustomerByMobiPhone(@PathVariable String phone);

    @PostMapping("/api/v1/customers/feign")
    Response<CustomerDTO> createForFeign(@Valid @RequestBody CustomerRequest request, @RequestParam Long shopId, @RequestParam Long userId);
}
