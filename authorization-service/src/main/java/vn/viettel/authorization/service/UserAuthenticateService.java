package vn.viettel.authorization.service;


import vn.viettel.authorization.service.dto.*;
import vn.viettel.core.db.entity.authorization.User;
import vn.viettel.core.messaging.Response;

import java.math.BigDecimal;
import java.util.List;

public interface UserAuthenticateService {
    Response<LoginResponse> preLogin(LoginRequest loginInfo);
    Response<LoginResponse> login(LoginRequest loginInfo, long roleId);
    Response<String> changePassword(ChangePasswordRequest request);
    List<RoleDTO> getUserRoles(int userId);
    String getUserUsedRole(int userId);
    List<Integer> getUserRoleId(int userId);
    List<BigDecimal> getFuncId(List<Integer> roleId);
    List<BigDecimal> getActionIdsAllow(List<Integer> roleIds, int funcId);
    void setAction(FunctionResponse func, List<BigDecimal> funcIds);
    List<FunctionResponse> getUserPermissions(List<Integer> roleId);
    User getUserById(long id);

}
