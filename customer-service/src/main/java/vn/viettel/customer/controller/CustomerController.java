package vn.viettel.customer.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.core.security.anotation.RoleFeign;
import vn.viettel.customer.messaging.CustomerFilter;
import vn.viettel.core.messaging.CustomerRequest;
import vn.viettel.customer.service.CustomerService;
import vn.viettel.customer.service.dto.ExportCustomerDTO;
import vn.viettel.customer.service.impl.CustomerExcelExporter;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@RestController
public class CustomerController extends BaseController {

    @Autowired
    CustomerService service;
    private final String root = "/customers";

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

    @ApiOperation(value = "Tìm kiếm danh sách khách hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root})
    public Response<Page<CustomerDTO>> getAllCustomer(HttpServletRequest httpRequest,
                                                      @RequestParam(value = "searchKeywords", required = false) String searchKeywords,
                                                      @RequestParam(value = "fromDate", required = false) Date fromDate,
                                                      @RequestParam(value = "toDate", required = false) Date toDate,
                                                      @RequestParam(value = "customerTypeId", required = false) Long customerTypeId,
                                                      @RequestParam(value = "status", required = false) Long status,
                                                      @RequestParam(value = "genderId", required = false) Long genderId,
                                                      @RequestParam(value = "areaId", required = false) Long areaId,
                                                      @RequestParam(value = "phone", required = false) String phone,
                                                      @RequestParam(value = "idNo", required = false) String idNo, Pageable pageable) {

        CustomerFilter customerFilter = new CustomerFilter(searchKeywords, fromDate, toDate, customerTypeId, status, genderId, areaId, phone, idNo, this.getShopId());
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.LOGIN_SUCCESS);
        return service.index(customerFilter, pageable);
    }

    /**
     *
     * @param request customer data
     * @return Response<Customer>
     */

    @ApiOperation(value = "Tạo khách hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @PostMapping(value = { V1 + root + "/create"})
    public Response<CustomerDTO> create(HttpServletRequest httpRequest,@Valid @RequestBody CustomerRequest request) {
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.LOGIN_SUCCESS);
        return service.create(request, this.getUserId(), this.getShopId());
    }

    @RoleFeign
    @PostMapping(value = { V1 + root + "/feign"})
    public Response<CustomerDTO> createForFeign(@Valid @RequestBody CustomerRequest request, @RequestParam Long userId, @RequestParam Long shopId) {
        return service.create(request, userId, shopId);
    }

    @RoleFeign
    @GetMapping(value = { V1 + root + "/{id}"})
    public Response<CustomerDTO> getCustomerById(@PathVariable(name = "id") Long id) {
        return service.getCustomerById(id);
    }

    @RoleFeign
    @GetMapping(value = { V1 + root + "/phone/{phone}"})
    public Response<CustomerDTO> getCustomerByMobiPhone(@PathVariable String phone) {
        return service.getCustomerByMobiPhone(phone);
    }

    @ApiOperation(value = "Chỉnh sửa khách hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @PatchMapping(value = { V1 + root + "/update/{id}"})
    public Response<CustomerDTO> update(HttpServletRequest httpRequest, @PathVariable(name = "id") Long id, @Valid @RequestBody CustomerRequest request) {
        request.setId(id);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.LOGIN_SUCCESS);
        return service.update(request, this.getUserId());
    }

    @GetMapping(value = { V1 + root + "/export"})
    public ResponseEntity excelCustomersReport( ) throws IOException {
        Response<List<ExportCustomerDTO>> customerDTOPage = service.findAllCustomer();
        List<ExportCustomerDTO> customers = customerDTOPage.getData();
        
        CustomerExcelExporter customerExcelExporter = new CustomerExcelExporter(customers);
        ByteArrayInputStream in = customerExcelExporter.export();
        HttpHeaders headers = new HttpHeaders();
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        headers.add("Content-Disposition", "attachment; filename= Danh_sach_khach_hang_" + date  + ".xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(in));
    }

    @RoleFeign
    @GetMapping(value = { V1 + root + "/ids-customer-by-keyword"})
    public Response<List<Long>> getIdCustomerBySearchKeyWords(@RequestParam(value = "searchKeywords", required = false) String searchKeywords) {
        return service.getIdCustomerBySearchKeyWords(searchKeywords);
    }

    @RoleFeign
    @GetMapping(value = { V1 + root + "/default"})
    public Response<CustomerDTO> getCustomerDefault() {
        return service.getCustomerDefault(this.getShopId());
    }
}