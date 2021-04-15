package vn.viettel.customer.messaging;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.messaging.BaseRequest;
import vn.viettel.core.validation.annotation.NotBlank;
import vn.viettel.core.validation.annotation.NotNull;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class CustomerRequest extends BaseRequest {
    private Long id;
    @NotBlank(responseMessage = ResponseMessage.CUSTOMER_FIRST_NAME_MUST_BE_NOT_BLANK)
    private String firstName;
    @NotBlank(responseMessage = ResponseMessage.CUSTOMER_LAST_NAME_MUST_BE_NOT_BLANK)
    private String lastName;
    @NotNull(responseMessage = ResponseMessage.CUSTOMER_INFORMATION_GENDER_MUST_BE_NOT_NULL)
    private Integer genderId;

    private String customerCode;
    private String barCode;
    private Date dob;
    private Long customerTypeId;
    private Long status;
    private Boolean isPrivate;
    private String idNo;
    private Date idNoIssuedDate;
    private String idNoIssuedPlace;
    @NotNull(responseMessage = ResponseMessage.CUSTOMER_INFORMATION_PHONE_MUST_BE_NOT_NULL)
    private String phone;
    private String mobiPhone;
    private String email;
    private Long areaId;
    private String street;
    private String address;
    private String workingOffice;
    private String officeAddress;
    private String taxCode;
    private Boolean isDefault;
    private Long closelyTypeId;
    private Long cardTypeId;
    private String noted;


}
