package vn.viettel.customer.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.viettel.core.dto.UserDTO;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

@Service
@FeignClientAuthenticate(name = "authorization-service")
public interface UserClient {

    @GetMapping("api/v1/users/findById/{id}")
    UserDTO getUserByIdV1(@PathVariable("id") Long id);
}
