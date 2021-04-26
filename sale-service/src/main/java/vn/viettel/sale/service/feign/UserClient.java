package vn.viettel.sale.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.viettel.core.dto.UserDTO;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;
import vn.viettel.core.service.dto.PermissionDTO;

import java.util.List;

@Service
@FeignClientAuthenticate(name = "authorization-service")
public interface UserClient {
    @GetMapping("api/v1/users/findById/{id}")
    UserDTO getUserById(@PathVariable("id") long id);

    @GetMapping("api/v1/users/get-user-permission/{roleId}")
    List<PermissionDTO> getUserPermission(@PathVariable Long roleId);

}
