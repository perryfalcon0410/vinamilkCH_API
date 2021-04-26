package vn.viettel.customer.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import vn.viettel.core.dto.common.AreaDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

import java.util.List;

@Service
@FeignClientAuthenticate(name = "common-service")
public interface AreaClient {
    @GetMapping("/api/v1/commons/areas/provinces")
    Response<List<AreaDTO>> getProvinces();

    @GetMapping("/api/v1/commons/areas/districts/province-id")
    Response<List<AreaDTO>> getDistrictsByProvinceId(@RequestParam("provinceId") Long provinceId);

    @GetMapping("/api/v1/commons/areas/precincts/district-id")
    Response<List<AreaDTO>> getPrecinctsByDistrictId(@RequestParam("districtId") Long districtId);

    @GetMapping("/api/v1/commons/areas/{id}")
    Response<AreaDTO> getById(@PathVariable Long id);
}