package vn.viettel.sale.service.feign;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import vn.viettel.core.db.entity.authorization.User;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;
import vn.viettel.core.service.dto.PermissionDTO;
import vn.viettel.customer.messaging.CustomerFilter;
import vn.viettel.customer.service.dto.CustomerDTO;

import java.util.Date;
import java.util.List;

@Service
@FeignClientAuthenticate(name = "authorization-service")
public interface UserClient {
    @GetMapping("api/user/findById/{id}")
    User getUserById(@PathVariable("id") long id);

    @GetMapping("api/user/get-user-permission/{roleId}")
    List<PermissionDTO> getUserPermission(@PathVariable Long roleId);

}
