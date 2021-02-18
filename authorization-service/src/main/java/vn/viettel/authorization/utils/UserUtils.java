package vn.viettel.authorization.utils;

import vn.viettel.authorization.service.dto.PasswordResetDTO;

public class UserUtils {

    public static PasswordResetDTO createPasswordResetDTOForAdmin(String token, long userId) {
        PasswordResetDTO passwordReset = new PasswordResetDTO();
        passwordReset.setToken(token);
        passwordReset.setAdminUserId(userId);
        return passwordReset;
    }

    public static PasswordResetDTO createPasswordResetDTOForManagementUser(String token, long managementUserId) {
        PasswordResetDTO passwordReset = new PasswordResetDTO();
        passwordReset.setToken(token);
        passwordReset.setManagementUserId(managementUserId);
        return passwordReset;
    }

    public static PasswordResetDTO createPasswordResetDTOForMember(String token, long userId, Long companyId) {
        PasswordResetDTO passwordReset = new PasswordResetDTO();
        passwordReset.setToken(token);
        passwordReset.setMemberId(userId);
        passwordReset.setCompanyId(companyId);
        return passwordReset;
    }
}
