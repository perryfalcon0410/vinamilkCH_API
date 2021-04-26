package vn.viettel.sale.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

@Service
@FeignClientAuthenticate(name = "common-service")
public interface ApparamClient {
    @GetMapping("api/v1/commons/apparams/reason-adjust/{id}")
    ApParamDTO getReason(@PathVariable Long id);
}
