package vn.viettel.customer.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

import java.util.List;

@Service
@FeignClientAuthenticate(name = "common-service")
public interface ApParamClient {
    @GetMapping("api/v1/commons/apparams/{id}")
    Response<ApParamDTO> getApParamByIdV1(@PathVariable Long id);

    @GetMapping("api/v1/commons/apparams/cardtypes")
    Response<List<ApParamDTO>> getCardTypesV1();

    @GetMapping("api/v1/commons/apparams/closelytypes")
    Response<List<ApParamDTO>> getCloselytypesV1();
}
