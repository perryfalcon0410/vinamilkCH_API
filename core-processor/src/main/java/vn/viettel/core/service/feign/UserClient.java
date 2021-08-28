package vn.viettel.core.service.feign;

import io.jsonwebtoken.Claims;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.dto.UserDTO;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

import java.util.List;

@FeignClientAuthenticate(name = "authorization-service")
public interface UserClient {

    @RequestMapping(value = "/api/token/feignGenerateContinueToken", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String generateContinueToken(@RequestBody Claims claims);

    @GetMapping("/api/token/feignSave")
    public void storeToken(@RequestParam("token") String token);

    @GetMapping("/api/token/feignGetBlackListToken")
    public boolean getBlackListToken(@RequestParam("token") String token);

    @GetMapping("/api/v1/users/get-data-user")
    List<UserDTO> getUserDataV1(@RequestParam("shopId") Long shopId);
}
