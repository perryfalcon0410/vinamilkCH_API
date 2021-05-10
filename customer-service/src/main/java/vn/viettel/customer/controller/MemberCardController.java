package vn.viettel.customer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.customer.MemberCardDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.customer.entities.MemberCard;
import vn.viettel.customer.service.MemberCardService;

import javax.validation.Valid;
import java.util.List;

@RestController
public class MemberCardController extends BaseController {
    @Autowired
    MemberCardService memberCardService;
    private final String root = "/customers/membercards";

    @PostMapping(value = { V1 + root})
    public Response<MemberCard> create(@Valid @RequestBody MemberCardDTO request) {
        return memberCardService.create(request, this.getUserId());
    }

    @GetMapping(value = { V1 + root + "/{id}"})
    public Response<MemberCardDTO> getMemberCardById(@PathVariable long id) {
        return memberCardService.getMemberCardById(id);
    }

    @GetMapping(value = { V1 + root + "/findByMemberCard/{id}"})
    public Response<MemberCardDTO> getMemberCardByMemberCardId(@PathVariable long id) {
        return memberCardService.getMemberCardByMemberCardId(id);
    }

    @PutMapping(value = { V1 + root})
    public Response<MemberCard> update(@Valid @RequestBody MemberCardDTO request) {
        return memberCardService.update(request);
    }

    @GetMapping(value = { V1 + root + "/customer-type-id/{id}"})
    public Response<List<MemberCardDTO>> getAllByCustomerTypeId(@PathVariable Long id) {
        return memberCardService.getMemberCardByCustomerId(id);
    }
}
