package vn.viettel.sale.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import vn.viettel.core.dto.common.CategoryDataDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

import java.util.List;

@Service
@FeignClientAuthenticate(name = "common-service")
public interface CategoryDataClient {

    @GetMapping("/api/v1/commons/categorydata/get-by-group-code")
    Response<List<CategoryDataDTO>> getByCategoryGroupCodeV1();

    @GetMapping("/api/v1/commons/categorydata/feign/get-reason-exchange")
    Response<List<CategoryDataDTO>> getReasonExchangeFeign(@RequestParam(required = false) List<Long> customerIds,
                                   @RequestParam(required = false) String sortName, @RequestParam(required = false) String direction);
}
