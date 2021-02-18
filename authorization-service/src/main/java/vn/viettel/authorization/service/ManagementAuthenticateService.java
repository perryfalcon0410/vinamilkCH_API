package vn.viettel.authorization.service;

import vn.viettel.authorization.service.dto.user.ManagementUserDTO;
import vn.viettel.authorization.service.dto.user.ManagementUserEditDTO;
import vn.viettel.authorization.service.dto.user.ManagementUserIndexDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.authorization.messaging.authorization.UserForgotPasswordRequest;
import vn.viettel.authorization.messaging.authorization.UserLoginRequest;
import vn.viettel.authorization.messaging.authorization.UserRegisterRequest;
import vn.viettel.authorization.messaging.authorization.UserUpdateForgotPasswordRequest;
import vn.viettel.authorization.messaging.user.ManagementUserCreateRequest;
import vn.viettel.authorization.service.dto.authorization.UserLoginDTO;
import vn.viettel.authorization.service.dto.user.*;
import vn.viettel.core.service.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ManagementAuthenticateService extends BaseService {

    /* REGISTER */
    Response<String> register(UserRegisterRequest request);

    /* LOGIN */
    Response<UserLoginDTO> login(UserLoginRequest request);

    /* FORGOT PASSWORD */
    Response<String> forgotPassword(UserForgotPasswordRequest request);

    /* CHECK VALID RESET PASSWORD TOKEN */
    Response<String> checkValidResetPasswordToken(String token);

    /* UPDATE FORGOT PASSWORD */
    Response<String> updateForgotPassword(UserUpdateForgotPasswordRequest request);

    UserLoginDTO managementUserLogin(String email, String password);

    Response<Page<ManagementUserIndexDTO>> usersManagementIndex(String searchKeywords, Long idCompany, Pageable pageable);

    Response<ManagementUserDTO> create(ManagementUserCreateRequest request);
    Response<ManagementUserDTO> update(Long id, ManagementUserCreateRequest request);
    Response<ManagementUserDTO> delete(Long id);
    Response<ManagementUserEditDTO> edit(Long id);
    Response<ManagementUserEditDTO> getUserAdminByCompanyId(Long id);
}
