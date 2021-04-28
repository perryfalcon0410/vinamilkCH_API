package vn.viettel.gateway.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

@Service
@FeignClientAuthenticate(name = "sale-service")
public interface SaleClient {

    @GetMapping("/sales/url")
    String getURLValue();
}
