package vn.viettel.authorization.messaging.user;

import vn.viettel.core.messaging.BaseRequest;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.validation.annotation.EmailAddress;
import vn.viettel.core.validation.annotation.MinTextLength;
import vn.viettel.core.validation.annotation.NotBlank;
import vn.viettel.core.validation.annotation.NotNull;

import java.util.List;

public class ManagementUserCreateRequest extends BaseRequest {
    @NotNull(responseMessage = ResponseMessage.USER_NAME_MUST_BE_NOT_NULL)
    @NotBlank(responseMessage = ResponseMessage.USER_NAME_MUST_BE_NOT_BLANK)
    private String name;

    @NotNull(responseMessage = ResponseMessage.USER_EMAIL_MUST_BE_NOT_NULL)
    @NotBlank(responseMessage = ResponseMessage.USER_EMAIL_MUST_BE_NOT_BLANK)
    @EmailAddress(responseMessage = ResponseMessage.USER_EMAIL_FORMAT_NOT_CORRECT)
    private String email;

    @NotNull(responseMessage = ResponseMessage.USER_PASSWORD_MUST_BE_NOT_NULL)
    @MinTextLength(length = 6, responseMessage = ResponseMessage.USER_PASSWORD_MUST_BE_GREATER_THAN_SIX_CHARACTER)
    private String encryptedPassword;

    @NotNull(responseMessage = ResponseMessage.USER_ROLE_MUST_BE_NOT_BLANK)
    private List<Long> managementPrivilageIds;

    @NotNull(responseMessage = ResponseMessage.COMPANY_NAME_MUST_BE_NOT_BLANK)
    private Long companyId;

    @NotNull(responseMessage = ResponseMessage.USER_STATUS_MUST_BE_NOT_BLANK)
    private Boolean status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String password) {
        this.encryptedPassword = password;
    }

    public List<Long> getManagementPrivilageIds() {
        return managementPrivilageIds;
    }

    public void setManagementPrivilageIds(List<Long> managementPrivilageIds) {
        this.managementPrivilageIds = managementPrivilageIds;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
