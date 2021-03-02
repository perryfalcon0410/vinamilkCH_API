package vn.viettel.customer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.db.entity.Company;
import vn.viettel.core.db.entity.Customer;
import vn.viettel.core.db.entity.IDCard;
import vn.viettel.core.db.entity.MemberCard;
import vn.viettel.core.messaging.Response;
import vn.viettel.customer.service.CustomerService;
import vn.viettel.customer.service.dto.CardMemberResponse;
import vn.viettel.customer.service.dto.CustomerCreateRequest;
import vn.viettel.customer.service.dto.CustomerResponse;
import vn.viettel.customer.service.dto.DeleteRequest;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/customer")
public class CustomerController {
    @Autowired
    CustomerService service;

    @GetMapping("/all")
    public Response<List<CustomerResponse>> getAllCustomer() {
        return service.getAll();
    }

    @PostMapping("/create/{userId}")
    public Response<Customer> createCustomer(@RequestBody CustomerCreateRequest request, @PathVariable long userId) {
        return service.createCustomer(request, userId);
    }

    @PutMapping("/update/{userId}")
    public Response<Customer> updateCustomer(@RequestBody CustomerCreateRequest request, @PathVariable long userId) {
        return service.updateCustomer(request, userId);
    }

    @GetMapping("/idCard/{id}")
    public Response<IDCard> getIDCardById(@PathVariable long id) {
        return service.getIDCardById(id);
    }

    @GetMapping("/company/{id}")
    public Response<Company> getCompanyById(@PathVariable long id) {
        return service.getCompanyById(id);
    }

    @GetMapping("/memberCard/{id}")
    public Response<CardMemberResponse> getMemberCardById(@PathVariable long id) {
        return service.getMemberCardById(id);
    }

    @DeleteMapping("delete")
    public Response<String> deleteCustomer(@RequestBody DeleteRequest ids) {
        return service.deleteCustomer(ids);
    }

}
