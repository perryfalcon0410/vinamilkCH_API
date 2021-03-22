package vn.viettel.customer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.db.entity.Customer;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.core.security.anotation.RoleFeign;
import vn.viettel.customer.messaging.CustomerBulkDeleteRequest;
import vn.viettel.customer.messaging.CustomerCreateRequest;
import vn.viettel.customer.messaging.CustomerUpdateRequest;
import vn.viettel.customer.service.CustomerService;
import vn.viettel.customer.service.dto.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/customer")
public class CustomerController extends BaseController {
    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    CustomerService service;

    /**
     *
     * @param searchKeywords search full name or customer code
     * @param fromDate default start date of month
     * @param toDate default last date of month
     * @param groupId customer groups
     * @param status all, active, inactive
     * @param gender all, male, female
     * @param areaAddress province id, district id
     * @param pageable size, page
     * @return Response<Page<CustomerDTO>>>
     */
    @RoleAdmin
    @GetMapping("/index")
    public Response<Page<CustomerDTO>> getAllCustomer(@RequestParam(value = "searchKeywords", required = false) String searchKeywords, @RequestParam(value = "fromDate", required = false) Date fromDate, @RequestParam(value = "toDate", required = false) Date toDate, @RequestParam(value = "groupId", required = false) Long groupId, @RequestParam(value = "status", required = false) Long status, @RequestParam(value = "gender", required = false) Long gender, @RequestParam(value = "areaAddress", required = false) String areaAddress, Pageable pageable) {
        logger.info("[index()] - customer index #user_id: {}, #searchKeywords: {}", this.getUserId(), searchKeywords);
        return service.index(searchKeywords, fromDate, toDate, groupId, status, gender, areaAddress, pageable);
    }

    /**
     *
     * @param request customer data
     * @return Response<Customer>
     */
    @RoleAdmin
    @PostMapping("/create")
    public Response<Customer> create(@Valid @RequestBody CustomerCreateRequest request) {
        return service.create(request, this.getUserId());
    }


    @RoleFeign
    @RoleAdmin
    @GetMapping("/edit/{id}")
    public Response<CustomerDTO> edit(@PathVariable(name = "id") Long id) {
        return service.edit(id);
    }


    @RoleAdmin
    @PatchMapping("/update/{id}")
    public Response<CustomerDTO> update(@Valid @RequestBody CustomerUpdateRequest request, @PathVariable(name = "id") Long id) {
        return service.update(request, id, this.getUserId());
    }

    @RoleAdmin
    @DeleteMapping("/delete-bulk")
    public Response<List<Response<CustomerDTO>>> bulkDelete(@Valid @RequestBody CustomerBulkDeleteRequest request) {
        return service.deleteBulk(request, this.getUserId());
    }


    @RoleAdmin
    @GetMapping("/location/index")
    public Response<List<LocationDTO>> getAllLocationOfCustomers(@RequestParam(value = "shopId") Long shopId) {
        return service.getAllLocationOfCustomers(shopId);
    }

}
