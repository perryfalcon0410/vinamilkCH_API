package vn.viettel.customer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.core.security.anotation.RoleFeign;
import vn.viettel.customer.messaging.CustomerFilter;
import vn.viettel.customer.messaging.CustomerRequest;
import vn.viettel.customer.service.CustomerService;
import vn.viettel.customer.service.dto.CustomerDTO;
import vn.viettel.customer.service.dto.ExportCustomerDTO;
import vn.viettel.customer.service.impl.CustomerExcelExporter;

import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController extends BaseController {
    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    CustomerService service;

    /**
     *
     * @param searchKeywords search full name or customer code
     * @param fromDate default start date of month
     * @param toDate default last date of month
     * @param customerTypeId customer type
     * @param status all, active, inactive
     * @param genderId category data id
     * @param areaId area id
     * @param pageable size, page
     * pop_up search customer
     * @param phone phone customer
     * @param idNo identity card
     * @return Response<Page<CustomerDTO>>>
     */

    @RoleAdmin
    @GetMapping
    public Response<Page<CustomerDTO>> getAllCustomer(@RequestParam(value = "searchKeywords", required = false) String searchKeywords,
                                                      @RequestParam(value = "fromDate", required = false) Date fromDate,
                                                      @RequestParam(value = "toDate", required = false) Date toDate,
                                                      @RequestParam(value = "customerTypeId", required = false) Long customerTypeId,
                                                      @RequestParam(value = "status", required = false) Long status,
                                                      @RequestParam(value = "genderId", required = false) Long genderId,
                                                      @RequestParam(value = "areaId", required = false) Long areaId,
                                                      @RequestParam(value = "phone", required = false) String phone,
                                                      @RequestParam(value = "idNo", required = false) String idNo,Pageable pageable) {
        logger.info("[index()] - customer index #user_id: {}, #searchKeywords: {}", this.getUserId(), searchKeywords);
        CustomerFilter customerFilter = new CustomerFilter(searchKeywords, fromDate, toDate, customerTypeId, status, genderId, areaId, phone, idNo);
        return service.find(customerFilter, pageable);
    }

    /**
     *
     * @param request customer data
     * @return Response<Customer>
     */
    @RoleAdmin
    @PostMapping("/create")
    public Response<CustomerDTO> create(@Valid @RequestBody CustomerRequest request) {
        return service.create(request, this.getUserId(), this.getShopId());
    }

    @RoleAdmin
    @RoleFeign
    @GetMapping("/{id}")
    public Response<CustomerDTO> getCustomerById(@PathVariable(name = "id") Long id) {
        return service.getCustomerById(id);
    }

    @RoleFeign
    @RoleAdmin
    @GetMapping("/getByPhone")
    public Response<CustomerDTO> getCustomerByPhone(@RequestParam String phone) {
        return service.getCustomerByPhone(phone);
    }


    @RoleAdmin
    @PatchMapping("/update/{id}")
    public Response<CustomerDTO> update(@PathVariable(name = "id") Long id, @Valid @RequestBody CustomerRequest request) {
        request.setId(id);
        return service.update(request, this.getUserId());
    }

    @RoleAdmin
    @GetMapping(value = "/export")
    public ResponseEntity excelCustomersReport(Pageable pageable) throws IOException {
        Response<Page<ExportCustomerDTO>> customerDTOPage = service.findAllCustomer(pageable);
        List<ExportCustomerDTO> customers = customerDTOPage.getData().getContent();
        
        CustomerExcelExporter customerExcelExporter = new CustomerExcelExporter(customers);
        ByteArrayInputStream in = customerExcelExporter.export();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=customers.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(in));
    }

    @GetMapping("/ids-customer-by-keyword")
    public Response<List<Long>> getIdCustomerBySearchKeyWords(@RequestParam("searchKeywords") String searchKeywords) {
        return service.getIdCustomerBySearchKeyWords(searchKeywords);
    }
}