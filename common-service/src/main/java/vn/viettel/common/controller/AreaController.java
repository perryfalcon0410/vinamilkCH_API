package vn.viettel.common.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.common.messaging.AreaSearch;
import vn.viettel.common.service.AreaService;
import vn.viettel.common.service.dto.AreaDefaultDTO;
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
        List<AreaDTO> areaDTOS = areaService.getProvinces();
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.GET_DATA_PROVINCES_SUCCESS);
        return new Response<List<AreaDTO>>().withData(areaDTOS);
    }

    @ApiOperation(value = "Địa bàn mặc định của shop")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping( value = { V1 + root + "/default", V2 + root + "/default"})
    public Response<AreaDefaultDTO> getAreaDefault(HttpServletRequest httpRequest) {
        AreaDefaultDTO areaDefaultDTO = areaService.getAreaDefault(this.getShopId(httpRequest));
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.GET_DATA_AREA_SUCCESS);
        return new Response<AreaDefaultDTO>().withData(areaDefaultDTO);
    }

    @ApiOperation(value = "Danh sách Khu vực sử dụng trong tìm kiếm customer")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root + "/districts/index-customers"})
    public Response<List<AreaSearch>> getDistrictsToSearchCustomer(HttpServletRequest httpRequest) {
        List<AreaSearch> areaSearches = areaService.getDistrictsToSearchCustomer(this.getShopId(httpRequest));
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.GET_DATA_DISTRICTS_SUCCESS);

        return new Response<List<AreaSearch>>().withData(areaSearches);
    }

    @ApiOperation(value = "Danh sách Quận/Huyện theo Id Tỉnh/Tp")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root + "/districts"})
    public Response<List<AreaDTO>> getDistrictsByProvinceId(HttpServletRequest httpRequest, @RequestParam("provinceId") Long provinceId) {
        List<AreaDTO> areaDTOS = areaService.getDistrictsByProvinceId(provinceId);
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.GET_DATA_DISTRICTS_SUCCESS);
        return new Response<List<AreaDTO>>().withData(areaDTOS);
    }

    @ApiOperation(value = "Danh sách Phường/Xã theo Id Quận/Huyện")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root + "/precincts"})
    public Response<List<AreaDTO>> getPrecinctsByDistrictId(HttpServletRequest httpRequest, @RequestParam("districtId")Long districtId) {
        List<AreaDTO> areaDTOS = areaService.getPrecinctsByDistrictId(districtId);
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.GET_DATA_PRECINCT_SUCCESS);
        return new Response<List<AreaDTO>>().withData(areaDTOS);
    }

    @ApiOperation(value = "Khu vực theo id")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root + "/{id}"})
    public Response<AreaDTO> getById(HttpServletRequest httpRequest,
                                     @PathVariable Long id) {
        AreaDTO areaDTO = areaService.getAreaById(id);
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.GET_DATA_AREA_SUCCESS);
        return new Response<AreaDTO>().withData(areaDTO);
    }


    @GetMapping(value = { V1 + root + "/find"})
    @ApiOperation(value = "Tìm kiếm đơn địa chỉ khách hàng trong tạo khách hàng đơn online")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<AreaDTO> getArea(HttpServletRequest request, @ApiParam("Tên tỉnh, thành phố")
                                    @RequestParam("provinceName") String provinceName,
                                    @ApiParam("Tên quận, huyên")
                                    @RequestParam("districtName") String districtName,
                                    @ApiParam("Tên xã, phường, thị trấn")
                                    @RequestParam("precinctName") String precinctName) {
        AreaDTO areaDTO = areaService.getArea(provinceName, districtName, precinctName);
        LogFile.logToFile(appName, getUsername(request), LogLevel.INFO, request, LogMessage.FIND_ONLINE_ORDERS_SUCCESS);
        return new Response<AreaDTO>().withData(areaDTO);
    }
}
