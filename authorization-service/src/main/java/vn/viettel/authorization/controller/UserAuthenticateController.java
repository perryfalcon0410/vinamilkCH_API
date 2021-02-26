package vn.viettel.authorization.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.authorization.service.UserAuthenticateService;
import vn.viettel.authorization.service.dto.ChangePasswordRequest;
import vn.viettel.authorization.service.dto.LoginRequest;
import vn.viettel.authorization.service.dto.LoginResponse;
import vn.viettel.core.handler.HandlerException;
import vn.viettel.core.messaging.Response;

@RestController
@RequestMapping("/api/user")
public class UserAuthenticateController extends HandlerException {

    @Autowired
    private UserAuthenticateService userLoginService;

    Logger logger = LoggerFactory.getLogger(getClass());

    @PostMapping("/login")
    public Response<LoginResponse> userLogin(@RequestBody LoginRequest loginInfo) {
        return userLoginService.login(loginInfo);
    }

    @PutMapping("/change-password")
    public Response<String> changePassword(@RequestBody ChangePasswordRequest request) {
        return userLoginService.changePassword(request);
    }

}
