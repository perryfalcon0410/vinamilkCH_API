package vn.viettel.authorization.messaging.user;

import vn.viettel.authorization.service.validation.annotation.UserExist;
import vn.viettel.core.messaging.BaseRequest;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.validation.annotation.NotNull;

public class UserImageDeleteRequest extends BaseRequest {

    @NotNull(responseMessage = ResponseMessage.USER_USERID_MUST_BE_NOT_NULL)
    @UserExist
    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}
