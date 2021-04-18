package vn.viettel.sale.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.viettel.core.db.entity.common.CustomerType;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

@Service
@FeignClientAuthenticate(name = "customer-service")
public interface CustomerTypeClient {
    @GetMapping("/api/customers/customer-types/shop-id/{id}")
    CustomerType getCusTypeIdByShopId(@PathVariable("id") Long id);

    @GetMapping("/api/customers/customer-types/default")
    Response<CustomerType> getCustomerTypeDefault();
}
