package vn.viettel.customer.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleFeign;
import vn.viettel.customer.service.CustomerTypeService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class CustomerTypeController extends BaseController {
    @Autowired
    CustomerTypeService customerTypeService;
    private final String root = "/customers/customer-types";

    @ApiOperation(value = "Danh sách nhóm khách hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root})
    public Response<List<CustomerTypeDTO>> getAll(HttpServletRequest httpRequest) {
        List<CustomerTypeDTO> customerTypeDTOS = customerTypeService.getAll();
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.SEARCH_CUSTOMER_TYPE_SUCCESS);
        return new Response<List<CustomerTypeDTO>>().withData(customerTypeDTOS);
    }

    @ApiOperation(value = "Tìm kiếm Customer type bằng shopId")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root + "/shop-id/{shopId}"})
    public Response<CustomerTypeDTO> getCusTypeByShopId(HttpServletRequest httpRequest, @PathVariable Long shopId) {
        CustomerTypeDTO customerTypeDTO = customerTypeService.getCusTypeByShopId(shopId);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.FIND_CUSTOMER_TYPE_SUCCESS);
        return new Response<CustomerTypeDTO>().withData(customerTypeDTO);
    }

    @ApiOperation(value = "Tìm kiếm Customer type mặc định")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @RoleFeign
    @GetMapping(value = { V1 + root + "/default"})
    public Response<CustomerTypeDTO> getCustomerTypeDefault(HttpServletRequest httpRequest) {
        CustomerTypeDTO customerTypeDTO = customerTypeService.getCustomerTypeDefaut();
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.FIND_CUSTOMER_TYPE_SUCCESS);
        return new Response<CustomerTypeDTO>().withData(customerTypeDTO);
    }

    @ApiOperation(value = "Tìm kiếm Ware house type Id bằng customerId")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @RoleFeign
    @GetMapping(V1+ root + "/warehouse-type/{id}")
    public Response<Long> getWarehouseTypeIdByCustomer(HttpServletRequest httpRequest, @PathVariable Long id) {
        Long aLong = customerTypeService.getWarehouseTypeIdByCustomer(id);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.FIND_WARE_HOUSE_TYPE_SUCCESS);
        return new Response<Long>().withData(aLong);
    }
}
