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
@RequestMapping("/api/customers/membercards")
public class MemberCardController extends BaseController {
    @Autowired
    MemberCardService memberCardService;

    @RoleAdmin
    @PostMapping
    public Response<MemberCard> create(@Valid @RequestBody MemberCardDTO request) {
        return memberCardService.create(request, this.getUserId());
    }

    @RoleAdmin
    @GetMapping("/{id}")
    public Response<MemberCardDTO> getMemberCardById(@PathVariable long id) {
        return memberCardService.getMemberCardById(id);
    }

    @RoleAdmin
    @PutMapping
    public Response<MemberCard> update(@Valid @RequestBody MemberCardDTO request) {
        return memberCardService.update(request);
    }

    @GetMapping("/customer-type-id/{id}")
    public Response<List<MemberCardDTO>> getAllByCustomerTypeId(@PathVariable Long id) {
        return memberCardService.getMemberCardByCustomerId(id);
    }
}
