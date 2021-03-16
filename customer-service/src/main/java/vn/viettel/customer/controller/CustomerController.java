package vn.viettel.customer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.db.entity.Company;
import vn.viettel.core.db.entity.Customer;
import vn.viettel.core.db.entity.IDCard;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.core.security.interceptor.CheckRoleInterceptor;
import vn.viettel.customer.service.CustomerService;
import vn.viettel.customer.service.SearchCustomerService;
import vn.viettel.customer.service.dto.*;
import vn.viettel.customer.service.impl.CustomerExcelExporter;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {
    @Autowired
    CustomerService service;

    @Autowired
    SearchCustomerService searchService;

    @Autowired
    CheckRoleInterceptor checkRoleInterceptor;

    @RoleAdmin
    @GetMapping("/all")
    public Response<Page<CustomerResponse>> getAllCustomer(Pageable pageable) {
        return service.getAll(pageable);
    }

    @GetMapping("/getById/{id}")
    public Response<CustomerResponse> getCustomerById(@PathVariable long id) {
        return service.getById(id);
    }

    @GetMapping("/findById/{id}")
    public Customer findById(@PathVariable long id) {
        return service.findById(id);
    }

    @GetMapping("/getByType/{type}")
    public Response<List<CustomerResponse>> getCustomerByType(@PathVariable int type) {
        return service.getByType(type);
    }

    @GetMapping("/search")
    public Response<List<CustomerResponse>> searchCustomer(@RequestBody SearchCustomerDto searchInfo) {
        return searchService.searchCustomer(searchInfo);
    }

    @PostMapping("/create/{userId}")
    public Response<Customer> createCustomer(@Valid @RequestBody CustomerCreateRequest request, @PathVariable long userId) {
        return service.createCustomer(request, userId);
    }

    @PutMapping("/update/{userId}")
    public Response<Customer> updateCustomer(@Valid @RequestBody CustomerCreateRequest request, @PathVariable long userId) {
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

    @GetMapping("/export/excel")
    public ResponseEntity exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");

        List<CustomerResponse> customerList = service.findAll();
        CustomerExcelExporter excelExporter = new CustomerExcelExporter(customerList);

        ByteArrayInputStream in = excelExporter.export();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=customers.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(in));
    }

}
