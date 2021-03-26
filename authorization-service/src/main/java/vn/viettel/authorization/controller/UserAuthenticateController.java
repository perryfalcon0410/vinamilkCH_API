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
import vn.viettel.core.db.entity.User;
import vn.viettel.core.handler.HandlerException;
import vn.viettel.core.messaging.Response;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserAuthenticateController extends HandlerException {

    @Autowired
    private UserAuthenticateService userLoginService;
    @Autowired
    RoleRepository repo;

    Logger logger = LoggerFactory.getLogger(getClass());

    @PostMapping("/preLogin")
    public Response<LoginResponse> preLogin(@Valid @RequestBody LoginRequest loginInfo) {
        return userLoginService.preLogin(loginInfo);
    }

    @PostMapping("/login/{roleId}")
    public Response<LoginResponse> userLogin(@Valid @RequestBody LoginRequest loginInfo, @PathVariable long roleId) {
        return userLoginService.login(loginInfo, roleId);
    }

    @PutMapping("/change-password")
    public Response<String> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        return userLoginService.changePassword(request);
    }

    @GetMapping("/findById/{id}")
    public User getUserById(@PathVariable long id) {
        return userLoginService.getUserById(id);
    }

}
