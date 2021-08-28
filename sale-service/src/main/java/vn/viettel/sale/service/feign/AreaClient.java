package vn.viettel.sale.service.feign;

import org.springframework.stereotype.Service;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

@Service
@FeignClientAuthenticate(name = "common-service")
public interface AreaClient {
//    @GetMapping("/api/v1/commons/areas/find")
//    Response<AreaDTO> getAreaV1(@RequestParam("provinceName") String provinceName,
//                                @RequestParam("districtName") String districtName,
//                                @RequestParam("precinctName") String precinctName);

}
