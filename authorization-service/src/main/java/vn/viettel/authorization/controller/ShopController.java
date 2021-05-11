package vn.viettel.authorization.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.authorization.service.ShopService;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleFeign;

@RestController
public class ShopController extends BaseController {
    @Autowired
    ShopService shopService;

    private final String root = "/users/shops";

    @GetMapping(value = { V1 + root + "/{id}"})
    public Response<ShopDTO> getById(@PathVariable Long id) {
        return shopService.getById(id);
    }

    @GetMapping(value = { V1 + root})
    public Response<ShopDTO> getByName(@RequestParam String name) {
        return shopService.getByName(name);
    }

    @RoleFeign
    @GetMapping(value = V1 + root + "/editable/online-order/{shopId}")
    public Response<Boolean> isEditableOnlineOrder(@PathVariable Long shopId) {
        return shopService.isEditableOnlineOrder(shopId);
    }

    @RoleFeign
    @GetMapping(value = V1 + root + "/manually-creatable/online-order/{shopId}")
    Response<Boolean> isManuallyCreatableOnlineOrder(@PathVariable Long shopId) {
        return shopService.isManuallyCreatableOnlineOrder(shopId);
    }

//    @RoleFeign
    @GetMapping(value = V1 + root + "/day-return/{shopId}")
    Response<String> dayReturn(@PathVariable Long shopId) {
        return shopService.dayReturn(shopId);
    }
}
