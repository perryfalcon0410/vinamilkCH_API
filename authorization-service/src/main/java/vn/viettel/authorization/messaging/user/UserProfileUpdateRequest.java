package vn.viettel.authorization.messaging.user;

import vn.viettel.core.messaging.BaseRequest;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.validation.annotation.NotNull;

public class UserProfileUpdateRequest extends BaseRequest {

    @NotNull(responseMessage = ResponseMessage.USER_NAME_MUST_BE_NOT_NULL)
    private String name;

    private String photo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

}
