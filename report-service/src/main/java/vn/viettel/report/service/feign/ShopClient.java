package vn.viettel.report.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import vn.viettel.core.db.entity.common.Shop;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

@Service
@FeignClientAuthenticate(name = "authorization-service")
public interface ShopClient {
    @GetMapping("/api/user/shop/{id}")
    Response<Shop> getShopById(@PathVariable Long id);

    @GetMapping("/api/user/shop")
    Response<Shop> getByName(@RequestParam String name);
}
