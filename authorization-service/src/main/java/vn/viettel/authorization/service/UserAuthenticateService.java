package vn.viettel.authorization.service;


import vn.viettel.authorization.service.dto.*;
import vn.viettel.core.db.entity.User;
import vn.viettel.core.messaging.Response;

import java.math.BigInteger;
import java.util.List;

public interface UserAuthenticateService {
    Response<LoginResponse> login(LoginRequest loginInfo);
    Response<String> changePassword(ChangePasswordRequest request);
    List<String> getUserRoles(int userId);
    List<Integer> getUserRoleId(int userId);
    List<BigInteger> getFuncId(List<Integer> roleId);
    List<BigInteger> getActionIdsAllow(List<Integer> roleIds, int funcId);
    void setAction(FunctionResponse func, List<BigInteger> funcIds);
    List<FunctionResponse> getUserPermissions(List<Integer> roleId);
    User getUserById(long id);

}
