package vn.viettel.sale.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.customer.RptCusMemAmountDTO;
import vn.viettel.core.messaging.CustomerRequest;
import vn.viettel.core.messaging.MemberCustomerRequest;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.messaging.RptCusMemAmountRequest;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;
import vn.viettel.core.security.anotation.RoleFeign;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Service
@FeignClientAuthenticate(name = "customer-service")
public interface CustomerClient {
    @GetMapping("/api/v1/customers/{id}")
    Response<CustomerDTO> getCustomerByIdV1(@PathVariable(name = "id") Long id);

    @GetMapping("/api/v1/customers/feign-cusinfo")
    List<CustomerDTO> getCustomerInfoV1(@RequestParam(required = false) Integer status, @RequestParam(required = false) List<Long> customerIds);

    @GetMapping("/api/v1/customers/ids-customer-by-keyword")
    Response<List<Long>> getIdCustomerBySearchKeyWordsV1(@RequestParam("searchKeywords") String searchKeywords);

    @GetMapping("/api/v1/customers/ids-customer")
    Response<List<Long>> getIdCustomerByV1(@RequestParam("searchKeywords") String searchKeywords, @RequestParam(value = "customerCode", required = false) String customerCode);

    @GetMapping("/api/v1/customers/phone/{phone}")
    Response<List<CustomerDTO>> getCustomerByMobiPhoneV1(@PathVariable String phone);

    @PostMapping("/api/v1/customers/feign")
    Response<CustomerDTO> createForFeignV1(@Valid @RequestBody CustomerRequest request, @RequestParam Long userId, @RequestParam Long shopId);

    @GetMapping("/api/v1/customers/feign-default/{id}")
    CustomerDTO getCusDefault(@PathVariable Long id);

    @PutMapping(value = { "/api/v1/customers/feign/update/{id}"})
    Response<CustomerDTO> updateFeignV1(@PathVariable(name = "id") Long id, @RequestBody CustomerRequest request);

    @GetMapping(value = { "/api/v1/customers/feign-customers"})
    Response<Map<Integer, CustomerDTO>> getAllCustomerToRedInvocieV1();

    @PutMapping(value = {"/api/v1/customers/membercustomers/update/{customerId}"})
    Response<Boolean> updateMemberCustomerV1(@PathVariable Long customerId, @RequestBody MemberCustomerRequest request);

}
