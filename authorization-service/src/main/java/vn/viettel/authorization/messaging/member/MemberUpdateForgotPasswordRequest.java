package vn.viettel.authorization.messaging.member;

import vn.viettel.core.messaging.BaseRequest;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.validation.annotation.NotNull;

public class MemberUpdateForgotPasswordRequest extends BaseRequest {

    @NotNull(responseMessage = ResponseMessage.INVALID_TOKEN)
    private String token;
    @NotNull(responseMessage = ResponseMessage.USER_PASSWORD_MUST_BE_NOT_NULL)
    private String password;
    @NotNull(responseMessage = ResponseMessage.COMPANY_ID_MUST_BE_NOT_NULL)
    private String companySlug;
    @NotNull(responseMessage = ResponseMessage.SALON_ID_MUST_BE_NOT_NULL)
    private String salonSlug;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCompanySlug() {
        return companySlug;
    }

    public void setCompanySlug(String companySlug) {
        this.companySlug = companySlug;
    }

    public String getSalonSlug() {
        return salonSlug;
    }

    public void setSalonSlug(String salonSlug) {
        this.salonSlug = salonSlug;
    }
}
