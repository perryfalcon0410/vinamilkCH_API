package vn.viettel.sale.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.viettel.core.db.entity.voucher.MemberCustomer;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

@Service
@FeignClientAuthenticate(name = "promotion-service")
public interface MemberCustomerClient {

    @GetMapping("/api/promotion/membercustomer/findCustomer/{id}")
    Response<MemberCustomer> getMemberCustomerByIdCustomer(@PathVariable long id);
}
