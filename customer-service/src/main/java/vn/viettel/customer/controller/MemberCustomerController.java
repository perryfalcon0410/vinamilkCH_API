package vn.viettel.customer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.customer.MemberCustomerDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.customer.entities.MemberCustomer;
import vn.viettel.customer.service.MemberCustomerService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/customers/membercustomers")
public class MemberCustomerController extends BaseController {
    @Autowired
    MemberCustomerService memberCustomerService;

    @PostMapping
    public Response<MemberCustomer> create(@Valid @RequestBody MemberCustomerDTO request) {
        return memberCustomerService.create(request, this.getUserId());
    }
    @GetMapping("/{id}")
    public Response<MemberCustomerDTO> getMemberCustomerById(@PathVariable long id) {
        return memberCustomerService.getMemberCustomerById(id);
    }
    @GetMapping("/findCustomer/{id}")
    public Response<MemberCustomerDTO> getMemberCustomerByIdCustomer(@PathVariable long id) {
        return memberCustomerService.getMemberCustomerByIdCustomer(id);
    }

}
