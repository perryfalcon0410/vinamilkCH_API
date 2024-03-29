package vn.viettel.customer.controller;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.SortDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.exception.BadRequestException;
import vn.viettel.core.jms.JMSSender;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.CustomerOnlRequest;
import vn.viettel.core.messaging.CustomerRequest;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.utils.JMSType;
import vn.viettel.core.util.VNCharacterUtils;
import vn.viettel.customer.entities.MemoryStats;
import vn.viettel.customer.messaging.CusRedInvoiceFilter;
import vn.viettel.customer.messaging.CustomerFilter;
import vn.viettel.customer.messaging.CustomerSaleFilter;
import vn.viettel.customer.service.CustomerService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@Api(value = "API thông tin khách hàng")
public class CustomerController extends BaseController {

    @Autowired
    private CustomerService service;
    
    @Autowired
    JMSSender jmsSender;
    
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
                                                      @RequestParam(value = "status", required = false) Integer status,
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
        CustomerFilter customerFilter = new CustomerFilter(searchKeywords, customerTypeId, status, genderId, areaId, phone, idNo, this.getShopId(httpRequest), isShop);
        Page<CustomerDTO> customerDTOS = service.index(customerFilter, pageable);
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.SEARCH_CUSTOMER_SUCCESS);
        return new Response<Page<CustomerDTO>>().withData(customerDTOS);
    }

    @ApiOperation(value = "Tìm kiếm danh sách khách hàng chức năng đổi hàng hỏng - hàng trả lại")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request")}
    )
    @GetMapping(value = { V1 + root +"/customers-to-sale"})
    public Response<Page<CustomerDTO>> getCustomerForAutoComplete(HttpServletRequest httpRequest,
                                                      @ApiParam(value = "Tìm theo tên, Mã khách hàng, Sdt")
                                                      @RequestParam(value = "searchKeywords", required = false) String searchKeywords,
                                                      @SortDefault.SortDefaults({
                                                              @SortDefault(sort = "customerCode", direction = Sort.Direction.ASC),
                                                              @SortDefault(sort = "nameText", direction = Sort.Direction.ASC),
                                                              @SortDefault(sort = "mobiPhone", direction = Sort.Direction.ASC)
                                                      }) Pageable pageable) {
        Page<CustomerDTO> customerDTOS = service.getCustomerForAutoComplete(searchKeywords, this.getShopId(httpRequest), pageable);
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.SEARCH_CUSTOMER_SUCCESS);
        return new Response<Page<CustomerDTO>>().withData(customerDTOS);
    }

    @ApiOperation(value = "Tạo khách hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request")}
    )
    @PostMapping(value = { V1 + root + "/create"})
    public Response<CustomerDTO> create(HttpServletRequest httpRequest,@Valid @RequestBody CustomerRequest request) {
        Response<CustomerDTO> response = new Response<>();
        CustomerDTO customerDTO = service.create(request, this.getUserId(httpRequest), this.getShopId(httpRequest));
        if(customerDTO != null && customerDTO.getId() != null) {
        	sendSynRequest(Arrays.asList(customerDTO.getId()));
        }
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.CREATE_CUSTOMER_SUCCESS);
        response.setStatusValue("Thêm mới thông tin khách hàng thành công");
        return response.withData(customerDTO);
    }

    @PostMapping(value = { V1 + root + "/feign"})
    public Response<CustomerDTO> createForFeign(@Valid @RequestBody CustomerOnlRequest request, @RequestParam Long shopId) {
        return new Response<CustomerDTO>().withData(service.createForOnlOrder(request, shopId));
    }

    @ApiOperation(value = "Tìm kiếm khách hàng theo id")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request")}
    )
    @GetMapping(value = { V1 + root + "/{id}"})
    public Response<CustomerDTO> getCustomerById(HttpServletRequest httpRequest, @PathVariable(name = "id") Long id) {
        CustomerDTO customerDTO = service.getCustomerById(id,this.getShopId(httpRequest));
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.FIND_CUSTOMER_SUCCESS);
        return new Response<CustomerDTO>().withData(customerDTO);
    }


    @ApiOperation(value = "Tìm kiếm khách hàng theo mobiphone")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request")}
    )
    @GetMapping(value = { V1 + root + "/phone/{phone}"})
    public Response<List<CustomerDTO>> getCustomerByMobiPhone(HttpServletRequest httpRequest, @PathVariable String phone) {
        List<CustomerDTO> customerDTOS = service.getCustomerByMobiPhone(phone);
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.FIND_CUSTOMER_SUCCESS);
        return new Response<List<CustomerDTO>>().withData(customerDTOS);
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
        CustomerDTO customerDTO = service.update(request, this.getUserId(httpRequest),this.getShopId(httpRequest), true);
        if(customerDTO != null && customerDTO.getId() != null) {
        	sendSynRequest(Arrays.asList(customerDTO.getId()));
        }
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.UPDATE_CUSTOMER_SUCCESS);
        return response.withData(customerDTO);
    }

    @GetMapping(value = { V1 + root + "/export"})
    public void excelCustomersReport(HttpServletRequest httpRequest,
                                               @ApiParam(value = "Tìm theo tên, Mã khách hàng, MobiPhone ")
                                               @RequestParam(value = "searchKeywords", required = false) String searchKeywords,
                                               @RequestParam(value = "customerTypeId", required = false) Long customerTypeId,
                                               @ApiParam(value = "Tìm trạng thái liệt kê các trạng thái, ")
                                               @RequestParam(value = "status", required = false) Integer status,
                                               @RequestParam(value = "isShop", required = false) Boolean isShop,
                                               @RequestParam(value = "genderId", required = false) Long genderId,
                                               @RequestParam(value = "areaId", required = false) Long areaId,
                                               @RequestParam(value = "phoneNumber", required = false) String phone,
                                               @RequestParam(value = "idNo", required = false) String idNo
            , HttpServletResponse response
                                               ) throws IOException {
        if(isShop == null) isShop = false;
        CustomerFilter customerFilter = new CustomerFilter(searchKeywords, customerTypeId, status, genderId, areaId, phone, idNo, this.getShopId(httpRequest),isShop);
        this.closeStreamExcel(response, service.exportExcel(customerFilter), "Danh_sach_khach_hang_");
        response.getOutputStream().flush();
    }

    /*
        Cập nhật KH bên bán hàng || hóa đơn đỏ
     */
    @PutMapping(value = { V1 + root + "/feign/update/{id}"})
    public Response<CustomerDTO> updateFeign(HttpServletRequest httpRequest, @PathVariable(name = "id") Long id,
                                             @Valid @RequestBody CustomerRequest request) {
        CustomerDTO customerDTO = service.updateForSale(request, this.getShopId(httpRequest));
        return new Response<CustomerDTO>().withData(customerDTO);
    }

    @ApiOperation(value = "Tìm kiếm danh sách ids khách hàng bằng FullName Or Code Or Phone")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request")}
    )
    @GetMapping(value = { V1 + root + "/ids-customer-by-keyword"})
    public Response<List<Long>> getIdCustomerBySearchKeyWords(@RequestParam(value = "searchKeywords", required = false) String searchKeywords) {
        return new Response<List<Long>>().withData(service.getIdCustomerBySearchKeyWords(searchKeywords));
    }

    @ApiOperation(value = "Tìm kiếm danh sách ids khách hàng theo 2 input khác nhau(tên, mã khách hàng - số điện thoại Khách hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request")}
    )
    @PostMapping(value = { V1 + root + "/ids-customer"})
    public Response<List<Long>> getIdCustomerBy(@RequestParam(value = "searchKeywords", required = false, defaultValue ="") String searchKeywords,
                                                @RequestParam(value = "customerPhone", required = false, defaultValue = "") String customerPhone,
                                                @RequestBody List<Long> ids) {
        return new Response<List<Long>>().withData(service.getIdCustomerBy(searchKeywords, customerPhone, ids));
    }

    @ApiOperation(value = "Tìm kiếm khách hàng mặc định của shop đang login")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request")}
    )
    @GetMapping(value = { V1 + root + "/default"})
    public Response<CustomerDTO> getCustomerDefault(HttpServletRequest httpRequest) {
        CustomerDTO customerDTO = service.getCustomerDefault(this.getShopId(httpRequest));
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.FIND_CUSTOMER_SUCCESS);
        return new Response<CustomerDTO>().withData(customerDTO);
    }

    @GetMapping(value = { V1 + root + "/feign-default/{id}"})
    public CustomerDTO getCustomerDefault(@PathVariable Long id) {
        CustomerDTO customerDTO = service.getCustomerDefaultByShop(id);
        return customerDTO;
    }

    //Lấy danh sách customer theo danh sách id
    @PostMapping(value = { V1 + root + "/feign-cusinfo"})
    public List<CustomerDTO> getCustomerInfo(@RequestBody List<SortDTO> sorts, @RequestParam(required = false) Integer status,
                                             @RequestParam(required = false) List<Long> customerIds ) {
        return service.getCustomerInfo(status, customerIds, sorts);
    }

