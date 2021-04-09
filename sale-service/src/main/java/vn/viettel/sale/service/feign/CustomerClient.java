package vn.viettel.sale.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;
import vn.viettel.sale.service.dto.CustomerDTO;

import java.util.List;

@Service
@FeignClientAuthenticate(name = "customer-service")
public interface CustomerClient {

    @GetMapping("/api/customers/{id}")
    Response<CustomerDTO> getCustomerById(@PathVariable(name = "id") Long id);

    @GetMapping("/api/customers/getByPhone")
    Response<CustomerDTO> getCustomerByPhone(@RequestParam String phone);

    @GetMapping("/api/customers/ids-customer-by-keyword")
    Response<List<Long>> getIdCustomerBySearchKeyWords(@RequestParam("searchKeywords") String searchKeywords);
}
