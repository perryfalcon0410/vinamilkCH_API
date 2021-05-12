package vn.viettel.common.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.common.messaging.AreaSearch;
import vn.viettel.common.service.AreaService;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.common.AreaDTO;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.Response;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class AreaController extends BaseController {
    @Autowired
    private AreaService areaService;
    private final String root = "/commons/areas";

    @ApiOperation(value = "Danh sách Tỉnh/Tp")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping( value = { V1 + root + "/provinces", V2 + root + "/provinces"})
    public Response<List<AreaDTO>> getProvinces(HttpServletRequest httpRequest) {
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.LOGIN_SUCCESS);
        return areaService.getProvinces();
    }

    @ApiOperation(value = "Danh sách Khu vực sử dụng trong tìm kiếm customer")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root + "/districts/index-customers"})
    public Response<List<AreaSearch>> getDistrictsToSearchCustomer(HttpServletRequest httpRequest) {
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.LOGIN_SUCCESS);
        return areaService.getDistrictsToSearchCustomer(this.getShopId());
    }

    @ApiOperation(value = "Danh sách Quận/Huyện theo Id Tỉnh/Tp")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root + "/districts"})
    public Response<List<AreaDTO>> getDistrictsByProvinceId(HttpServletRequest httpRequest, @RequestParam("provinceId") Long provinceId) {
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.LOGIN_SUCCESS);
        return areaService.getDistrictsByProvinceId(provinceId);
    }

    @ApiOperation(value = "Danh sách Phường/Xã theo Id Quận/Huyện")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root + "/precincts"})
    public Response<List<AreaDTO>> getPrecinctsByDistrictId(HttpServletRequest httpRequest, @RequestParam("districtId")Long districtId) {
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.LOGIN_SUCCESS);
        return areaService.getPrecinctsByDistrictId(districtId);
    }

    @GetMapping(value = { V1 + root + "/{id}"})
    public Response<AreaDTO> getById(@PathVariable Long id) {
        return areaService.getAreaById(id);
    }
}
