package vn.viettel.authorization.messaging.member;

import vn.viettel.core.messaging.BaseRequest;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.validation.annotation.EmailAddress;
import vn.viettel.core.validation.annotation.NotNull;

public class MemberSendEmailRegistrationRequest extends BaseRequest {
    @NotNull(responseMessage = ResponseMessage.USER_EMAIL_MUST_BE_NOT_NULL)
    @EmailAddress(responseMessage = ResponseMessage.USER_EMAIL_FORMAT_NOT_CORRECT)
    private String email;

    @NotNull(responseMessage =  ResponseMessage.SALON_DOES_NOT_EXIST)
    private Long salonId;

    @NotNull(responseMessage =  ResponseMessage.COMPANY_ID_MUST_BE_NOT_NULL)
    private Long companyId;

    @NotNull(responseMessage =  ResponseMessage.COMPANY_ID_MUST_BE_NOT_NULL)
    private String companySlug;

    @NotNull(responseMessage =  ResponseMessage.SALON_DOES_NOT_EXIST)
    private String salonSlug;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getSalonId() {
        return salonId;
    }

    public void setSalonId(Long salonId) {
        this.salonId = salonId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
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
