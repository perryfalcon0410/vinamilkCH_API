package vn.viettel.authorization.messaging.user;

import vn.viettel.core.ResponseMessage;
import vn.viettel.core.validation.annotation.MinTextLength;
import vn.viettel.core.validation.annotation.NotNull;

public class ShopEmployeeUserUpdatePasswordRequest {

    @NotNull(responseMessage = ResponseMessage.SHOP_SHOPID_MUST_BE_NOT_NULL)
    private Long shopId;

    @NotNull(responseMessage = ResponseMessage.USER_PASSWORD_MUST_BE_NOT_NULL)
    @MinTextLength(length = 6, responseMessage = ResponseMessage.USER_PASSWORD_MUST_BE_GREATER_THAN_SIX_CHARACTER)
    private String password;

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
