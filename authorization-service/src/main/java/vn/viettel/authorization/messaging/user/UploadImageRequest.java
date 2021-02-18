package vn.viettel.authorization.messaging.user;

import vn.viettel.core.ResponseMessage;
import vn.viettel.core.validation.annotation.NotNull;

public class UploadImageRequest {

    @NotNull(responseMessage = ResponseMessage.SHOP_IMAGE_MUST_BE_NOT_NULL)
    private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
