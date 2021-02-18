package vn.viettel.authorization.messaging.user;

import vn.viettel.core.messaging.BaseRequest;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.validation.annotation.EmailAddress;
import vn.viettel.core.validation.annotation.MinTextLength;
import vn.viettel.core.validation.annotation.NotBlank;
import vn.viettel.core.validation.annotation.NotNull;

import java.math.BigDecimal;

public class UserCreateRequest extends BaseRequest {
    @NotNull(responseMessage = ResponseMessage.USER_NAME_MUST_BE_NOT_NULL)
    @NotBlank(responseMessage = ResponseMessage.USER_NAME_MUST_BE_NOT_BLANK)
    private String name;

    @NotNull(responseMessage = ResponseMessage.USER_EMAIL_MUST_BE_NOT_NULL)
    @NotBlank(responseMessage = ResponseMessage.USER_EMAIL_MUST_BE_NOT_BLANK)
    @EmailAddress(responseMessage = ResponseMessage.USER_EMAIL_FORMAT_NOT_CORRECT)
    private String email;

    @NotNull(responseMessage = ResponseMessage.USER_PASSWORD_MUST_BE_NOT_NULL)
    @MinTextLength(length = 6, responseMessage = ResponseMessage.USER_PASSWORD_MUST_BE_GREATER_THAN_SIX_CHARACTER)
    private String password;

    @NotNull(responseMessage = ResponseMessage.USER_ROLE_MUST_BE_NOT_BLANK)
    private Long roleId;

    @NotNull(responseMessage = ResponseMessage.USER_STATUS_MUST_BE_NOT_BLANK)
    private Integer status;

    private BigDecimal currentPlanIncentiveRate;

    private BigDecimal nextPlanIncentiveRate;

    private BigDecimal currentPlatformIncentiveRate;

    private BigDecimal nextPlatformIncentiveRate;

    private String phoneNumber;

    private String address;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public BigDecimal getCurrentPlanIncentiveRate() {
        return currentPlanIncentiveRate;
    }

    public void setCurrentPlanIncentiveRate(BigDecimal currentPlanIncentiveRate) {
        this.currentPlanIncentiveRate = currentPlanIncentiveRate;
    }

    public BigDecimal getNextPlanIncentiveRate() {
        return nextPlanIncentiveRate;
    }

    public void setNextPlanIncentiveRate(BigDecimal nextPlanIncentiveRate) {
        this.nextPlanIncentiveRate = nextPlanIncentiveRate;
    }

    public BigDecimal getCurrentPlatformIncentiveRate() {
        return currentPlatformIncentiveRate;
    }

    public void setCurrentPlatformIncentiveRate(BigDecimal currentPlatformIncentiveRate) {
        this.currentPlatformIncentiveRate = currentPlatformIncentiveRate;
    }

    public BigDecimal getNextPlatformIncentiveRate() {
        return nextPlatformIncentiveRate;
    }

    public void setNextPlatformIncentiveRate(BigDecimal nextPlatformIncentiveRate) {
        this.nextPlatformIncentiveRate = nextPlatformIncentiveRate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
