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
    @GetMapping("api/sale/apparam/{id}")
    Response<ApParamDTO> getApParamById(@PathVariable Long id);

    @GetMapping("api/sale/apparam/cardtypes")
    Response<List<ApParamDTO>> getCardTypes();

    @GetMapping("api/sale/apparam/closelytypes")
    Response<List<ApParamDTO>> getCloselytypes();
}
