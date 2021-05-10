package vn.viettel.gateway.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;
import vn.viettel.core.service.dto.PermissionDTO;

import java.util.List;

@Service
@FeignClientAuthenticate(name = "authorization-service")
public interface AuthClient {

    @GetMapping("/users/url")
    String getURLValue();

    @GetMapping(value = {"api/v1/users/get-user-permission/{roleId}"})
    List<PermissionDTO> getUserPermission(@PathVariable Long roleId);
}
