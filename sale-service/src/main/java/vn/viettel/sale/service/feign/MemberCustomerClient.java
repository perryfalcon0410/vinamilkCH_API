package vn.viettel.sale.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.viettel.core.dto.customer.MemberCustomerDTO;
import vn.viettel.core.dto.customer.RptCusMemAmountDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

@Service
@FeignClientAuthenticate(name = "customer-service")
public interface MemberCustomerClient {

    @GetMapping("/api/v1/customers/membercustomers/{id}")
    Response<MemberCustomerDTO> getMemberCustomerByIdCustomer(@PathVariable long id);

    @GetMapping("/api/v1/customers/prt-cus-mem-amounts/customer-id/{id}")
    Response<RptCusMemAmountDTO> findByCustomerId(@PathVariable Long id);
}
