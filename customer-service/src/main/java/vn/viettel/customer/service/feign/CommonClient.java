package vn.viettel.customer.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;
import vn.viettel.customer.service.dto.DistrictDTO;
import vn.viettel.customer.service.dto.ProvinceDTO;

import java.util.List;

@Service
@FeignClientAuthenticate(name = "common-service")
public interface CommonClient {

    @GetMapping("/api/common/province/find-by-ids")
    Response<List<ProvinceDTO>> getAllProvinceByIds(@RequestParam(value = "ids") List<Long> ids);

    @GetMapping("/api/common/district/find-by-ids")
    Response<List<DistrictDTO>> getAllDistrictByIds(@RequestParam(value = "ids") List<Long> ids);
}
