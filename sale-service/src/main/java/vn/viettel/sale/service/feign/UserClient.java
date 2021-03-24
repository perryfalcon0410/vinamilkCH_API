package vn.viettel.sale.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.viettel.core.db.entity.authorization.User;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

@Service
@FeignClientAuthenticate(name = "authorization-service")
public interface UserClient {
    @GetMapping("api/user/findById/{id}")
    User getUserById(@PathVariable("id") long id);

    @GetMapping("api/user/findByUserName/{userName}")
    User getUserByUserName(@PathVariable("userName") String userName);
}
