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
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.SEARCH_CUSTOMER_TYPE_SUCCESS);
        return customerTypeService.getAll();
    }

    @ApiOperation(value = "Tìm kiếm Customer type bằng shopId")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root + "/shop-id/{shopId}"})
    public CustomerTypeDTO getCusTypeByShopId(@PathVariable Long shopId) {
        return customerTypeService.getCusTypeByShopId(shopId);
    }

    @ApiOperation(value = "Tìm kiếm Customer type mặc định")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @RoleFeign
    @GetMapping(value = { V1 + root + "/default"})
    public Response<CustomerTypeDTO> getCustomerTypeDefault() {
        return customerTypeService.getCustomerTypeDefaut();
    }

    @ApiOperation(value = "Tìm kiếm Ware house type bằng customerId")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @RoleFeign
    @GetMapping(V1+ root + "/warehouse-type/{id}")
    public Response<Long> getWarehouseTypeIdByCustomer(@PathVariable Long id) {
        return new Response<Long>().withData(customerTypeService.getWarehouseTypeIdByCustomer(id));
    }
}
