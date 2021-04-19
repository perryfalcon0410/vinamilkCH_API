package vn.viettel.customer.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.viettel.core.db.entity.authorization.User;
import vn.viettel.core.db.entity.voucher.MemberCard;
import vn.viettel.core.db.entity.voucher.MemberCustomer;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

@Service
@FeignClientAuthenticate(name = "promotion-service")
public interface MemberClient {

    @GetMapping("/api/promotion/membercustomer/findCustomer/{id}")
    Response<MemberCustomer> getMemberCustomerByIdCustomer(@PathVariable long id);

    @GetMapping("/api/promotion/membercard/findByMemberCard/{id}")
    Response<MemberCard> getMemberCardById(@PathVariable long id);

}
