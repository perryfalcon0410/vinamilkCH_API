package vn.viettel.common.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.common.service.CategoryDataService;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.common.CategoryDataDTO;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.Response;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class CategoryDataController extends BaseController {
    @Autowired
    CategoryDataService categoryDataService;
    private final String root = "/commons/categorydata";

    public void setService(CategoryDataService service){
        if(categoryDataService == null) categoryDataService = service;
    }

    @ApiOperation(value = "")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root + "/{id}"})
    public Response<CategoryDataDTO> getCategoryDataById(HttpServletRequest httpRequest,@PathVariable Long id) {
        CategoryDataDTO categoryDataDTO = categoryDataService.getCategoryDataById(id);
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.GET_DATA_CATEGORY_SUCCESS);
        return new Response<CategoryDataDTO>().withData(categoryDataDTO);
    }

    @ApiOperation(value = "Danh sách giới tính")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root + "/genders"})
    public Response<List<CategoryDataDTO>> getGenders(HttpServletRequest httpRequest){
        List<CategoryDataDTO> dtoList = categoryDataService.getGenders();
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.GET_DATA_CATEGORY_SUCCESS);
        return new Response<List<CategoryDataDTO>>().withData(dtoList);
    }

    @ApiOperation(value = "")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root + "/get-by-group-code"})
    public Response<List<CategoryDataDTO>> getByCategoryGroupCode(HttpServletRequest httpRequest) {
        List<CategoryDataDTO> dtoList = categoryDataService.getByCategoryGroupCode();
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.GET_DATA_CATEGORY_SUCCESS);
        return new Response<List<CategoryDataDTO>>().withData(dtoList);
    }

    @ApiOperation(value = "")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root + "/reason/{id}"})
    public Response<CategoryDataDTO> getReasonById(@PathVariable Long id) {
        CategoryDataDTO categoryDataDTO = categoryDataService.getReasonById(id);
        return new Response<CategoryDataDTO>().withData(categoryDataDTO);
    }

    @ApiOperation(value = "")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root + "/get-reason-exchange"})
    public Response<List<CategoryDataDTO>> getReasonExchange(HttpServletRequest httpRequest) {
        List<CategoryDataDTO> dtoList = categoryDataService.getListReasonExchange();
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.GET_DATA_CATEGORY_SUCCESS);
        return new Response<List<CategoryDataDTO>>().withData(dtoList);
    }

    @ApiOperation(value = "")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root + "/feign/get-reason-exchange"})
    public Response<List<CategoryDataDTO>> getReasonExchangeFeign(HttpServletRequest httpRequest, @RequestParam(required = false) List<Long> customerIds,
                                                             @RequestParam(required = false) String sortName, @RequestParam(required = false) String direction) {
        List<CategoryDataDTO> dtoList = categoryDataService.getListReasonExchangeFeign(customerIds, sortName, direction);
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.GET_DATA_CATEGORY_SUCCESS);
        return new Response<List<CategoryDataDTO>>().withData(dtoList);
    }


}
