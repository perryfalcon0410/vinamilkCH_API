package vn.viettel.common.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping(value = { V1 + root + "/{id}"})
    public Response<CategoryDataDTO> getCategoryDataById(@PathVariable Long id) {
        return categoryDataService.getCategoryDataById(id);
    }

    @ApiOperation(value = "Danh sách giới tính")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root + "/genders"})
    public Response<List<CategoryDataDTO>> getGenders(HttpServletRequest httpRequest){
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.LOGIN_SUCCESS);
        return categoryDataService.getGenders();
    }

    @GetMapping(value = { V1 + root + "/get-by-group-code"})
    public List<CategoryDataDTO> getByCategoryGroupCode() {
        return categoryDataService.getByCategoryGroupCode();
    }

    @GetMapping(value = { V1 + root + "/reason/{id}"})
    public CategoryDataDTO getReasonById(@PathVariable Long id) {
        return categoryDataService.getReasonById(id);
    }
}
