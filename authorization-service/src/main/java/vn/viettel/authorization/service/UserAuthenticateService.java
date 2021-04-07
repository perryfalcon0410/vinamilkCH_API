package vn.viettel.authorization.service;

import vn.viettel.authorization.service.dto.*;
import vn.viettel.core.db.entity.authorization.User;
import vn.viettel.core.messaging.Response;

import java.util.List;

public interface UserAuthenticateService {
    Response<Object> preLogin(LoginRequest loginInfo, String captchaCode);
    Response<Object> login(LoginRequest loginInfo);
    Response<String> changePassword(ChangePasswordRequest request);
    User getUserById(long id);
    List<ShopDTO> getShopByRole(Long roleId);
    List<PermissionDTO> getUserPermission(Long roleId);
}
