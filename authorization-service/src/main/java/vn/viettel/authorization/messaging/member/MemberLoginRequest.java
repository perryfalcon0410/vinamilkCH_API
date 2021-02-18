package vn.viettel.authorization.messaging.member;

import vn.viettel.core.messaging.BaseRequest;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.validation.annotation.NotNull;

public class MemberLoginRequest extends BaseRequest {

    @NotNull(responseMessage = ResponseMessage.USER_EMAIL_MUST_BE_NOT_NULL)
    private String email;
    @NotNull(responseMessage = ResponseMessage.USER_PASSWORD_MUST_BE_NOT_NULL)
    private String password;
    @NotNull(responseMessage = ResponseMessage.COMPANY_ID_MUST_BE_NOT_NULL)
    private String companySlug;

    public MemberLoginRequest() {
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

    public String getCompanySlug() {
        return companySlug;
    }

    public void setCompanySlug(String companySlug) {
        this.companySlug = companySlug;
    }
}
