package vn.viettel.authorization.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@Api(tags = "Api sử dụng cho đăng nhập và đổi mật khẩu")
public class UserAuthenticateController extends BaseController {

    @Autowired
    private UserAuthenticateService userLoginService;
    @Autowired
    RoleRepository repo;
    private final String root = "/users";

    @ApiOperation(value = "Api dùng khi đăng nhập, trả về danh sách vai trò và cửa hàng cho người dùng chọn để đăng nhập, nếu có 1 role được gắn trên 1 shop thì đăng nhập luôn")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping(value = { V1 + root + "/preLogin"})
    public Response<Object> preLogin(HttpServletRequest request, @Valid @RequestBody LoginRequest loginInfo) {
        Response<Object> result = userLoginService.preLogin(loginInfo);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.LOGIN_SUCCESS);
        return result;
    }

    @ApiOperation(value = "Api dùng khi chọn role và shop khi có nhiều role, shop")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping(value = { V1 + root + "/login"})
    public Response<Object> userLogin(HttpServletRequest request, @Valid @RequestBody LoginRequest loginInfo) {
        Response<Object> result = userLoginService.getRoleShop(loginInfo);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.LOGIN_SUCCESS);
        return result;
    }

    @ApiOperation(value = "Api dùng khi đổi mật khẩu")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PutMapping(value = { V1 + root + "/change-password"})
    public Response<Object> changePassword(HttpServletRequest request, @Valid @RequestBody ChangePasswordRequest requestBody) {
        Response<Object> result = userLoginService.changePassword(requestBody);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.CHANGE_PD);
        return result;
    }

    @ApiOperation(value = "Api dùng reload captcha mới")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PutMapping(value = { V1 + root + "/reload-captcha/{username}"})
    public Response<String> relloadCaptcha(HttpServletRequest request,@PathVariable String username) {
        String result = userLoginService.reloadCaptcha(username);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.RELOAD_CAPTCHA_SUCCESS);
        return new Response<String>().withData(result);
    }

    @ApiOperation(value = "Kiểm tra quyền dữ liệu của shop")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @RoleFeign
    @PostMapping(value = { V1 + root + "/permission-valid"})
    public Boolean gateWayCheckPermissionType2(@RequestParam Long roleId, @RequestParam Long shopId) {
        Boolean result = userLoginService.gateWayCheckPermissionType2(roleId, shopId);
        return result;
    }

    @RoleFeign
    @GetMapping(value = { V1 + root + "/findById/{id}"})
    public UserDTO getUserById(@PathVariable long id) {
        return userLoginService.getUserById(id);
    }

    @RoleFeign
    @GetMapping(value = { V1 + root + "/findByIds"})
    public List<UserDTO> getUserByIds(@RequestParam(required = false) List<Long> userIds) {
        return userLoginService.getUserByIds(userIds);
    }

    @RoleAdmin
    @RoleFeign
    @GetMapping(value = { V1 + root + "/get-shop-by-role/{roleId}"})
    public List<ShopDTO> getShopByRole(@PathVariable long roleId) {
        return userLoginService.getShopByRole(roleId);
    }

//    @RoleAdmin
//    @RoleFeign
//    @GetMapping(value = { V1 + root + "/get-user-permission/{roleId}"})
//    public List<PermissionDTO> getUserPermission(@PathVariable Long roleId) {
//        return userLoginService.getUserPermission(roleId, this.getShopId());
//    }

    @RoleAdmin
    @RoleFeign
    @GetMapping(value = { V1 + root + "/get-data-user"})
    public List<UserDTO> getUserData(@RequestParam Long shopId) {
        List<UserDTO> dtoList = userLoginService.getDataUser(shopId);
        return dtoList;
    }
}
