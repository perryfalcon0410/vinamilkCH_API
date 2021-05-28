package vn.viettel.sale.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.messaging.CustomerRequest;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Service
@FeignClientAuthenticate(name = "customer-service")
public interface CustomerClient {

    @GetMapping("/api/v1/customers/{id}")
    Response<CustomerDTO> getCustomerByIdV1(@PathVariable(name = "id") Long id);

    @GetMapping("/api/v1/customers/ids-customer-by-keyword")
    Response<List<Long>> getIdCustomerBySearchKeyWordsV1(@RequestParam("searchKeywords") String searchKeywords);

    @GetMapping("/api/v1/customers/phone/{phone}")
    Response<CustomerDTO> getCustomerByMobiPhoneV1(@PathVariable String phone);

    @PostMapping("/api/v1/customers/feign")
    Response<CustomerDTO> createForFeignV1(@Valid @RequestBody CustomerRequest request, @RequestParam Long userId, @RequestParam Long shopId);

    @GetMapping("/api/v1/customers/feign-default")
    CustomerDTO getCusDefault(@PathVariable Long shopId);

}
