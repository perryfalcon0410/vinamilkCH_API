package vn.viettel.sale.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.dto.SortDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.messaging.CustomerOnlRequest;
import vn.viettel.core.messaging.CustomerRequest;
import vn.viettel.core.messaging.MemberCustomerRequest;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

import javax.validation.Valid;
import java.util.List;

@Service
@FeignClientAuthenticate(name = "customer-service")
public interface CustomerClient {
    @GetMapping("/api/v1/customers/{id}")
    Response<CustomerDTO> getCustomerByIdV1(@PathVariable(name = "id") Long id);

    @PostMapping("/api/v1/customers/feign-cusinfo")
    List<CustomerDTO> getCustomerInfoV1(@RequestBody List<SortDTO> sorts, @RequestParam(required = false) Integer status, @RequestParam(required = false) List<Long> customerIds);

    @GetMapping("/api/v1/customers/ids-customer-by-keyword")
    Response<List<Long>> getIdCustomerBySearchKeyWordsV1(@RequestParam("searchKeywords") String searchKeywords);

    @GetMapping("/api/v1/customers/ids-customer")
    Response<List<Long>> getIdCustomerByV1(@RequestParam(value = "searchKeywords", required = false, defaultValue ="") String searchKeywords,
                                           @RequestParam(value = "customerPhone", required = false, defaultValue = "") String customerPhone,
                                           @RequestParam(value = "ids") List<Long> ids);

    @GetMapping("/api/v1/customers/phone/{phone}")
    Response<List<CustomerDTO>> getCustomerByMobiPhoneV1(@PathVariable String phone);

    @PostMapping("/api/v1/customers/feign")
    Response<CustomerDTO> createForFeignV1(@Valid @RequestBody CustomerOnlRequest request, @RequestParam Long shopId);

    @GetMapping("/api/v1/customers/feign-default/{id}")
    CustomerDTO getCusDefault(@PathVariable Long id);

    @PutMapping(value = { "/api/v1/customers/feign/update/{id}"})
    Response<CustomerDTO> updateFeignV1(@PathVariable(name = "id") Long id, @RequestBody CustomerRequest request);

//    @GetMapping(value = { "/api/v1/customers/feign-customers"})
//    Response<List<CustomerDTO>> getAllCustomerToRedInvocieV1(@RequestParam List<Long> customerIds);

    @PutMapping(value = {"/api/v1/customers/membercustomers/update/{customerId}"})
    Response<Long> updateMemberCustomerV1(@PathVariable Long customerId, @RequestBody MemberCustomerRequest request);

}
