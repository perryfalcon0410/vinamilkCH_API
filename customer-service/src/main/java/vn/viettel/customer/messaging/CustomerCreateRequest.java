package vn.viettel.customer.messaging;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.service.dto.BaseDTO;
import vn.viettel.core.validation.annotation.NotNull;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class CustomerCreateRequest extends BaseDTO {
    @NotNull(responseMessage = ResponseMessage.CUSTOMER_CODE_MUST_BE_NOT_BLANK)
    private String cusCode;
    private String barCode;
    @NotNull(responseMessage = ResponseMessage.CUSTOMER_FIRST_NAME_MUST_BE_NOT_BLANK)
    private String firstName;
    @NotNull(responseMessage = ResponseMessage.CUSTOMER_LAST_NAME_MUST_BE_NOT_BLANK)
    private String lastName;
    @NotNull(responseMessage = ResponseMessage.CUSTOMER_INFORMATION_PHONE_MUST_BE_NOT_NULL)
    private String phoneNumber;
    private String taxCode;
    private Date DOB;
    private String email;
    @NotNull(responseMessage = ResponseMessage.CUSTOMER_INFORMATION_GENDER_MUST_BE_NOT_NULL)
    private Long gender;
    @NotNull(responseMessage = ResponseMessage.CUSTOMER_STATUS_MUST_BE_NOT_NULL)
    private Boolean status;
    private Long cusType;
    private Boolean exclusive;
    private String description;
    private Long addressId;
    private Long shopId;
    private Long groupId;
    private Long idCardId;
    private Long cardMemberId;
    private Long companyId;
    private Long createdBy;
    private Long updatedBy;
    private Long deletedBy;
}
