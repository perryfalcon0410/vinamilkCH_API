package vn.viettel.sale.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;
import vn.viettel.sale.messaging.CustomerRequest;
import vn.viettel.sale.service.dto.CustomerDTO;

import javax.validation.Valid;
import java.util.List;

@Service
@FeignClientAuthenticate(name = "customer-vn.viettel.report.service")
public interface CustomerClient {

    @GetMapping("/api/customers/{id}")
    Response<CustomerDTO> getCustomerById(@PathVariable(name = "id") Long id);

    @GetMapping("/api/customers/ids-customer-by-keyword")
    Response<List<Long>> getIdCustomerBySearchKeyWords(@RequestParam("searchKeywords") String searchKeywords);

    @GetMapping("/api/customers/phone/{phone}")
    Response<CustomerDTO> getCustomerByPhone(@PathVariable String phone,
                                             @RequestParam String lastName,
                                             @RequestParam String firstName, @RequestParam Long shopId);

    @PostMapping("/api/customers/feign")
    Response<CustomerDTO> createForFeign(@Valid @RequestBody CustomerRequest request, @RequestParam Long shopId);
}
