package vn.viettel.authorization.service;

import vn.viettel.core.messaging.Response;
import vn.viettel.authorization.service.dto.PasswordResetDTO;
import vn.viettel.core.db.entity.PasswordReset;
import vn.viettel.core.service.BaseService;

import java.util.List;

public interface PasswordResetService extends BaseService {

    /* GET RESET PASSWORD BY USER ID */
    public Response<PasswordResetDTO> getByUserId(Long userId);

    /* GET RESET ADMIN PASSWORD BY USER ID */
    public Response<PasswordResetDTO> getByAdminUserId(Long userId);

    /* GET RESET ADMIN PASSWORD BY USER ID */
    public Response<PasswordResetDTO> getByMemberIdAndCompanyId(Long userId, Long companyId);

    /* GET RESET PASSWORD BY TOKEN */
    public Response<PasswordResetDTO> getByToken(String token);

    public Response<PasswordResetDTO> getByManagementUserId(Long userId);

    public Response<PasswordResetDTO> getByTokenAndCompanyId(String token, Long companyId);

    public List<PasswordReset> getByMember(Long memberId);
}
