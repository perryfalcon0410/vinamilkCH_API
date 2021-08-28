package vn.viettel.sale.service.feign;

import org.springframework.stereotype.Service;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

@Service
@FeignClientAuthenticate(name = "customer-service")
public interface MemberCustomerClient {

//    @GetMapping("/api/v1/customers/membercustomers/{id}")
//    Response<MemberCustomerDTO> getMemberCustomerByIdCustomerV1(@PathVariable long id);
//
//    @GetMapping("/api/v1/customers/prt-cus-mem-amounts/customer-id/{id}")
//    Response<RptCusMemAmountDTO> findByCustomerIdV1(@PathVariable Long id);
}
