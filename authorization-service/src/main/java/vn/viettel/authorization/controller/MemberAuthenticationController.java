package vn.viettel.authorization.controller;

import vn.viettel.core.messaging.Response;
import vn.viettel.authorization.messaging.member.*;
import vn.viettel.authorization.service.MemberAuthenticateService;
import vn.viettel.authorization.service.dto.authorization.MemberUserLoginDTO;
import vn.viettel.core.db.entity.commonEnum.Feature;
import vn.viettel.core.security.anotation.Feature.OnFeature;
import vn.viettel.core.exception.FeatureNotAvailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.authorization.messaging.member.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/member")
public class MemberAuthenticationController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    MemberAuthenticateService service;

    @OnFeature(feature = Feature.RESERVATION)
    @PostMapping("/login")
    public Response<MemberUserLoginDTO> login(@Valid @RequestBody MemberLoginRequest request) throws FeatureNotAvailableException {
        logger.info("[login()] - member login #email: {}", request.getEmail());
        return service.login(request);
    }

    @OnFeature(feature = Feature.RESERVATION)
    @PostMapping("/forgot")
    public Response<String> forgotPassword(@Valid @RequestBody MemberForgotPasswordRequest request) throws Exception {
        logger.info("[forgotPassword()] - member forgot password #member: {}", request.getEmail());
        return service.forgotPassword(request);
    }

    @GetMapping("/valid-reset-password-token")
    public Response<String> checkValidResetPasswordToken(@RequestParam(value = "token", required = true) String token, @RequestParam(value = "companySlug", required = true) String companySlug, @RequestParam(value = "salonSlug", required = true) String salonSlug) {
        logger.info("[checkValidResetPasswordToken()] - member check valid reset password token #token: {}", token);
        return service.checkValidResetPasswordToken(token, companySlug, salonSlug);
    }

    @PostMapping("/update-forgot-password")
    public Response<String> updateForgotPassword(@Valid @RequestBody MemberUpdateForgotPasswordRequest request) {
        logger.info("[updateForgotPassword()] - member update forgot password token #token: {}", request.getToken());
        return service.updateForgotPassword(request);
    }

    @PostMapping("/send-activation-code")
    public Response<String> sendActivationCode(@Valid @RequestBody MemberSendEmailRegistrationRequest request) {
        logger.info("[sendActivationCode()] - send email for customer register account with  #email: {}", request.getEmail());
        return service.sendActivationCode(request);
    }

    @GetMapping("/valid-activation-code")
    public Response<String> checkValidActivationCode(@RequestParam(value = "email", required = true) String email,
                                                     @RequestParam(value = "companySlug", required = true) String companySlug,
                                                     @RequestParam(value = "salonSlug", required = true) String salonSlug,
                                                     @RequestParam(value = "token", required = true) String token) {
        logger.info("[checkValidActivationCode()] -  for customer register account token #token: {}", token);
        return service.checkValidActivationCode(email, companySlug, salonSlug, token);
    }

    @PostMapping("/precheckRegister")
    public Response<MemberRegisterResponse> precheckRegisterAccount(@Valid @RequestBody MemberRegisterRequest request) {
        logger.info("[precheckRegisterAccount()] - precheck customer register account name #name: {}", request.getName());
        return service.precheckRegisterAccount(request);
    }

    @PostMapping("/register")
    public Response<MemberRegisterResponse> registerAccount(@Valid @RequestBody MemberRegisterRequest request) {
        logger.info("[registerAccount()] - customer register account name #name: {}", request.getName());
        return service.registerAccount(request);
    }
}
