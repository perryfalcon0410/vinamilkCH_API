package vn.viettel.authorization.controller;

import vn.viettel.core.messaging.Response;
import vn.viettel.authorization.messaging.authorization.UserForgotPasswordRequest;
import vn.viettel.authorization.messaging.authorization.UserLoginRequest;
import vn.viettel.authorization.messaging.authorization.UserUpdateForgotPasswordRequest;
import vn.viettel.authorization.service.ReceptionAuthenticateService;
import vn.viettel.authorization.service.dto.authorization.ReceptionUserLoginDTO;
import vn.viettel.core.db.entity.commonEnum.Feature;
import vn.viettel.core.security.anotation.Feature.OnFeature;
import vn.viettel.core.exception.FeatureNotAvailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/reception")
public class ReceptionAuthenticationController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    ReceptionAuthenticateService service;

    @OnFeature(feature = Feature.RECEPTION)
    @PostMapping("/login")
    public Response<ReceptionUserLoginDTO> login(@Valid @RequestBody UserLoginRequest request) throws FeatureNotAvailableException {
        logger.info("[login()] - reception login #email: {}", request.getEmail());
        return service.login(request);
    }

    @OnFeature(feature = Feature.RECEPTION)
    @PostMapping("/forgot")
    public Response<String> forgotPassword(@Valid @RequestBody UserForgotPasswordRequest request) throws FeatureNotAvailableException {
        logger.info("[forgotPassword()] - reception forgot password #email: {}", request.getEmail());
        return service.forgotPassword(request);
    }

    @GetMapping("/valid-reset-password-token")
    public Response<String> checkValidResetPasswordToken(@RequestParam(value = "token", required = true) String token) {
        logger.info("[checkValidResetPasswordToken()] - reception check valid reset password token #token: {}", token);
        return service.checkValidResetPasswordToken(token);
    }

    @PostMapping("/update-forgot-password")
    public Response<String> updateForgotPassword(@Valid @RequestBody UserUpdateForgotPasswordRequest request) {
        logger.info("[updateForgotPassword()] - reception update forgot password token #token: {}", request.getToken());
        return service.updateForgotPassword(request);
    }
}
