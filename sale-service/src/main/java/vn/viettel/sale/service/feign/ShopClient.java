package vn.viettel.sale.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;
import vn.viettel.core.security.anotation.RoleFeign;

@Service
@FeignClientAuthenticate(name = "authorization-service")
public interface ShopClient {

    @GetMapping("api/v1/users/shops/{id}")
    Response<ShopDTO> getById(@PathVariable Long id);

    @GetMapping("api/v1/users/shops/editable/online-order/{shopId}")
    Response<Boolean> isEditableOnlineOrder(@PathVariable Long shopId);

    @RoleFeign
    @GetMapping( "api/v1/users/shops/manually-creatable/online-order/{shopId}")
    Response<Boolean> isManuallyCreatableOnlineOrder(@PathVariable Long shopId);

}
