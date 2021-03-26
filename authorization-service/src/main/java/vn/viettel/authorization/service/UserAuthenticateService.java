package vn.viettel.authorization.service;

import vn.viettel.authorization.service.dto.ChangePasswordRequest;
import vn.viettel.authorization.service.dto.LoginRequest;
import vn.viettel.authorization.service.dto.LoginResponse;
import vn.viettel.authorization.service.dto.ShopDTO;
import vn.viettel.core.db.entity.authorization.User;
import vn.viettel.core.messaging.Response;

import java.util.List;

public interface UserAuthenticateService {
    Response<LoginResponse> preLogin(LoginRequest loginInfo);
    Response<LoginResponse> login(LoginRequest loginInfo, long roleId, long shopId);
    Response<String> changePassword(ChangePasswordRequest request);
    User getUserById(long id);
    List<ShopDTO> getShopByRole(Long roleId);
}
