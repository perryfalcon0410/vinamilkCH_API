package vn.viettel.authorization.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.authorization.repository.RoleRepository;
import vn.viettel.authorization.service.UserAuthenticateService;
import vn.viettel.authorization.service.dto.*;
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

    @PostMapping("/preLogin")
    public Response<Object> preLogin(@Valid @RequestBody LoginRequest loginInfo,
                                            @RequestParam(value = "captcha", required = false) String captcha) {
        return userLoginService.preLogin(loginInfo, captcha);
    }

    @PostMapping("/login")
    public Response<Object> userLogin(@Valid @RequestBody LoginRequest loginInfo) {
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

    @GetMapping("get-user-permission/{roleId}")
    public List<PermissionDTO> getUserPermission(@PathVariable Long roleId) {
        return userLoginService.getUserPermission(roleId);
    }
}
