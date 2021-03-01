package vn.viettel.customer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.db.entity.Customer;
import vn.viettel.core.messaging.Response;
import vn.viettel.customer.service.CustomerService;
import vn.viettel.customer.service.dto.CustomerCreateRequest;
import vn.viettel.customer.service.dto.CustomerResponse;

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
}