//    @GetMapping(value = { V1 + root + "/feign-customers"})
//    public Response<List<CustomerDTO>> getAllCustomerToRedInvocie(@RequestParam List<Long> customerIds) {
//        return new Response<List<CustomerDTO>>().withData(service.getAllCustomerToRedInvoice(customerIds));
//    }

    @Override
    public ResponseEntity<?> handleAPIBadRequestException(BadRequestException ex, HttpServletRequest request) {
        return super.handleAPIBadRequestException(ex, request);
    }

	private void sendSynRequest(List<Long> lstIds) {
		try {
			if(!lstIds.isEmpty()) {
				jmsSender.sendMessage(JMSType.customers, lstIds);
			}
		} catch (Exception ex) {
			LogFile.logToFile("CustomerServiceImpl.sendSynRequest", JMSType.customers, LogLevel.ERROR, null, "has error when encode data " + ex.getMessage());
		}
	}
    
    @ApiOperation(value = "Tìm kiếm khách hàng dạng autocomplete ở màn hình bán hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request")}
    )
    @GetMapping(value = { V1 + root + "/autocomplete"})
    public Response<Page<CustomerDTO>> findCustomerForSale(HttpServletRequest httpRequest,
                                                     @RequestParam(value = "searchKeywords", required = false, defaultValue ="") String searchKeywords,
                                                     @RequestParam(value = "customerOfShop", required = false) Boolean customerOfShop,
                                                     @RequestParam(value = "searchPhoneOnly", required = false) Boolean searchPhoneOnly,
                                                     @RequestParam(value = "searchAddressOnly", required = false) Boolean searchAddressOnly, Pageable pageable ) {
        if(customerOfShop == null) customerOfShop = true;
        if(searchPhoneOnly == null) searchPhoneOnly = true;
        CustomerSaleFilter filter = new CustomerSaleFilter();

        filter.setCustomerOfShop(customerOfShop);
        filter.setSearchPhoneOnly(searchPhoneOnly);
        filter.setSearchKeywords(VNCharacterUtils.removeAccent(searchKeywords.trim()).toUpperCase());

        return new Response<Page<CustomerDTO>>().withData(service.findCustomerForSale(this.getShopId(httpRequest), filter, pageable));
    }

    @ApiOperation(value = "Lấy thông tin tiền tích lũy theo khách hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request")}
    )
    @GetMapping(value = { V1 + root + "/scorecumulated/{customerId}"})
    public Response<Double> getScoreCumulated(HttpServletRequest httpRequest, @PathVariable(name = "customerId") Long customerId) {
        return new Response<Double>().withData(service.getScoreCumulated(customerId));
    }


    @ApiOperation(value = "Tìm kiếm khách hàng ở màn hình tạo hóa đơn đỏ")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request")}
    )
    @GetMapping(value = { V1 + root + "/red-invoice"})
    public Response<Page<CustomerDTO>> findCustomerForRedInvoice(HttpServletRequest httpRequest,
                                                           @RequestParam(value = "searchKeywords", required = false, defaultValue ="") String searchKeywords,
                                                           @RequestParam(value = "mobiphone", required = false, defaultValue = "") String mobiphone,
                                                           @RequestParam(value = "workingOffice", required = false) String workingOffice,
                                                           @RequestParam(value = "officeAddress", required = false) String officeAddress,
                                                           @RequestParam(value = "taxCode", required = false) String taxCode, Pageable pageable ) {

        if(workingOffice != null) workingOffice = VNCharacterUtils.removeAccent(workingOffice.trim()).toUpperCase();
        if(officeAddress != null) officeAddress = VNCharacterUtils.removeAccent(officeAddress.trim()).toUpperCase();
        if(taxCode != null) taxCode = VNCharacterUtils.removeAccent(taxCode.trim()).toUpperCase();

        CusRedInvoiceFilter filter = new CusRedInvoiceFilter(VNCharacterUtils.removeAccent(searchKeywords.trim()).toUpperCase(), mobiphone.trim(), workingOffice, officeAddress, taxCode);

        return new Response<Page<CustomerDTO>>().withData(service.findCustomerForRedInvoice(filter, this.getShopId(httpRequest), pageable));
    }

    public void setService(CustomerService cService) {
        if(service == null) service = cService;
    }
}