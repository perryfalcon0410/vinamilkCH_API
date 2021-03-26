package vn.viettel.customer.messaging;

import lombok.Getter;
import lombok.Setter;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.messaging.BaseRequest;
import vn.viettel.core.validation.annotation.NotBlank;
import vn.viettel.core.validation.annotation.NotNull;

import java.util.Date;

@Getter
@Setter
public class CustomerUpdateRequest extends BaseRequest {
    @NotNull(responseMessage = ResponseMessage.ID_MUST_NOT_BE_NULL)
    private Long id;
    @NotBlank(responseMessage = ResponseMessage.CUSTOMER_CODE_MUST_BE_NOT_BLANK)
    private String customerCode;
    @NotBlank(responseMessage = ResponseMessage.CUSTOMER_FIRST_NAME_MUST_BE_NOT_BLANK)
    private String firstName;
    @NotBlank(responseMessage = ResponseMessage.CUSTOMER_LAST_NAME_MUST_BE_NOT_BLANK)
    private String lastName;
    @NotNull(responseMessage = ResponseMessage.CUSTOMER_INFORMATION_GENDER_MUST_BE_NOT_NULL)
    private Integer genderId;

    private String barCode;
    private Date dob;
    private Long customerTypeId;
    private Long status;
    private Boolean isPrivate;
    private String idNo;
    private Date idNoIssuedDate;
    private String idNoIssuedPlace;
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

}
