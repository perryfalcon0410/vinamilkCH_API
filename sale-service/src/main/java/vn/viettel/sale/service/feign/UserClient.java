package vn.viettel.sale.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.viettel.core.db.entity.authorization.User;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;
import vn.viettel.core.service.dto.PermissionDTO;

import java.util.List;

@Service
@FeignClientAuthenticate(name = "authorization-service")
public interface UserClient {
    @GetMapping("api/user/findById/{id}")
    User getUserById(@PathVariable("id") long id);

    @GetMapping("api/user/get-user-permission/{roleId}")
    List<PermissionDTO> getUserPermission(@PathVariable Long roleId);

}
