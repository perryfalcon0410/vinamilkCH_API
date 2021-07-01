package vn.viettel.customer.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

@Service
@FeignClientAuthenticate(name = "authorization-service")
public interface ShopClient {
    @GetMapping("/api/v1/users/shops/{id}")
    Response<ShopDTO> getShopByIdV1(@PathVariable Long id);

    @PostMapping(value = { "/api/v1/users/shops/level-customer"})
    Response<Long> getLevelUpdateCustomerV1(@RequestParam Long shopId);
}
