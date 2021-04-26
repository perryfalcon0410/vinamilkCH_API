package vn.viettel.authorization.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.viettel.core.dto.common.AreaDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

@Service
@FeignClientAuthenticate(name = "common-service")
public interface AreaClient {
    @GetMapping("/api/v1/commons/areas/{id}")
    Response<AreaDTO> getById(@PathVariable Long id);

}
