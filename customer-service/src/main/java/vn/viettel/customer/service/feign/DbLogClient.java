package vn.viettel.customer.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import vn.viettel.core.dto.common.AreaDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

import java.util.List;

@Service
@FeignClientAuthenticate(name = "common-service")
public interface DbLogClient {

    @PostMapping("/api/v1/commons/dblogs")
    Response<List<AreaDTO>> createV1();
}
