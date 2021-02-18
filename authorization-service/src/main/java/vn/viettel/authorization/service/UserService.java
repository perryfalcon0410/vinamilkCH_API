package vn.viettel.authorization.service;

import vn.viettel.core.messaging.Response;
import vn.viettel.authorization.messaging.user.UserBulkDeleteRequest;
import vn.viettel.authorization.messaging.user.UserCreateRequest;
import vn.viettel.authorization.messaging.user.UserExistResponse;
import vn.viettel.authorization.messaging.user.UserProfileUpdateRequest;
import vn.viettel.authorization.service.dto.user.UserDTO;
import vn.viettel.authorization.service.dto.user.UserEditDTO;
import vn.viettel.authorization.service.dto.user.UserIndexDTO;
import vn.viettel.authorization.service.dto.user.UserProfileDTO;
import vn.viettel.core.db.entity.User;
import vn.viettel.core.service.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService extends BaseService {

    /* GET BY ID */
    UserDTO getById(Long id);

    boolean checkExistsUserByEmail(String email);

    Response<UserDTO> findUserByEmail(String email);

    Response<List<UserDTO>> findUsersByEmail(String email);

    UserExistResponse checkUserExist(Long id);

    /* GET USER BY ID */
    public Response<UserDTO> getUserById(Long id);

    User getCustomerUserForgotPasswordByEmail(String email);

    /* USER UPDATE PROFILE */
    Response<UserProfileDTO> updateProfile(UserProfileUpdateRequest request);

    /* GET BY EMAIL AND ROLE ID */
    UserDTO getByEmailAndRoleId(String email, Long roleId);

    /* GET BY ID AND ROLE ID */
    UserDTO getByIdAndRoleId(Long id, Long roleId);

    List<Long> findByRole(Long rid);

    Response<List<String>> getListEmailById(long[] ids);

    Response<Page<UserIndexDTO>> usersIndex(Long roleId, String searchKeywords, Pageable pageable);

    Response<UserEditDTO> edit(Long id);

    Response<UserDTO> create(UserCreateRequest request);

    Response<UserDTO> update(Long id, UserCreateRequest request);

    Response<UserDTO> delete(Long id);

    Response<List<Response<UserDTO>>> bulkDelete(UserBulkDeleteRequest request);

    Response<List<Long>> getAllUserIdByUserName(String name);
}
