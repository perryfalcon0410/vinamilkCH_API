package vn.viettel.authorization.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.authorization.service.ShopService;
import vn.viettel.core.db.entity.common.Shop;
import vn.viettel.core.messaging.Response;

@RestController
@RequestMapping("api/user")
public class ShopController {
    @Autowired
    ShopService shopService;

    @GetMapping("shop/{id}")
    public Response<Shop> getById(@PathVariable Long id) {
        return shopService.getById(id);
    }
    @GetMapping("shop")
    public Response<Shop> getByName(@RequestParam String name) {
        return shopService.getByName(name);
    }
}
