package vn.viettel.core.service.feign;

import io.jsonwebtoken.Claims;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.dto.UserDTO;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

import java.util.List;

@FeignClientAuthenticate(name = "authorization-service")
public interface UserClient {

    @GetMapping("/api/v1/users/get-data-user")
    List<UserDTO> getUserDataV1(@RequestParam("shopId") Long shopId);
}
