package vn.viettel.gateway.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

@Service
@FeignClientAuthenticate(name = "customer-service")
public interface CustomerClient {

    @GetMapping("/customers/url")
    String getURLValue();
}
