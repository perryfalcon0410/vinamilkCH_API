package vn.viettel.authorization.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.authorization.service.ShopService;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.messaging.Response;

@RestController
@RequestMapping("api/user")
public class ShopController {
    @Autowired
    ShopService shopService;

    @GetMapping("shop/{id}")
    public Response<ShopDTO> getById(@PathVariable Long id) {
        return shopService.getById(id);
    }
    @GetMapping("shop")
    public Response<ShopDTO> getByName(@RequestParam String name) {
        return shopService.getByName(name);
    }
}
