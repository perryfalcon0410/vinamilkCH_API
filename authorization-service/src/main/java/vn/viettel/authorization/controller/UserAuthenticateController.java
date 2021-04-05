package vn.viettel.authorization.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.authorization.repository.RoleRepository;
import vn.viettel.authorization.service.UserAuthenticateService;
import vn.viettel.authorization.service.dto.ChangePasswordRequest;
import vn.viettel.authorization.service.dto.LoginRequest;
import vn.viettel.authorization.service.dto.LoginResponse;
import vn.viettel.authorization.service.dto.ShopDTO;
import vn.viettel.core.db.entity.authorization.User;
import vn.viettel.core.handler.HandlerException;
import vn.viettel.core.messaging.Response;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserAuthenticateController extends HandlerException {

    @Autowired
    private UserAuthenticateService userLoginService;
    @Autowired
    RoleRepository repo;

    Logger logger = LoggerFactory.getLogger(getClass());

    @PostMapping("/preLogin")
    public Response<LoginResponse> preLogin(@Valid @RequestBody LoginRequest loginInfo,
                                            @RequestParam(value = "captcha", required = false) String captcha) {
        return userLoginService.preLogin(loginInfo, captcha);
    }

    @PostMapping("/login")
    public Response<LoginResponse> userLogin(@Valid @RequestBody LoginRequest loginInfo) {
        return userLoginService.login(loginInfo);
    }

    @PutMapping("/change-password")
    public Response<String> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        return userLoginService.changePassword(request);
    }

    @GetMapping("/findById/{id}")
    public User getUserById(@PathVariable long id) {
        return userLoginService.getUserById(id);
    }

    @GetMapping("/get-shop-by-role/{roleId}")
    public List<ShopDTO> getShopByRole(@PathVariable long roleId) {
        return userLoginService.getShopByRole(roleId);
    }

}
