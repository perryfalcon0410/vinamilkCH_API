package vn.viettel.customer.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.db.entity.voucher.MemberCard;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;
import vn.viettel.customer.service.dto.MemberCardDTO;

import javax.validation.Valid;
import java.util.Optional;

@Service
@FeignClientAuthenticate(name = "promotion-service")
public interface MemberCardClient {
    @GetMapping("api/promotion/membercard/findById/{id}")
    Optional<MemberCard> getMemberCardById(@PathVariable("id") Long id);

    @GetMapping("api/promotion/membercard/findByMemberCardCode/{code}")
    Optional<MemberCard> getMemberCardByMemberCardCode(@PathVariable("code") String code);

    @PostMapping("api/promotion/membercard/create")
    Response<MemberCard> create(@Valid @RequestBody MemberCardDTO request);

    @PutMapping("api/promotion/membercard/update")
    Response<MemberCard> update(@Valid @RequestBody MemberCardDTO request);


}
