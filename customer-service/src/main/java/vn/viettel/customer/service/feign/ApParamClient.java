package vn.viettel.customer.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import vn.viettel.core.db.entity.common.ApParam;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

import java.util.List;
import java.util.Optional;

@Service
@FeignClientAuthenticate(name = "sale-service")
public interface ApParamClient {
    @GetMapping("api/sale/apparam/findById/{id}")
    Response<ApParam> getApParamById(@PathVariable Long id);

    @GetMapping("api/sale/apparam/cardtypes")
    Response<List<ApParam>> getCardTypes();

    @GetMapping("api/sale/apparam/closelytypes")
    Response<List<ApParam>> getCloselytypes();
}
