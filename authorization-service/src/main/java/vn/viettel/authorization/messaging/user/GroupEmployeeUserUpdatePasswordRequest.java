package vn.viettel.authorization.messaging.user;

import vn.viettel.core.ResponseMessage;
import vn.viettel.core.validation.annotation.MinTextLength;
import vn.viettel.core.validation.annotation.NotNull;

public class GroupEmployeeUserUpdatePasswordRequest {

    @NotNull(responseMessage = ResponseMessage.GROUP_ID_MUST_BE_NOT_NULL)
    private Long groupId;

    @NotNull(responseMessage = ResponseMessage.USER_PASSWORD_MUST_BE_NOT_NULL)
    @MinTextLength(length = 6, responseMessage = ResponseMessage.USER_PASSWORD_MUST_BE_GREATER_THAN_SIX_CHARACTER)
    private String password;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
