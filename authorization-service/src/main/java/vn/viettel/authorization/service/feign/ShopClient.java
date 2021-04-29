package vn.viettel.authorization.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.viettel.authorization.entities.Shop;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

@Service
@FeignClientAuthenticate(name = "sale-service")
public interface ShopClient {
    @GetMapping("/api/v1/sales/get-shop-by-id/{id}")
    Response<Shop> getShopByIdV1(@PathVariable long id);
}
