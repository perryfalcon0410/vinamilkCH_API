package vn.viettel.authorization.messaging.authorization;

import vn.viettel.core.db.entity.status.Object;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.validation.annotation.NotNull;

public class UserLoginForViewPageRequest {

    @NotNull(responseMessage = ResponseMessage.OBJECT_MUST_BE_NOT_NULL)
    private Object object;
    @NotNull(responseMessage = ResponseMessage.OBJECT_ID_MUST_BE_NOT_NULL)
    private Long objectId;
    @NotNull(responseMessage = ResponseMessage.USER_EMAIL_MUST_BE_NOT_NULL)
    private String email;
    @NotNull(responseMessage = ResponseMessage.USER_PASSWORD_MUST_BE_NOT_NULL)
    private String password;

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
