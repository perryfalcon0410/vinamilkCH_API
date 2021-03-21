package vn.viettel.customer.messaging;

import lombok.Getter;
import lombok.Setter;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.messaging.BaseRequest;
import vn.viettel.core.validation.annotation.NotNull;

import java.util.Date;

@Getter
@Setter
public class CustomerUpdateRequest extends BaseRequest {
    @NotNull(responseMessage = ResponseMessage.ID_MUST_NOT_BE_NULL)
    private Long id;
    @NotNull(responseMessage = ResponseMessage.CUSTOMER_CODE_MUST_BE_NOT_BLANK)
    private String customerCode;
    private String barCode;
    @NotNull(responseMessage = ResponseMessage.CUSTOMER_FIRST_NAME_MUST_BE_NOT_BLANK)
    private String firstName;
    @NotNull(responseMessage = ResponseMessage.CUSTOMER_LAST_NAME_MUST_BE_NOT_BLANK)
    private String lastName;
    @NotNull(responseMessage = ResponseMessage.CUSTOMER_INFORMATION_PHONE_MUST_BE_NOT_NULL)
    private String phoneNumber;
    @NotNull(responseMessage = ResponseMessage.CUSTOMER_INFORMATION_GENDER_MUST_BE_NOT_NULL)
    private Long gender;
    @NotNull(responseMessage = ResponseMessage.CUSTOMER_STATUS_MUST_BE_NOT_NULL)
    private Long status;

    private Date birthday;
    private Long customerGroupId;
    private Long specialCustomer;
    private String noted;
    private String email;
    private Long countryId;
    private Long areaId;
    private Long provinceId;
    private Long districtId;
    private Long wardId;
    private String address;
    private Long shopId;
    private IdentityCardUpdateRequest identityCard;
    private String companyName;
    private String companyAddress;
    private String taxCode;
    private Long cardMemberId;
}
