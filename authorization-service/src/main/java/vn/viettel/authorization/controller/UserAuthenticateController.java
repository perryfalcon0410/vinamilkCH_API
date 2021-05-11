package vn.viettel.authorization.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.authorization.repository.RoleRepository;
import vn.viettel.authorization.service.UserAuthenticateService;
import vn.viettel.authorization.service.dto.ChangePasswordRequest;
import vn.viettel.authorization.service.dto.LoginRequest;
import vn.viettel.authorization.service.dto.ShopDTO;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.UserDTO;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.core.security.anotation.RoleFeign;
import vn.viettel.core.service.dto.PermissionDTO;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
public class UserAuthenticateController extends BaseController {

    @Autowired
    private UserAuthenticateService userLoginService;
    @Autowired
    RoleRepository repo;
    private final String root = "/users";

    @PostMapping(value = { V1 + root + "/preLogin"})
    public Response<Object> preLogin(HttpServletRequest request, @Valid @RequestBody LoginRequest loginInfo,
                                     @RequestParam(value = "captcha", required = false) String captcha) {
        Response<Object> result = userLoginService.preLogin(loginInfo, captcha);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.LOGIN_SUCCESS);
        return result;
    }

    @PostMapping(value = { V1 + root + "/login"})
    public Response<Object> userLogin(HttpServletRequest request, @Valid @RequestBody LoginRequest loginInfo) {
        Response<Object> result = userLoginService.login(loginInfo);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.LOGIN_SUCCESS);
        return result;
    }

    @PutMapping(value = { V1 + root + "/change-password"})
    public Response<Object> changePassword(HttpServletRequest request, @Valid @RequestBody ChangePasswordRequest requestBody) {
        Response<Object> result = userLoginService.changePassword(requestBody);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.CHANGE_PASSWORD);
        return result;
    }

    @RoleAdmin
    @RoleFeign
    @GetMapping(value = { V1 + root + "/findById/{id}"})
    public UserDTO getUserById(@PathVariable long id) {
        return userLoginService.getUserById(id);
    }

    @RoleAdmin
    @RoleFeign
    @GetMapping(value = { V1 + root + "/get-shop-by-role/{roleId}"})
    public List<ShopDTO> getShopByRole(@PathVariable long roleId) {
        return userLoginService.getShopByRole(roleId);
    }

    @RoleAdmin
    @RoleFeign
    @GetMapping(value = { V1 + root + "/get-user-permission/{roleId}"})
    public List<PermissionDTO> getUserPermission(@PathVariable Long roleId) {
        return userLoginService.getUserPermission(roleId);
    }
}
