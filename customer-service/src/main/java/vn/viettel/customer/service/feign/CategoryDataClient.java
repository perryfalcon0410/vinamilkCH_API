package vn.viettel.customer.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.viettel.core.dto.common.CategoryDataDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

import java.util.List;

@Service
@FeignClientAuthenticate(name = "common-service")
public interface CategoryDataClient {
    @GetMapping("api/common/categorydata/{id}")
    Response<CategoryDataDTO> getCategoryDataById(@PathVariable("id") Long id);

    @GetMapping("api/common/categorydata/genders")
    Response<List<CategoryDataDTO>> getGenders();
}
