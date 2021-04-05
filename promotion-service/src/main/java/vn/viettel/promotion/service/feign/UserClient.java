package vn.viettel.promotion.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.viettel.core.db.entity.authorization.User;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

@Service
@FeignClientAuthenticate(name = "authorization-service")
public interface UserClient {

    @GetMapping("api/user/findById/{id}")
    User getUserById(@PathVariable("id") Long id);
}