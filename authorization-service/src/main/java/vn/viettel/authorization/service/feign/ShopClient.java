package vn.viettel.authorization.service.feign;

import vn.viettel.core.messaging.Response;
import vn.viettel.authorization.service.dto.shop.CustomerShopAddressDTO;
import vn.viettel.authorization.service.dto.shop.CustomerShopInformationDTO;
import vn.viettel.authorization.service.dto.shop.ShopDTO;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

@FeignClientAuthenticate(name = "salon-service")
public interface ShopClient {

    @GetMapping("/exist/{id}")
    Boolean shopExist(@PathVariable("id") Long id);

    @GetMapping("/customer/shop-information/{shopCode}")
    Response<CustomerShopInformationDTO> getShopInformation(@Valid @PathVariable(name = "shopCode", required = true) String shopCode);

    @GetMapping("/customer/shop-address/{shopCode}")
    Response<CustomerShopAddressDTO> getShopAddress(@Valid @PathVariable(name = "shopCode", required = true) String shopCode);

    @GetMapping("/customer/valid-shop-code/{shopCode}")
    Response<String> validShopCode(@Valid @PathVariable(name = "shopCode", required = true) String shopCode);

    @GetMapping("/find-by-shop-id/{id}")
    Response<ShopDTO> findById(@PathVariable("id") Long id);

    @GetMapping("/get-shop-by-id-and-user-id")
    Response<ShopDTO> getShopByIdAndUserId(@RequestParam("shopId") Long shopId, @RequestParam("userId") Long userId);

    @GetMapping("/get-by-shop-id")
    ShopDTO getShopById(@RequestParam("id") Long shopId);

    @GetMapping("/get-by-distributor-id")
    Response<ShopDTO> getByDistributorId(@RequestParam("shopId") Long shopId, @RequestParam(name = "distributorId") Long distributorId);
}
