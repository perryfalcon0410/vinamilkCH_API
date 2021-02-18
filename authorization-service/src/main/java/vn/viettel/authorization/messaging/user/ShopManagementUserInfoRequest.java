package vn.viettel.authorization.messaging.user;

import vn.viettel.core.ResponseMessage;
import vn.viettel.core.validation.annotation.NotNull;

public class ShopManagementUserInfoRequest {

    @NotNull(responseMessage = ResponseMessage.SHOP_SHOPID_MUST_BE_NOT_NULL)
    private Long shopId;

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }
}
