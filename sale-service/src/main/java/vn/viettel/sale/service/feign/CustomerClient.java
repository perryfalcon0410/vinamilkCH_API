package vn.viettel.sale.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import vn.viettel.core.db.entity.common.Customer;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;
import vn.viettel.sale.service.dto.CustomerDTO;

@Service
@FeignClientAuthenticate(name = "customer-service")
public interface CustomerClient {
    @GetMapping("/api/customer/feign/{id}")
    Response<Customer> getCustomerById(@PathVariable(name = "id") Long id) ;

    @GetMapping("/api/customer/edit/{id}")
    Response<CustomerDTO> getCustomerDTO(@PathVariable(name = "id") Long id);

    @GetMapping("/api/customer/get-by-id-and-type")
    Response<Customer> getByIdAndType(@RequestParam Long id, @RequestParam Long typeId);

    @GetMapping("/api/customer/getByPhone")
    Response<Customer> getCustomerByPhone(@RequestParam String phone);
}
