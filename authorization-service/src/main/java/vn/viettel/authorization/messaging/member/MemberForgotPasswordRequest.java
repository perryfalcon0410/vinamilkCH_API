package vn.viettel.authorization.messaging.member;

import vn.viettel.core.messaging.BaseRequest;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.validation.annotation.NotNull;

public class MemberForgotPasswordRequest extends BaseRequest {

    @NotNull(responseMessage = ResponseMessage.USER_EMAIL_MUST_BE_NOT_NULL)
    private String email;
    @NotNull(responseMessage = ResponseMessage.COMPANY_ID_MUST_BE_NOT_NULL)
    private String companySlug;
    @NotNull(responseMessage = ResponseMessage.SALON_ID_MUST_BE_NOT_NULL)
    private String salonSlug;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
