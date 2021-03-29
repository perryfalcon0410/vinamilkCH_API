package vn.viettel.promotion.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.db.entity.authorization.User;
import vn.viettel.core.db.entity.common.Customer;
import vn.viettel.core.db.entity.voucher.MemberCard;
import vn.viettel.core.messaging.Response;
import vn.viettel.promotion.service.MemberCardService;
import vn.viettel.promotion.service.dto.MemberCardDTO;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/promotion")
public class MemberCardController extends BaseController {
    @Autowired
    MemberCardService memberCardService;

    @PostMapping("/create")
    public Response<MemberCard> create(@Valid @RequestBody MemberCardDTO request) {
        return memberCardService.create(request, this.getUserId());
    }

    @GetMapping("/findById/{id}")
    public MemberCard getMemberCardById(@PathVariable long id) {
        return memberCardService.getMemberCardById(id).get();
    }
}
