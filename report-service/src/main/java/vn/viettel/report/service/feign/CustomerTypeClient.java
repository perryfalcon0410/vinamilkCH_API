package vn.viettel.report.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

import java.util.List;

@Service
@FeignClientAuthenticate(name = "customer-service")
public interface CustomerTypeClient {
    @GetMapping("/api/v1/customers/customer-types/shop-id/{shopId}")
    CustomerTypeDTO getCusTypeIdByShopIdV1(@PathVariable("shopId") Long shopId);
}
