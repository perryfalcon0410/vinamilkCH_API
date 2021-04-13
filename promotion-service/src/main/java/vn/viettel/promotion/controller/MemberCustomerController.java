package vn.viettel.promotion.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.db.entity.voucher.MemberCustomer;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.core.security.anotation.RoleFeign;
import vn.viettel.promotion.service.MemberCustomerService;
import vn.viettel.promotion.service.dto.MemberCustomerDTO;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/promotion/membercustomer")
public class MemberCustomerController extends BaseController {
    @Autowired
    MemberCustomerService memberCustomerService;

    @PostMapping
    public Response<MemberCustomer> create(@Valid @RequestBody MemberCustomerDTO request) {
        return memberCustomerService.create(request, this.getUserId());
    }
    @GetMapping("/{id}")
    public Response<MemberCustomer> getMemberCustomerById(@PathVariable long id) {
        return memberCustomerService.getMemberCustomerById(id);
    }

    @RoleFeign
    @RoleAdmin
    @GetMapping("/customer/{customerId}")
    public Response<MemberCustomer> getMemberCustomerByCustomerId(@PathVariable long customerId) {
        return memberCustomerService.getMemberCustomerByCustomerId(customerId);
    }

}
