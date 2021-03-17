package vn.viettel.customer.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.service.dto.BaseDTO;
import vn.viettel.core.validation.annotation.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class CustomerCreateRequest extends BaseDTO {
    private String cusCode;
    @NotNull(responseMessage = ResponseMessage.CUSTOMER_FIRST_NAME_MUST_BE_NOT_BLANK)
    private String firstName;
    @NotNull(responseMessage = ResponseMessage.CUSTOMER_LAST_NAME_MUST_BE_NOT_BLANK)
    private String lastName;
    @NotNull(responseMessage = ResponseMessage.CUSTOMER_INFORMATION_PHONE_MUST_BE_NOT_NULL)
    private String phoneNumber;
    private String taxCode;
    private String DOB;
    private String email;
    @NotNull(responseMessage = ResponseMessage.CUSTOMER_INFORMATION_GENDER_MUST_BE_NOT_NULL)
    private int gender;
    @NotNull(responseMessage = ResponseMessage.CUSTOMER_STATUS_MUST_BE_NOT_NULL)
    private int status;
    private int cusType;
    private boolean exclusive;
    private String description;
    private String addressDetail;
    private AddressDto address;
    private Long shopId;
    private Long groupId;
    private IDCardDto idCard;
    private CardMemberDto cardMember;
    private CompanyDto company;
}
