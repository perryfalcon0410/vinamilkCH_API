package vn.viettel.sale.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;
import vn.viettel.core.db.entity.authorization.User;
import vn.viettel.core.db.entity.common.CustomerType;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

@Service
@FeignClientAuthenticate(name = "customer-service")
public interface CustomerTypeClient {
    @GetMapping("/api/customer/customer-type/custype/{shopId}")
    CustomerType getCusTypeIdByShopId(@PathVariable("shopId") Long shopId);
}
