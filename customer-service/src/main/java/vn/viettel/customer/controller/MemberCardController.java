package vn.viettel.customer.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.customer.MemberCardDTO;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.customer.entities.MemberCard;
import vn.viettel.customer.service.MemberCardService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
public class MemberCardController extends BaseController {
    @Autowired
    MemberCardService memberCardService;
    private final String root = "/customers/membercards";


    @ApiOperation(value = "Danh sách thẻ thành viên")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @PostMapping(value = { V1 + root})
    public Response<MemberCard> create(HttpServletRequest httpRequest, @Valid @RequestBody MemberCardDTO request) {
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.SEARCH_MEMBER_CARD_SUCCESS);
        return memberCardService.create(request, this.getUserId());
    }

    @GetMapping(value = { V1 + root + "/{id}"})
    public Response<MemberCardDTO> getMemberCardById(@PathVariable long id) {
        return memberCardService.getMemberCardById(id);
    }

    @GetMapping(value = { V1 + root + "/findByMemberCard/{id}"})
    public Response<MemberCardDTO> getMemberCardByMemberCardId(@PathVariable long id) {
        return memberCardService.getMemberCardByMemberCardId(id);
    }

    @PutMapping(value = { V1 + root})
    public Response<MemberCard> update(@Valid @RequestBody MemberCardDTO request) {
        return memberCardService.update(request);
    }

    @GetMapping(value = { V1 + root + "/customer-type-id/{id}"})
    public Response<List<MemberCardDTO>> getAllByCustomerTypeId(@PathVariable Long id) {
        return memberCardService.getMemberCardByCustomerId(id);
    }
}
