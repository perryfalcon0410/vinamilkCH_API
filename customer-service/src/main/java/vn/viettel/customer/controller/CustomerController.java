package vn.viettel.customer.controller;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.exception.BadRequestException;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleFeign;
import vn.viettel.core.util.StringUtils;
import vn.viettel.customer.messaging.CustomerFilter;
import vn.viettel.core.messaging.CustomerRequest;
import vn.viettel.customer.service.CustomerService;
import vn.viettel.customer.service.dto.ExportCustomerDTO;
import vn.viettel.customer.service.impl.CustomerExcelExporter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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
                                                      @ApiParam(value = "Tìm theo tên, Mã khách hàng")
                                                      @RequestParam(value = "searchKeywords", required = false) String searchKeywords,
                                                      @RequestParam(value = "customerTypeId", required = false) Long customerTypeId,
                                                      @ApiParam(value = "Tìm trạng thái liệt kê các trạng thái, ")
                                                      @RequestParam(value = "status", required = false) Long status,
                                                      @ApiParam(value = "Khách hàng của cửa hàng")
                                                      @RequestParam(value = "isShop", required = false) Boolean isShop,
                                                      @RequestParam(value = "genderId", required = false) Long genderId,
                                                      @RequestParam(value = "areaId", required = false) Long areaId,
                                                      @RequestParam(value = "phoneNumber", required = false) String phone,
                                                      @RequestParam(value = "idNo", required = false) String idNo,
                                                      @SortDefault.SortDefaults({
                                                        @SortDefault(sort = "customerCode", direction = Sort.Direction.ASC),
                                                        @SortDefault(sort = "nameText", direction = Sort.Direction.ASC),
                                                        @SortDefault(sort = "mobiPhone", direction = Sort.Direction.ASC)
                                                      }) Pageable pageable) {
        if(isShop == null) isShop = false;
        CustomerFilter customerFilter = new CustomerFilter(searchKeywords, customerTypeId, status, genderId, areaId, phone, idNo, this.getShopId(), isShop);
        Page<CustomerDTO> customerDTOS = service.index(customerFilter, pageable);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.SEARCH_CUSTOMER_SUCCESS);
        return new Response<Page<CustomerDTO>>().withData(customerDTOS);
    }

    @ApiOperation(value = "Tìm kiếm danh sách khách hàng chức năng bán hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request")}
    )
    @GetMapping(value = { V1 + root +"/customers-to-sale"})
    public Response<Page<CustomerDTO>> getAllCustomerToSaleService(HttpServletRequest httpRequest,
                                                      @ApiParam(value = "Tìm theo tên, Mã khách hàng, Sdt")
                                                      @RequestParam(value = "searchKeywords", required = false) String searchKeywords,
                                                      @SortDefault.SortDefaults({
                                                              @SortDefault(sort = "customerCode", direction = Sort.Direction.ASC),
                                                              @SortDefault(sort = "nameText", direction = Sort.Direction.ASC),
                                                              @SortDefault(sort = "mobiPhone", direction = Sort.Direction.ASC)
                                                      }) Pageable pageable) {
        Page<CustomerDTO> customerDTOS = service.getAllCustomerToSaleService(searchKeywords, pageable);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.SEARCH_CUSTOMER_SUCCESS);
        return new Response<Page<CustomerDTO>>().withData(customerDTOS);
    }

    @ApiOperation(value = "Tạo khách hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request")}
    )
    @PostMapping(value = { V1 + root + "/create"})
    public Response<CustomerDTO> create(HttpServletRequest httpRequest,@Valid @RequestBody CustomerRequest request) {
        Response<CustomerDTO> response = new Response<>();
        CustomerDTO customerDTO = service.create(request, this.getUserId(), this.getShopId());
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.CREATE_CUSTOMER_SUCCESS);
        response.setStatusValue("Thêm mới thông tin khách hàng thành công");
        return response.withData(customerDTO);
    }

    @RoleFeign
    @PostMapping(value = { V1 + root + "/feign"})
    public Response<CustomerDTO> createForFeign(@Valid @RequestBody CustomerRequest request, @RequestParam Long userId, @RequestParam Long shopId) {
        return new Response<CustomerDTO>().withData(service.create(request, userId, shopId));
    }

    //    @RoleFeign
    @ApiOperation(value = "Tìm kiếm khách hàng theo id")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request")}
    )
    @GetMapping(value = { V1 + root + "/{id}"})
    public Response<CustomerDTO> getCustomerById(HttpServletRequest httpRequest, @PathVariable(name = "id") Long id) {
        CustomerDTO customerDTO = service.getCustomerById(id);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.FIND_CUSTOMER_SUCCESS);
        return new Response<CustomerDTO>().withData(customerDTO);
    }

    //    @RoleFeign
    @ApiOperation(value = "Tìm kiếm khách hàng theo mobiphone")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request")}
    )
    @GetMapping(value = { V1 + root + "/phone/{phone}"})
    public Response<CustomerDTO> getCustomerByMobiPhone(HttpServletRequest httpRequest, @PathVariable String phone) {
        CustomerDTO customerDTO = service.getCustomerByMobiPhone(phone);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.FIND_CUSTOMER_SUCCESS);
        return new Response<CustomerDTO>().withData(customerDTO);
    }

    @ApiOperation(value = "Chỉnh sửa khách hàng")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request")}
    )
    @PatchMapping(value = { V1 + root + "/update/{id}"})
    public Response<CustomerDTO> update(HttpServletRequest httpRequest, @PathVariable(name = "id") Long id, @Valid @RequestBody CustomerRequest request) {
        request.setId(id);
        Response<CustomerDTO> response = new Response<>();
        response.setStatusCode(201);
        response.setStatusValue("Cập nhật thông tin khách hàng thành công");
        CustomerDTO customerDTO = service.update(request, this.getUserId());
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.UPDATE_CUSTOMER_SUCCESS);
        return response.withData(customerDTO);
    }

    @GetMapping(value = { V1 + root + "/export"})
    public void excelCustomersReport(
                                               @ApiParam(value = "Tìm theo tên, Mã khách hàng, MobiPhone ")
                                               @RequestParam(value = "searchKeywords", required = false) String searchKeywords,
                                               @RequestParam(value = "customerTypeId", required = false) Long customerTypeId,
                                               @ApiParam(value = "Tìm trạng thái liệt kê các trạng thái, ")
                                               @RequestParam(value = "status", required = false) Long status,
                                               @RequestParam(value = "isShop", required = false) Boolean isShop,
                                               @RequestParam(value = "genderId", required = false) Long genderId,
                                               @RequestParam(value = "areaId", required = false) Long areaId,
                                               @RequestParam(value = "phoneNumber", required = false) String phone,
                                               @RequestParam(value = "idNo", required = false) String idNo
            , HttpServletResponse response
                                               ) throws IOException {
        if(isShop == null) isShop = false;
        CustomerFilter customerFilter = new CustomerFilter(searchKeywords, customerTypeId, status, genderId, areaId, phone, idNo, this.getShopId(),isShop);
        List<ExportCustomerDTO> customerDTOPage = service.findAllCustomer(customerFilter);
        CustomerExcelExporter customerExcelExporter = new CustomerExcelExporter(customerDTOPage);
        ByteArrayInputStream in = customerExcelExporter.export();
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Disposition", "attachment; filename= Danh_sach_khach_hang_" + StringUtils.createExcelFileName());
        response.setContentType("application/octet-stream");
        response.addHeader("Content-Disposition", "attachment; filename=Danh_sach_khach_hang_" + StringUtils.createExcelFileName());
        FileCopyUtils.copy(in, response.getOutputStream());
        response.getOutputStream().flush();

//        return ResponseEntity
//                .ok()
//                .headers(headers)
//                .body(new InputStreamResource(in));
    }


    @RoleFeign
    @PutMapping(value = { V1 + root + "/feign/update/{id}"})
    public Response<CustomerDTO> updateFeign(@PathVariable(name = "id") Long id, @Valid @RequestBody CustomerRequest request) {
        CustomerDTO customerDTO = service.update(request, this.getUserId());
        return new Response<CustomerDTO>().withData(customerDTO);
    }

    //    @RoleFeign
    @ApiOperation(value = "Tìm kiếm danh sách ids khách hàng bằng FullName Or Code Or Phone")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request")}
    )
    @GetMapping(value = { V1 + root + "/ids-customer-by-keyword"})
    public Response<List<Long>> getIdCustomerBySearchKeyWords(@RequestParam(value = "searchKeywords", required = false) String searchKeywords) {
        return new Response<List<Long>>().withData(service.getIdCustomerBySearchKeyWords(searchKeywords));
    }

    @RoleFeign
    @ApiOperation(value = "Tìm kiếm danh sách ids khách hàng theo 2 input khác nhau(tên, mã khách hàng - số điện thoại Khách hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request")}
    )
    @GetMapping(value = { V1 + root + "/ids-customer"})
    public Response<List<Long>> getIdCustomerBy(@RequestParam(value = "searchKeywords", required = false, defaultValue ="") String searchKeywords,
                                                @RequestParam(value = "customerCode", required = false, defaultValue = "") String customerCode) {
        return new Response<List<Long>>().withData(service.getIdCustomerBy(searchKeywords, customerCode));
    }

    @ApiOperation(value = "Tìm kiếm khách hàng mặc định của shop đang login")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request")}
    )
    @GetMapping(value = { V1 + root + "/default"})
    public Response<CustomerDTO> getCustomerDefault(HttpServletRequest httpRequest) {
        CustomerDTO customerDTO = service.getCustomerDefault(this.getShopId());
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.FIND_CUSTOMER_SUCCESS);
        return new Response<CustomerDTO>().withData(customerDTO);
    }

    @RoleFeign
    @GetMapping(value = { V1 + root + "/feign-default/{id}"})
    public CustomerDTO getCustomerDefault(@PathVariable Long id) {
        CustomerDTO customerDTO = service.getCustomerDefaultByShop(id);
        return customerDTO;
    }

    //Lấy danh sách customer theo danh sách id
    @RoleFeign
    @GetMapping(value = { V1 + root + "/feign-cusinfo"})
    public List<CustomerDTO> getCustomerInfo(@RequestParam(required = false) Long status, @RequestParam List<Long> customerIds) {
        return service.getCustomerInfo(status, customerIds);
    }

    @GetMapping(value = { V1 + root + "/feign-customers"})
    public Response<Map<Integer, CustomerDTO>> getAllCustomerToRedInvocie() {
        return new Response<Map<Integer, CustomerDTO>>().withData(service.getAllCustomerToRedInvoice());
    }

    @Override
    public ResponseEntity<?> handleAPIBadRequestException(BadRequestException ex, HttpServletRequest request) {
        return super.handleAPIBadRequestException(ex, request);
    }
}