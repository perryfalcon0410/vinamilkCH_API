package vn.viettel.customer.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.Response;
import vn.viettel.customer.service.CustomerService;
import vn.viettel.customer.service.CustomerTypeService;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
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
    public Response<List<CustomerTypeDTO>> getAll(HttpServletRequest httpRequest,
                                                  @RequestParam(value = "isCreate",defaultValue = "false") Boolean isCreate) {
        List<CustomerTypeDTO> customerTypeDTOS = customerTypeService.getAll(isCreate);
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.SEARCH_CUSTOMER_TYPE_SUCCESS);
        return new Response<List<CustomerTypeDTO>>().withData(customerTypeDTOS);
    }

    @ApiOperation(value = "Tìm kiếm Customer type của khách hàng mặc định bằng shopId")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root + "/shop-id/{shopId}"})
    public CustomerTypeDTO getCusTypeByShopId( @PathVariable Long shopId) {
        CustomerTypeDTO customerTypeDTO = customerTypeService.getCusTypeByShopId(shopId);
        return customerTypeDTO;
    }

    @ApiOperation(value = "Tìm kiếm Customer type mặc định")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root + "/default"})
    public Response<CustomerTypeDTO> getCustomerTypeDefault() {
        CustomerTypeDTO customerTypeDTO = customerTypeService.getCustomerTypeDefaut();
        return new Response<CustomerTypeDTO>().withData(customerTypeDTO);
    }

    @ApiOperation(value = "Tìm kiếm loại kho của shop")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(V1+ root + "/warehouse-type/shop/{shopId}")
    public Long getWarehouseTypeByShopId( @PathVariable Long shopId) {
        Long aLong = customerTypeService.getWarehouseTypeByShopId(shopId);
        return aLong;
    }

    @ApiOperation(value = "Tìm kiếm Customer type theo id")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root + "/{id}"})
    public CustomerTypeDTO getCusTypeById( @PathVariable Long id) {
        if(id == null) return null;
        List<CustomerTypeDTO> customerTypes = customerTypeService.findByIds(Arrays.asList(id));
        if(customerTypes == null || customerTypes.isEmpty()) return null;

        return customerTypes.get(0);
    }

    @ApiOperation(value = "Tìm kiếm Customer type theo kho")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root + "/getbywarehouse"})
    public List<CustomerTypeDTO> getCusTypeByWarehouse( @RequestParam Long warehouseId) {
        if(warehouseId == null) return new ArrayList<>();
        List<CustomerTypeDTO> customerTypes = customerTypeService.findByWarehouse(warehouseId);
        if(customerTypes == null || customerTypes.isEmpty()) return new ArrayList<>();

        return customerTypes;
    }

    @ApiOperation(value = "Tìm kiếm Customer type bán hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root + "/sale-order"})
    public CustomerTypeDTO getCustomerTypeForSale(@RequestParam(required = false) Long customerId, @RequestParam(required = false) Long shopId) {
        CustomerTypeDTO customerType = customerTypeService.getCustomerType(customerId, shopId);
        return customerType;
    }


    public void setService(CustomerTypeService service) {
        if( customerTypeService == null) customerTypeService = service;
    }
}
