package vn.viettel.customer.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import vn.viettel.core.db.entity.voucher.MemberCard;
import vn.viettel.core.db.entity.voucher.MemberCustomer;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;
import vn.viettel.customer.service.dto.MemberCardDTO;
import vn.viettel.customer.service.dto.MemberCustomerDTO;

import javax.validation.Valid;
import java.util.Optional;

@Service
@FeignClientAuthenticate(name = "promotion-service")
public interface MemberCustomerClient {
    @GetMapping("api/promotion/membercustomer/findById/{id}")
    MemberCustomer getMemberCustomerById(@PathVariable("id") Long id);

    @PostMapping("api/promotion/membercustomer/create")
    Response<MemberCustomer> create(@Valid @RequestBody MemberCustomerDTO request);
}
