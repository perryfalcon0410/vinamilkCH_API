package vn.viettel.customer.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.viettel.core.db.entity.common.ApParam;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

import java.util.Optional;

@Service
@FeignClientAuthenticate(name = "sale-service")
public interface ApParamClient {
    @GetMapping("api/sale/apparam/findById/{id}")
    Optional<ApParam> getApParamById(@PathVariable Long id);
}
