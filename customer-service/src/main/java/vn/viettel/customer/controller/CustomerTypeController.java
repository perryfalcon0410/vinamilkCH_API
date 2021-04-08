package vn.viettel.customer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.db.entity.common.Area;
import vn.viettel.core.db.entity.common.CustomerType;
import vn.viettel.core.messaging.Response;
import vn.viettel.customer.service.CustomerTypeService;

import java.util.List;

@RestController
@RequestMapping("/api/customer/customer-type")
public class CustomerTypeController extends BaseController {
    @Autowired
    CustomerTypeService customerTypeService;

    @GetMapping
    public Response<List<CustomerType>> getAll() {
        return customerTypeService.getAll();
    }
    @GetMapping("/custype/{shopId}")
    public CustomerType getCusTypeIdByShopId(@PathVariable Long shopId) {
        return customerTypeService.getCusTypeByShopId(shopId);
    }
}
