package vn.viettel.customer.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

@Service
@FeignClientAuthenticate(name = "common-service")
public interface AddressClient {

    @GetMapping("api/location/country/{id}")
    String getCountry(@PathVariable long id);

    @GetMapping("api/location/province/{id}")
    String getProvince(@PathVariable long id);

    @GetMapping("api/location/district/{id}")
    String getDistrict(@PathVariable long id);

    @GetMapping("api/location/ward/{id}")
    String getWard(@PathVariable long id);

    @GetMapping("api/location/address/{id}")
    String getAddress(@PathVariable long id);
}
