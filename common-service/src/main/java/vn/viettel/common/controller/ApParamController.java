package vn.viettel.common.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.common.service.ApParamService;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.core.security.anotation.RoleFeign;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class ApParamController extends BaseController {
    @Autowired
    ApParamService apParamService;
    private final String root = "/commons/apparams";

    @ApiOperation(value = "Ap param theo id")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = {V1 + root + "/{id}"})
    public Response<ApParamDTO> getApParamById(HttpServletRequest httpRequest,
                                               @PathVariable Long id) {
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.GET_DATA_AP_PARAM_SUCCESS);
        Response<ApParamDTO> response = new Response<>();
        return response.withData(apParamService.getApParamById(id));
    }

    @ApiOperation(value = "Danh sách loại thẻ")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = {V1 + root + "/cardtypes"})
    Response<List<ApParamDTO>> getCardTypes(HttpServletRequest httpRequest) {
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.GET_DATA_CARD_TYPES_SUCCESS);
        Response<List<ApParamDTO>> response = new Response<>();
        return response.withData(apParamService.getCardTypes());
    }

    @ApiOperation(value = "Danh sách loại khách hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = {V1 + root + "/closelytypes"})
    Response<List<ApParamDTO>> getCloselytypes(HttpServletRequest httpRequest) {
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.GET_DATA_CLOSELY_TYPES_SUCCESS);
        Response<List<ApParamDTO>> response = new Response<>();
        return response.withData(apParamService.getCloselytypes());
    }

    @ApiOperation(value = "Lý do điều chỉnh theo id")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = {V1 + root + "/reason-adjust/{id}"})
    Response<ApParamDTO> getReasonAdjust(HttpServletRequest httpRequest,
                                         @PathVariable Long id) {
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.GET_DATA_AP_PARAM_SUCCESS);
        Response<ApParamDTO> response = new Response<>();
        return response.withData(apParamService.getReason(id));
    }

    @ApiOperation(value = "Danh sách ap param theo loại")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )

    @GetMapping(value = {V1 + root + "/type/{type}"})
    Response<List<ApParamDTO>> getByType(HttpServletRequest httpRequest,
                                         @PathVariable String type) {
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.GET_DATA_AP_PARAM_SUCCESS);
        Response<List<ApParamDTO>> response = new Response<>();
        return response.withData(apParamService.getByType(type));
    }

    @ApiOperation(value = "Lý do không nhập")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = {V1 + root + "/sale-mt-deny"})
    Response<List<ApParamDTO>> getReasonNotImport(HttpServletRequest httpRequest) {
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.GET_DATA_AP_PARAM_SUCCESS);
        Response<List<ApParamDTO>> response = new Response<>();
        return response.withData(apParamService.getReasonNotImport());
    }


    @ApiOperation(value = "Ap param")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = {V1 + root})
    public Response<List<ApParamDTO>> getApParams(HttpServletRequest httpRequest) {
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.GET_DATA_AP_PARAM_SUCCESS);
        Response<List<ApParamDTO>> response = new Response<>();
        return response.withData(apParamService.findAll());
    }


    @ApiOperation(value = "Ap param theo mã")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = {V1 + root + "/getByCode/{code}"})
    public Response<ApParamDTO> getApParamByCode(HttpServletRequest httpRequest,
                                                 @PathVariable String code) {
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.GET_DATA_AP_PARAM_SUCCESS);
        Response<ApParamDTO> response = new Response<>();
        return response.withData(apParamService.getByCode(code));
    }
}

