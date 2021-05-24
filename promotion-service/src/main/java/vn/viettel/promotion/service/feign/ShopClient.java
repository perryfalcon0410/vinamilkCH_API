package vn.viettel.promotion.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.dto.ShopParamDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.messaging.ShopParamRequest;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

@Service
@FeignClientAuthenticate(name = "authorization-service")
public interface ShopClient {
    @GetMapping("/api/v1/users/shops/shop-params")
    Response<ShopParamDTO> getShopParamV1(@RequestParam String type, @RequestParam String code, @RequestParam Long shopId);

    @PutMapping("/api/v1/users/shops/shop-params-1/{id}")
    Response<ShopParamDTO> updateShopParamV1(@RequestBody ShopParamRequest request, @PathVariable Long id);

}
