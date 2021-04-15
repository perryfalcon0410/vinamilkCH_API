package vn.viettel.promotion.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.db.entity.authorization.User;
import vn.viettel.core.db.entity.common.ApParam;
import vn.viettel.core.db.entity.common.Customer;
import vn.viettel.core.db.entity.voucher.MemberCard;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.promotion.service.MemberCardService;
import vn.viettel.promotion.service.dto.MemberCardDTO;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/promotion/membercard")
public class MemberCardController extends BaseController {
    @Autowired
    MemberCardService memberCardService;

    @RoleAdmin
    @PostMapping
    public Response<MemberCard> create(@Valid @RequestBody MemberCardDTO request) {
        return memberCardService.create(request, this.getUserId());
    }
    @RoleAdmin
    @GetMapping("/{code}")
    public Response<MemberCard> getMemberCardByMemberCardCode(@PathVariable("code") String code){
        return memberCardService.getMemberCardByMemberCardCode(code);
    }
    @RoleAdmin
    @GetMapping("/{id}")
    public Response<MemberCard> getMemberCardById(@PathVariable long id) {
        return memberCardService.getMemberCardById(id);
    }

//    @RoleAdmin
    @GetMapping("/findByMemberCard/{id}")
    public Response<MemberCard> getMemberCardByMemberCardId(@PathVariable long id) {
        return memberCardService.getMemberCardByMemberCardId(id);
    }

    @RoleAdmin
    @PutMapping
    public Response<MemberCard> update(@Valid @RequestBody MemberCardDTO request) {
        return memberCardService.update(request);
    }

    @GetMapping("/customer-type-id/{id}")
    public Response<List<MemberCard>> getAllByCustomerTypeId(@PathVariable Long id) {
        return memberCardService.getMemberCardByCustomerId(id);
    }
}
