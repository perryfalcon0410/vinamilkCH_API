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

    @ApiOperation(value = "Danh sách điểm của thành viên")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @PostMapping(value = { V1 + root})
    public Response<MemberCustomer> create(HttpServletRequest httpRequest, @Valid @RequestBody MemberCustomerDTO request) {
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.LOGIN_SUCCESS);
        return memberCustomerService.create(request, this.getUserId());
    }
    @GetMapping(value = { V1 + root + "/{id}"})
    public Response<MemberCustomerDTO> getMemberCustomerById(@PathVariable long id) {
        return memberCustomerService.getMemberCustomerById(id);
    }
    @GetMapping(value = { V1 + root + "/findCustomer/{id}"})
    public Response<MemberCustomerDTO> getMemberCustomerByIdCustomer(@PathVariable long id) {
        return memberCustomerService.getMemberCustomerByIdCustomer(id);
    }

}
