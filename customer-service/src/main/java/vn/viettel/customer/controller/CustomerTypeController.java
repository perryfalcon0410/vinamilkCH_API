package vn.viettel.customer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.customer.entities.CustomerType;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.core.security.anotation.RoleFeign;
import vn.viettel.customer.service.CustomerTypeService;

import java.util.List;

@RestController
@RequestMapping("/api/customers/customer-types")
public class CustomerTypeController extends BaseController {
    @Autowired
    CustomerTypeService customerTypeService;

    @RoleAdmin
    @GetMapping
    public Response<List<CustomerTypeDTO>> getAll() {
        return customerTypeService.getAll();
    }

    @GetMapping("/shop-id/{shopId}")
    public CustomerTypeDTO getCusTypeIdByShopId(@PathVariable Long shopId) {
        return customerTypeService.getCusTypeByShopId(shopId);
    }

    @RoleFeign
    @RoleAdmin
    @GetMapping("/default")
    public Response<CustomerTypeDTO> getCustomerTypeDefault() {
        return customerTypeService.getCustomerTypeDefaut();
    }

}
