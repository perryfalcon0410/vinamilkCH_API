package vn.viettel.authorization.controller;

import vn.viettel.core.messaging.Response;
import vn.viettel.authorization.messaging.authorization.UserForgotPasswordRequest;
import vn.viettel.authorization.messaging.authorization.UserLoginRequest;
import vn.viettel.authorization.messaging.authorization.UserRegisterRequest;
import vn.viettel.authorization.messaging.authorization.UserUpdateForgotPasswordRequest;
import vn.viettel.authorization.messaging.user.ManagementUserCreateRequest;
import vn.viettel.authorization.service.ManagementAuthenticateService;
import vn.viettel.authorization.service.dto.authorization.UserLoginDTO;
import vn.viettel.authorization.service.dto.user.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.viettel.authorization.service.dto.user.ManagementUserDTO;
import vn.viettel.authorization.service.dto.user.ManagementUserEditDTO;
import vn.viettel.authorization.service.dto.user.ManagementUserIndexDTO;

import javax.validation.Valid;

@RestController
@RequestMapping("/management")
public class ManagementAuthenticationController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    ManagementAuthenticateService service;

    @PostMapping("/register")
    public Response<String> register(@Valid @RequestBody UserRegisterRequest request) {
        logger.info("[register()] - shop owner register #email: {}", request.getEmail());
        return service.register(request);
    }

    @PostMapping("/login")
    public Response<UserLoginDTO> login(@Valid @RequestBody UserLoginRequest request) {
        logger.info("[login()] - shop owner login #email: {}", request.getEmail());
        return service.login(request);
    }

    @PostMapping("/forgot")
    public Response<String> forgotPassword(@Valid @RequestBody UserForgotPasswordRequest request) {
        logger.info("[forgotPassword()] - shop owner forgot password #email: {}", request.getEmail());
        return service.forgotPassword(request);
    }

    @GetMapping("/valid-reset-password-token")
    public Response<String> checkValidResetPasswordToken(@RequestParam(value = "token", required = true) String token) {
        logger.info("[checkValidResetPasswordToken()] - shop owner check valid reset password token #token: {}", token);
        return service.checkValidResetPasswordToken(token);
    }

    @PostMapping("/update-forgot-password")
    public Response<String> updateForgotPassword(@Valid @RequestBody UserUpdateForgotPasswordRequest request) {
        logger.info("[updateForgotPassword()] - shop owner update forgot password token #token: {}", request.getToken());
        return service.updateForgotPassword(request);
    }

    @GetMapping("/user/index")
    public Response<Page<ManagementUserIndexDTO>> usersManagementIndex(@RequestParam(value = "q", required = false) String searchKeywords, @RequestParam(value = "companyId") Long idCompany, Pageable pageable) {
        logger.info("[index()] - user management index #searchKeywords: {}", searchKeywords);
        return service.usersManagementIndex(searchKeywords, idCompany, pageable);
    }

    @GetMapping("/user/{id}/edit")
    public Response<ManagementUserEditDTO> edit(@PathVariable("id") Long id) {
        logger.info("[edit()] - edit user #id: {}", id);
        return service.edit(id);
    }

    @GetMapping("/user/{id}/admin")
    public Response<ManagementUserEditDTO> getUserAdminByCompanyId(@PathVariable("id") Long id) {
        logger.info("[getUserAdminByCompanyId()] - edit company #id: {}", id);
        return service.getUserAdminByCompanyId(id);
    }

    @PostMapping("/user/create")
    public Response<ManagementUserDTO> create(@Valid @RequestBody ManagementUserCreateRequest request) {
        logger.info("[add()] - add user #email: {}", request.getEmail());
        return service.create(request);
    }

//    @RoleFeign
    @PostMapping("/user/{id}/update")
    public Response<ManagementUserDTO> update(@PathVariable("id") Long id,@Valid @RequestBody ManagementUserCreateRequest request) {
        logger.info("[update()] - update user #id: {}", id);
        return service.update(id, request);
    }

    @PostMapping("/user/{id}/delete")
    public Response<ManagementUserDTO> delete(@PathVariable("id") Long id) {
        logger.info("[delete()] - delete user #id: {}", id);
        return service.delete(id);
    }
}
