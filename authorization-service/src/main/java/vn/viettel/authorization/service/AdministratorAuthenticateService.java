package vn.viettel.authorization.service;

import vn.viettel.core.messaging.Response;
import vn.viettel.authorization.messaging.authorization.UserForgotPasswordRequest;
import vn.viettel.authorization.messaging.authorization.UserLoginRequest;
import vn.viettel.authorization.messaging.authorization.UserUpdateForgotPasswordRequest;
import vn.viettel.authorization.messaging.user.AdminUserCreateRequest;
import vn.viettel.authorization.service.dto.authorization.AdminUserLoginDTO;
import vn.viettel.authorization.service.dto.user.AdminUserDTO;
import vn.viettel.authorization.service.dto.user.AdminUserEditDTO;
import vn.viettel.authorization.service.dto.user.UserAdminIndexDTO;
import vn.viettel.core.dto.user.AdminUserResponseDTO;
import vn.viettel.core.service.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdministratorAuthenticateService extends BaseService {
    /* LOGIN */
    Response<AdminUserLoginDTO> login(UserLoginRequest request);

    /* FORGOT PASSWORD */
    Response<String> forgotPassword(UserForgotPasswordRequest request);

    /* CHECK VALID RESET PASSWORD TOKEN */
    Response<String> checkValidResetPasswordToken(String token);

    /* UPDATE FORGOT PASSWORD */
    Response<String> updateForgotPassword(UserUpdateForgotPasswordRequest request);

    AdminUserLoginDTO administratorLogin(String email, String password);

    Response<Page<UserAdminIndexDTO>> usersAdminIndex(String searchKeywords, Pageable pageable);

    Response<AdminUserDTO> create(AdminUserCreateRequest request);

    Response<AdminUserDTO> update(Long id, AdminUserCreateRequest request);

    Response<AdminUserDTO> delete(Long id);

    Response<AdminUserEditDTO> edit(Long id);

    /**
     * Get service user admin by id (available)
     *
     * @param id id of admin
     * @return AdminUserResponseDTO
     */
    Response<AdminUserResponseDTO> getAdminById(Long id);
}
