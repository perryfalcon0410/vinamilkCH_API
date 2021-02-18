package vn.viettel.authorization.messaging.user;

import vn.viettel.core.ResponseMessage;
import vn.viettel.core.validation.annotation.EmailAddress;
import vn.viettel.core.validation.annotation.NotNull;

public class ShopEmployeeUserUpdateRequest {

    @NotNull(responseMessage = ResponseMessage.SHOP_SHOPID_MUST_BE_NOT_NULL)
    private Long shopId;

    @NotNull(responseMessage = ResponseMessage.USER_EMAIL_MUST_BE_NOT_NULL)
    @EmailAddress(responseMessage = ResponseMessage.USER_EMAIL_FORMAT_NOT_CORRECT)
    private String email;

    @NotNull(responseMessage = ResponseMessage.USER_NUMBER_OF_DATE_EXPIRATION_MUST_BE_NOT_NULL)
    private Integer numberOfDateExpiration;

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getNumberOfDateExpiration() {
        return numberOfDateExpiration;
    }

    public void setNumberOfDateExpiration(Integer numberOfDateExpiration) {
        this.numberOfDateExpiration = numberOfDateExpiration;
    }
}
