package vn.viettel.authorization.controller;

import vn.viettel.authorization.messaging.authorization.UserLoginForViewPageRequest;
import vn.viettel.authorization.messaging.customer.*;
import vn.viettel.authorization.service.CustomerAuthenticateService;
import vn.viettel.authorization.service.dto.authorization.CustomerLoginDTO;
import vn.viettel.authorization.service.dto.authorization.UserLoginDTO;
import vn.viettel.core.handler.HandlerException;
import vn.viettel.core.messaging.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.authorization.messaging.customer.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/customer")
public class CustomerAuthenticateController extends HandlerException {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    CustomerAuthenticateService service;

    @PostMapping("/register")
    public Response<String> register(@Valid @RequestBody CustomerRegisterRequest request) {
        logger.info("[register()] - register customer #email: {}", request.getEmail());
        return service.register(request);
    }

    @GetMapping("/valid-activation-token")
    public Response<String> checkValidActivationCode(@Valid CustomerValidActivationCodeRequest request) {
        logger.info("[checkValidActivationCode()] - check valid activation code #code: {}", request.getToken());
        return service.checkValidActivationCode(request);
    }

    @PostMapping("/forgot")
    public Response<String> forgotPassword(@Valid @RequestBody CustomerForgotPasswordRequest request) {
        logger.info("[forgotPassword()] - customer forgot password #email: {}", request.getEmail());
        return service.forgotPassword(request);
    }

    @GetMapping("/valid-reset-password-token")
    public Response<String> checkValidForgotPasswordToken(@Valid @RequestParam("token") String token) {
        logger.info("[checkValidForgotPasswordToken()] - check valid forgot password token #token: {}", token);
        return service.checkValidForgotPasswordToken(token);
    }

    @PostMapping("/update-forgot-password")
    public Response<String> updateForgotPassword(@Valid @RequestBody CustomerUpdatePasswordRequest request) {
        logger.info("[updateForgotPassword()] - update forgot password token #token: {}", request.getToken());
        return service.updateForgotPassword(request);
    }

    @PostMapping("/login")
    public Response<CustomerLoginDTO> login(@Valid @RequestBody CustomerLoginRequest request) {
        logger.info("[login()] - customer login #email: {}", request.getEmail());
        return service.login(request);
    }

    @PostMapping("/login-for-view-page")
    public Response<UserLoginDTO> loginForViewPage(@Valid @RequestBody UserLoginForViewPageRequest request) {
        logger.info("[loginForViewPage()] - login for view page #email: {}", request.getEmail());
        return service.loginForViewPage(request);
    }

}
