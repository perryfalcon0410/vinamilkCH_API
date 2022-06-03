package vn.viettel.common.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.common.service.ApParamService;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.Response;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class ApParamController extends BaseController {
    @Autowired
    ApParamService apParamService;
    private final String root = "/commons/apparams";

    public void setService(ApParamService service){
        if(apParamService == null) apParamService = service;
    }

    @ApiOperation(value = "Ap param theo id")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = {V1 + root + "/{id}"})
    public Response<ApParamDTO> getApParamById(HttpServletRequest httpRequest,
                                               @PathVariable Long id) {
        ApParamDTO apParamDTO = apParamService.getApParamById(id);
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.GET_DATA_AP_PARAM_SUCCESS);
        return new Response<ApParamDTO>().withData(apParamDTO);
    }

    @ApiOperation(value = "Danh sách loại thẻ")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = {V1 + root + "/cardtypes"})
    Response<List<ApParamDTO>> getCardTypes(HttpServletRequest httpRequest) {
        List<ApParamDTO> apParamDTOS = apParamService.getCardTypes();
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.GET_DATA_CARD_TYPES_SUCCESS);
        return new Response<List<ApParamDTO>>().withData(apParamDTOS);
    }

    @ApiOperation(value = "Danh sách loại khách hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = {V1 + root + "/closelytypes"})
    Response<List<ApParamDTO>> getCloselytypes(HttpServletRequest httpRequest) {
        List<ApParamDTO> apParamDTOS = apParamService.getCloselytypes();
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.GET_DATA_CLOSELY_TYPES_SUCCESS);
        return new Response<List<ApParamDTO>>().withData(apParamDTOS);
    }

    @ApiOperation(value = "Lý do điều chỉnh theo id")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )

    @GetMapping(value = {V1 + root + "/reason-adjust/{id}"})
    Response<ApParamDTO> getReasonAdjust(HttpServletRequest httpRequest,
                                         @PathVariable Long id) {
        ApParamDTO apParamDTO = apParamService.getReason(id);
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.GET_DATA_AP_PARAM_SUCCESS);

        return new Response<ApParamDTO>().withData(apParamDTO);
    }

    @ApiOperation(value = "Danh sách ap param theo loại")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )

    @GetMapping(value = {V1 + root + "/type/{type}"})
    Response<List<ApParamDTO>> getByType(HttpServletRequest httpRequest, @PathVariable String type) {
        List<ApParamDTO> apParamDTOS = apParamService.getByType(type);
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.GET_DATA_AP_PARAM_SUCCESS);
        return new Response<List<ApParamDTO>>().withData(apParamDTOS);
    }

    @ApiOperation(value = "Lý do không nhập")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = {V1 + root + "/sale-mt-deny"})
    Response<List<ApParamDTO>> getReasonNotImport(HttpServletRequest httpRequest) {
        List<ApParamDTO> apParamDTOS = apParamService.getReasonNotImport();
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.GET_DATA_AP_PARAM_SUCCESS);
        return new Response<List<ApParamDTO>>().withData(apParamDTOS);
    }


    @ApiOperation(value = "Ap param")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = {V1 + root})
    public Response<List<ApParamDTO>> getApParams(HttpServletRequest httpRequest) {
        List<ApParamDTO> apParamDTOS = apParamService.findAll();
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.GET_DATA_AP_PARAM_SUCCESS);

        return new Response<List<ApParamDTO>>().withData(apParamDTOS);
    }

    @ApiOperation(value = "Ap param theo mã")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = {V1 + root + "/getByCode/{code}"})
    public Response<ApParamDTO> getApParamByCode(HttpServletRequest httpRequest,
                                                 @PathVariable String code) {
        ApParamDTO apParamDTO = apParamService.getByCode(code);
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.GET_DATA_AP_PARAM_SUCCESS);
        return new Response<ApParamDTO>().withData(apParamDTO);
    }

    @ApiOperation(value = "Ap param theo type và status")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = {V1 + root + "/get_sales_channel"})
    public Response<List<ApParamDTO>> getSalesChannel(HttpServletRequest httpRequest) {
        List<ApParamDTO> apParamDTO = apParamService.getSalesChannel();
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.GET_DATA_AP_PARAM_SUCCESS);
        return new Response<List<ApParamDTO>>().withData(apParamDTO);
    }


    @ApiOperation(value = "Ap param theo type và value")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = {V1 + root + "/type-value"})
    public Response<ApParamDTO> getApParamByTypeAndvalue(@RequestParam String type, @RequestParam String value) {
        ApParamDTO apParamDTO = apParamService.getApParamByTypeAndvalue(type, value);
        return new Response<ApParamDTO>().withData(apParamDTO);
    }

    @ApiOperation(value = "Lấy loại đơn online trong đơn online")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = {V1 + root + "/online-order/type"})
    public Response<ApParamDTO> getApParamOnlineOrder(@RequestParam(required = false) String discription) {
        ApParamDTO apParamDTO = apParamService.getApParamOnlineOrder(discription);
        return new Response<ApParamDTO>().withData(apParamDTO);
    }

    @ApiOperation(value = "Ap param theo mã và loại")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = {V1 + root + "/code-type/{code}"})
    public Response<ApParamDTO> getApParamByCodeType(HttpServletRequest httpRequest, @PathVariable String code, @RequestParam String type) {
        ApParamDTO apParamDTO = apParamService.getByCode(code, type);
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.GET_DATA_AP_PARAM_SUCCESS);
        return new Response<ApParamDTO>().withData(apParamDTO);
    }

    @ApiOperation(value = "Ap param theo mã và value")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = {V1 + root + "/get-by-value/{type}"})
    public Response<List<ApParamDTO>> getApParams(@PathVariable String type, @RequestParam List<String> values) {
        List<ApParamDTO> apParamDTO = apParamService.getApParams(values, type);
        return new Response<List<ApParamDTO>>().withData(apParamDTO);
    }

}

