package vn.viettel.customer.controller;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.exception.BadRequestException;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.Response;
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
@Api(value = "API thông tin khách hàng")
public class CustomerController extends BaseController {

    @Autowired
    private CustomerService service;
    private final String root = "/customers";

    @ApiOperation(value = "Tìm kiếm danh sách khách hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request")}
    )
    @GetMapping(value = { V1 + root})
    public Response<Page<CustomerDTO>> getAllCustomer(HttpServletRequest httpRequest,
                                                      @ApiParam(value = "Tìm theo tên, Mã khách hàng, MobiPhone ")
                                                      @RequestParam(value = "searchKeywords", required = false) String searchKeywords,
                                                      @RequestParam(value = "fromDate", required = false) Date fromDate,
                                                      @RequestParam(value = "toDate", required = false) Date toDate,
                                                      @RequestParam(value = "customerTypeId", required = false) Long customerTypeId,
                                                      @ApiParam(value = "Tìm trạng thái liệt kê các trạng thái, ")
                                                      @RequestParam(value = "status", required = false) Long status,
                                                      @RequestParam(value = "genderId", required = false) Long genderId,
                                                      @RequestParam(value = "areaId", required = false) Long areaId,
                                                      @RequestParam(value = "phone", required = false) String phone,
                                                      @RequestParam(value = "idNo", required = false) String idNo, Pageable pageable) {

        CustomerFilter customerFilter = new CustomerFilter(searchKeywords, fromDate, toDate, customerTypeId, status, genderId, areaId, phone, idNo, this.getShopId());
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.SEARCH_CUSTOMER_SUCCESS);
        Response<Page<CustomerDTO>> response = new Response<>();
        return response.withData(service.index(customerFilter, pageable));
    }

    @ApiOperation(value = "Tạo khách hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request")}
    )
    @PostMapping(value = { V1 + root + "/create"})
    public Response<CustomerDTO> create(HttpServletRequest httpRequest,@Valid @RequestBody CustomerRequest request) {
        Response<CustomerDTO> response = new Response<CustomerDTO>().withData(service.create(request, this.getUserId(), this.getShopId()));
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.CREATE_CUSTOMER_SUCCESS);
        return response;
    }

    @RoleFeign
    @PostMapping(value = { V1 + root + "/feign"})
    public Response<CustomerDTO> createForFeign(@Valid @RequestBody CustomerRequest request, @RequestParam Long userId, @RequestParam Long shopId) {
        Response<CustomerDTO> response = new Response<>();
        return response.withData(service.create(request, userId, shopId));
    }

//    @RoleFeign
    @ApiOperation(value = "Tìm kiếm khách hàng theo id")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request")}
    )
    @GetMapping(value = { V1 + root + "/{id}"})
    public Response<CustomerDTO> getCustomerById(@PathVariable(name = "id") Long id) {
        return service.getCustomerById(id);
    }

//    @RoleFeign
    @ApiOperation(value = "Tìm kiếm khách hàng theo mobiphone")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request")}
    )
    @GetMapping(value = { V1 + root + "/phone/{phone}"})
    public Response<CustomerDTO> getCustomerByMobiPhone(@PathVariable String phone) {
        return service.getCustomerByMobiPhone(phone);
    }

    @ApiOperation(value = "Chỉnh sửa khách hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request")}
    )
    @PatchMapping(value = { V1 + root + "/update/{id}"})
    public Response<CustomerDTO> update(HttpServletRequest httpRequest, @PathVariable(name = "id") Long id, @Valid @RequestBody CustomerRequest request) {
        request.setId(id);
        Response<CustomerDTO> response = new Response<>();
        response.withData(service.update(request, this.getUserId()));
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.UPDATE_CUSTOMER_SUCCESS);
        return response;
    }

    @ApiOperation(value = "Xuất excel ds khách hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request")}
    )
    @GetMapping(value = { V1 + root + "/export"})
    public ResponseEntity excelCustomersReport( ) throws IOException {
        List<ExportCustomerDTO> customerDTOPage = service.findAllCustomer();
        
        CustomerExcelExporter customerExcelExporter = new CustomerExcelExporter(customerDTOPage);
        ByteArrayInputStream in = customerExcelExporter.export();
        HttpHeaders headers = new HttpHeaders();
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        headers.add("Content-Disposition", "attachment; filename= Danh_sach_khach_hang_" + date  + ".xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(in));
    }

//    @RoleFeign
    @ApiOperation(value = "Tìm kiếm danh sách ids khách hàng bằng FullName Or Code Or Phone")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request")}
    )
    @GetMapping(value = { V1 + root + "/ids-customer-by-keyword"})
    public Response<List<Long>> getIdCustomerBySearchKeyWords(@RequestParam(value = "searchKeywords", required = false) String searchKeywords) {
        return service.getIdCustomerBySearchKeyWords(searchKeywords);
    }

//    @RoleFeign
    @ApiOperation(value = "Tìm kiếm khách hàng mặc định của shop đang login")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request")}
    )
    @GetMapping(value = { V1 + root + "/default"})
    public Response<CustomerDTO> getCustomerDefault() {
        return service.getCustomerDefault(this.getShopId());
    }

    @Override
    public ResponseEntity<?> handleAPIBadRequestException(BadRequestException ex, HttpServletRequest request) {
        return super.handleAPIBadRequestException(ex, request);
    }
}