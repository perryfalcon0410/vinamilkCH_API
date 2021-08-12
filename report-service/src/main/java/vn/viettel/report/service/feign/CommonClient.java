package vn.viettel.report.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.dto.common.CategoryDataDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;
import java.util.List;

@Service
@FeignClientAuthenticate(name = "common-service")
public interface CommonClient {
    @GetMapping("/api/v1/commons/categorydata/get-reason-exchange")
    Response<List<CategoryDataDTO>> getReasonExchangeV1();
    @GetMapping("/api/v1/commons/apparams/type/{type}")
    Response<List<ApParamDTO>> getApParamByTypeV1(@PathVariable String type);
}
