package vn.viettel.authorization.controller;

import vn.viettel.core.messaging.Response;
import vn.viettel.authorization.messaging.authorization.UserForgotPasswordRequest;
import vn.viettel.authorization.messaging.authorization.UserLoginRequest;
import vn.viettel.authorization.messaging.authorization.UserUpdateForgotPasswordRequest;
import vn.viettel.authorization.messaging.user.AdminUserCreateRequest;
import vn.viettel.authorization.service.AdministratorAuthenticateService;
import vn.viettel.authorization.service.dto.authorization.AdminUserLoginDTO;
import vn.viettel.authorization.service.dto.user.AdminUserDTO;
import vn.viettel.authorization.service.dto.user.AdminUserEditDTO;
import vn.viettel.authorization.service.dto.user.UserAdminIndexDTO;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.user.AdminUserResponseDTO;
import vn.viettel.core.security.anotation.RoleFeign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin")
public class AdministratorAuthenticationController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    AdministratorAuthenticateService service;

    @PostMapping("/login")
    public Response<AdminUserLoginDTO> login(@Valid @RequestBody UserLoginRequest request) {
        logger.info("[login()] - administrator login #email: {}", request.getEmail());
        return service.login(request);
    }

    @PostMapping("/forgot")
    public Response<String> forgotPassword(@Valid @RequestBody UserForgotPasswordRequest request) {
        logger.info("[forgotPassword()] - administrator forgot password #email: {}", request.getEmail());
        return service.forgotPassword(request);
    }

    @GetMapping("/valid-reset-password-token")
    public Response<String> checkValidResetPasswordToken(@RequestParam(value = "token", required = true) String token) {
        logger.info("[checkValidResetPasswordToken()] - administrator check valid reset password token #token: {}", token);
        return service.checkValidResetPasswordToken(token);
    }

    @PostMapping("/update-forgot-password")
    public Response<String> updateForgotPassword(@Valid @RequestBody UserUpdateForgotPasswordRequest request) {
        logger.info("[updateForgotPassword()] - administrator update forgot password token #token: {}", request.getToken());
        return service.updateForgotPassword(request);
    }

    @GetMapping("/user/index")
    public Response<Page<UserAdminIndexDTO>> usersAdminIndex(@RequestParam(value = "q", required = false) String searchKeywords, Pageable pageable) {
        logger.info("[index()] - user admin index #searchKeywords: {}", searchKeywords);
        return service.usersAdminIndex(searchKeywords, pageable);
    }

    @PostMapping("/user/create")
    public Response<AdminUserDTO> create(@Valid @RequestBody AdminUserCreateRequest request) {
        logger.info("[add()] - add user #email: {}", request.getEmail());
        return service.create(request);
    }

    @GetMapping("/user/{id}/edit")
    public Response<AdminUserEditDTO> edit(@PathVariable("id") Long id) {
        logger.info("[edit()] - edit user #id: {}", id);
        return service.edit(id);
    }

    @PostMapping("/user/{id}/update")
    public Response<AdminUserDTO> update(@PathVariable("id") Long id,@Valid @RequestBody AdminUserCreateRequest request) {
        logger.info("[update()] - update user #id: {}", id);
        return service.update(id, request);
    }

    @PostMapping("/user/{id}/delete")
    public Response<AdminUserDTO> delete(@PathVariable("id") Long id) {
        logger.info("[delete()] - delete user #id: {}", id);
        return service.delete(id);
    }

    @RoleFeign
    @GetMapping("/getAdminById")
    public Response<AdminUserResponseDTO> getAdminById(@RequestParam("id") Long id) {
        logger.info("[getAdminById()] - get admin user #id: {}", id);
        return service.getAdminById(id);
    }
}
