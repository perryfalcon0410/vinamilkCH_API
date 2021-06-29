package vn.viettel.sale.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

import java.util.List;

@Service
@FeignClientAuthenticate(name = "common-service")
public interface ApparamClient {
    @GetMapping("api/v1/commons/apparams/reason-adjust/{id}")
    Response<ApParamDTO> getReasonV1(@PathVariable Long id);

    @GetMapping("api/v1/commons/apparams")
    Response<List<ApParamDTO>> getApParamsV1();

    @GetMapping("api/v1/commons/apparams/getByCode/{code}")
    Response<ApParamDTO> getApParamByCodeV1(@PathVariable String code);

    @GetMapping("api/v1/commons/apparams/type/{type}")
    Response<List<ApParamDTO>> getApParamByTypeV1(@PathVariable String type);

    @GetMapping(value = {"api/v1/commons/apparams/type-value"})
    Response<ApParamDTO> getApParamByTypeAndvalue(@RequestParam String type, @RequestParam String value);
}
