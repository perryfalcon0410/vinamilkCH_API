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

    @GetMapping(value = { V1 + root + "/{id}"})
    public Response<ApParamDTO> getApParamById(@PathVariable Long id) {
        return apParamService.getApParamById(id);
    }

    @ApiOperation(value = "Danh sách loại thẻ")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root + "/cardtypes"})
    Response<List<ApParamDTO>> getCardTypes(HttpServletRequest httpRequest)
    {
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.LOGIN_SUCCESS);
        return apParamService.getCardTypes();
    }

    @ApiOperation(value = "Danh sách loại khách hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root + "/closelytypes"})
    Response<List<ApParamDTO>> getCloselytypes(HttpServletRequest httpRequest){
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.LOGIN_SUCCESS);
        return apParamService.getCloselytypes();
    }

    @GetMapping(value = { V1 + root + "/reason-adjust/{id}"})
    Response<ApParamDTO> getReasonAdjust(@PathVariable Long id){
        return apParamService.getReason(id);
    }

    @RoleAdmin
    @RoleFeign
    @GetMapping(value = { V1 + root + "/type/{type}"})
    Response<List<ApParamDTO>> getByType(@PathVariable String type){
        return apParamService.getByType(type);
    }
    @RoleAdmin
    @GetMapping(value = { V1 + root + "/sale-mt-deny"})
    Response<List<ApParamDTO>> getReasonNotImport() {
        return apParamService.getReasonNotImport();
    }
    
    @RoleFeign
    @RoleAdmin
    @GetMapping(value = {V1 + root})
    public Response<List<ApParamDTO>> getApParams() {
        return apParamService.findAll();
    }

    @RoleFeign
    @RoleAdmin
    @GetMapping(value = {V1 + root + "/getByCode/{code}"})
    public Response<ApParamDTO> getApParamByCode(@PathVariable String code) {
        return apParamService.getByCode(code);
    }
}

