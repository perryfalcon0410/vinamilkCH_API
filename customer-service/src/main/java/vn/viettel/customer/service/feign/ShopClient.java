/*
package vn.viettel.customer.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.viettel.core.db.entity.Shop;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

@Service
@FeignClientAuthenticate(name = "sale-service")
public interface ShopClient {
    @GetMapping("/api/sale/get-shop-by-id/{id}")
    Response<Shop> getShopById(@PathVariable long id);
}
*/
