package vn.viettel.sale.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.ShopParamDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;
import vn.viettel.core.security.anotation.RoleFeign;

import java.util.Map;

@Service
@FeignClientAuthenticate(name = "authorization-service")
public interface ShopClient {

    @GetMapping("api/v1/users/shops/{id}")
    Response<ShopDTO> getByIdV1(@PathVariable Long id);

    @GetMapping("api/v1/users/shops/editable/online-order/{shopId}")
    Response<Boolean> isEditableOnlineOrderV1(@PathVariable Long shopId);

    @RoleFeign
    @GetMapping( "api/v1/users/shops/manually-creatable/online-order/{shopId}")
    Response<Boolean> isManuallyCreatableOnlineOrderV1(@PathVariable Long shopId);

    @GetMapping("api/v1/users/shops/day-return/{shopId}")
    Response<String> dayReturn(@PathVariable Long shopId);

    @RoleFeign
    @GetMapping("api/v1/users/shops/import-trans-return/{shopId}")
    ShopParamDTO getImportSaleReturn(@PathVariable Long shopId);

    @GetMapping("api/v1/users/shops/code/{code}")
    Response<ShopDTO> getByShopCode(@PathVariable String code);

    @GetMapping(value = "api/v1/users/shops/feign/shops")
    Response<Map<Integer, ShopDTO>> getAllShopToRedInvoiceV1();
}
