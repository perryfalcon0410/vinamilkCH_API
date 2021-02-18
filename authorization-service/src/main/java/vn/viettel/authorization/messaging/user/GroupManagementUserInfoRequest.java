package vn.viettel.authorization.messaging.user;

import vn.viettel.core.ResponseMessage;
import vn.viettel.core.validation.annotation.NotNull;

public class GroupManagementUserInfoRequest {

    @NotNull(responseMessage = ResponseMessage.GROUP_ID_MUST_BE_NOT_NULL)
    private Long groupId;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

}
