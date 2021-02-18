package vn.viettel.authorization.messaging.user;

import vn.viettel.authorization.service.validation.annotation.UserExist;
import vn.viettel.core.messaging.BaseRequest;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.validation.annotation.NotNull;

public class UserImageUploadRequest extends BaseRequest {

    @NotNull(responseMessage = ResponseMessage.USER_USERID_MUST_BE_NOT_NULL)
    @UserExist
    private Long userId;

    @NotNull(responseMessage = ResponseMessage.USER_IMAGE_MUST_BE_NOT_NULL)
    private String image;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
