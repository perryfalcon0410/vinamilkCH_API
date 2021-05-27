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


    @ApiOperation(value = "Tạo mới thẻ thành viên")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @PostMapping(value = { V1 + root})
    public Response<MemberCard> create(HttpServletRequest httpRequest, @Valid @RequestBody MemberCardDTO request) {
        MemberCard memberCard = memberCardService.create(request, this.getUserId());
        Response<MemberCard> response = new Response<>();
        response.setStatusValue("Tạo mới thẻ thành viên thành công");
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.CREATE_MEMBER_CARD_SUCCESS);
        return new Response<MemberCard>().withData(memberCard);
    }

    @ApiOperation(value = "Tìm kiếm thẻ thành viên theo id")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root + "/{id}"})
    public Response<MemberCardDTO> getMemberCardById(HttpServletRequest httpRequest, @PathVariable long id) {
        MemberCardDTO memberCardDTO = memberCardService.getMemberCardById(id);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.FIND_MEMBER_CARD_SUCCESS);
        return new Response<MemberCardDTO>().withData(memberCardDTO);
    }

    @ApiOperation(value = "Cập nhật thẻ thành viên")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @PutMapping(value = { V1 + root})
    public Response<MemberCard> update(HttpServletRequest httpRequest, @Valid @RequestBody MemberCardDTO request) {
        MemberCard memberCard = memberCardService.update(request);
        Response<MemberCard> response = new Response<>();
        response.setStatusCode(201);
        response.setStatusValue("Cập nhật thẻ thành viên thành công");
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.UPDATE_MEMBER_CARD_SUCCESS);
        return response.withData(memberCard);
    }

    @ApiOperation(value = "Tìm kiếm thẻ thành viên theo customerTypeId")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root + "/customer-type-id/{id}"})
    public Response<List<MemberCardDTO>> getAllByCustomerTypeId(HttpServletRequest httpRequest, @PathVariable Long id) {
        List<MemberCardDTO>  memberCardDTOS = memberCardService.getMemberCardByCustomerId(id);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.FIND_MEMBER_CARD_SUCCESS);
        return new Response<List<MemberCardDTO>>().withData(memberCardDTOS);
    }
}
