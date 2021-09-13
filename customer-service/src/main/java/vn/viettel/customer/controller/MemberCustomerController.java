package vn.viettel.customer.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.customer.MemberCustomerDTO;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.MemberCustomerRequest;
import vn.viettel.core.messaging.Response;
import vn.viettel.customer.entities.MemberCustomer;
import vn.viettel.customer.service.MemberCustomerService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
public class MemberCustomerController extends BaseController {
    @Autowired
    MemberCustomerService memberCustomerService;
    private final String root = "/customers/membercustomers";

    @ApiOperation(value = "Tạo mới thẻ điểm thành viên")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @PostMapping(value = { V1 + root + "/create"})
    public Response<MemberCustomer> create(HttpServletRequest httpRequest, @Valid @RequestBody MemberCustomerDTO request) {
        Response<MemberCustomer> response = new Response<>();
        response.setStatusValue("Tạo thẻ điểm thành viên thành công");
        MemberCustomer memberCustomer = memberCustomerService.create(request, this.getUserId());
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.CREATE_MEMBER_CUSTOMER_SUCCESS);
        return response.withData(memberCustomer);
    }

    @ApiOperation(value = "Tìm kiếm thẻ điểm thành viên bằng id")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root + "/{id}"})
    public Response<MemberCustomerDTO> getMemberCustomerById(HttpServletRequest httpRequest, @PathVariable long id) {
        MemberCustomerDTO memberCustomerDTO = memberCustomerService.getMemberCustomerById(id);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.FIND_MEMBER_CUSTOMER_SUCCESS);
        return new Response<MemberCustomerDTO>().withData(memberCustomerDTO);
    }

    @ApiOperation(value = "Tìm kiếm thẻ điểm thành viên bằng customerId")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root + "/findCustomer/{id}"})
    public Response<MemberCustomerDTO> getMemberCustomerByIdCustomer(HttpServletRequest httpRequest, @PathVariable long id) {
        MemberCustomerDTO memberCustomerDTO = memberCustomerService.getMemberCustomerByIdCustomer(id);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.FIND_MEMBER_CUSTOMER_SUCCESS);
        return new Response<MemberCustomerDTO>().withData(memberCustomerDTO);
    }

    @ApiOperation(value = "Tìm kiếm thẻ điểm thành viên bằng customerId")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )

    @GetMapping(value = { V1 + root + "/findCustomer/feign/{id}"})
    public MemberCustomerDTO getMemberCustomerByIdCustomer( @PathVariable long id) {
        MemberCustomerDTO memberCustomerDTO = memberCustomerService.getMemberCustomerByIdCustomer(id);
        return memberCustomerDTO;
    }

    @ApiOperation(value = "Cập nhật điểm thẻ thành viên")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @PutMapping(value = { V1 + root + "/update/{customerId}"})
    public Response<Long> updateMemberCustomer(@PathVariable Long customerId, @RequestBody MemberCustomerRequest request) {
        Response<Long> response = new Response<>();
        Long result = memberCustomerService.updateMemberCustomer(customerId, request);
        return response.withData(result);
    }


}
