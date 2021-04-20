package vn.viettel.authorization.service;

import vn.viettel.authorization.service.dto.ChangePasswordRequest;
import vn.viettel.authorization.service.dto.LoginRequest;
import vn.viettel.authorization.service.dto.PermissionDTO;
import vn.viettel.authorization.service.dto.ShopDTO;
import vn.viettel.core.dto.UserDTO;
import vn.viettel.core.messaging.Response;

import java.util.List;

public interface UserAuthenticateService {
    Response<Object> preLogin(LoginRequest loginInfo, String captchaCode);
    Response<Object> login(LoginRequest loginInfo);
    Response<Object> changePassword(ChangePasswordRequest request);
    UserDTO getUserById(long id);
    List<ShopDTO> getShopByRole(Long roleId);
    List<PermissionDTO> getUserPermission(Long roleId);
}
