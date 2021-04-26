package vn.viettel.authorization.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.authorization.service.ShopService;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.messaging.Response;

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
}
