package vn.viettel.gateway.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

@Service
@FeignClientAuthenticate(name = "authorization-service")
public interface AuthClient {

    @GetMapping("/users/url")
    String getURLValue();

    @PostMapping(value = {"api/v1/users/permission-valid"})
    Boolean gateWayCheckPermissionType2(@RequestParam Long roleId, @RequestParam Long shopId);
}
