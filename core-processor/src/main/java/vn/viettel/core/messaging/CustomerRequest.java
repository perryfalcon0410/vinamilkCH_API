package vn.viettel.core.messaging;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.validation.annotation.NotBlank;
import vn.viettel.core.validation.annotation.NotNull;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Api(value = "Thông tin tạo khách hàng")
public class CustomerRequest extends BaseRequest {
    @ApiModelProperty(value = "Họ")
    @NotBlank(responseMessage = ResponseMessage.CUSTOMER_FIRST_NAME_MUST_BE_NOT_BLANK)
    private String firstName;
    @ApiModelProperty(value = "Tên")
    @NotBlank(responseMessage = ResponseMessage.CUSTOMER_LAST_NAME_MUST_BE_NOT_BLANK)
    private String lastName;
    private Integer genderId;
    private String customerCode;
    private String barCode;
    @NotNull(responseMessage = ResponseMessage.DATE_OF_BIRTH_MUST_BE_NOT_NULL)
    private Date dob;
    private Long customerTypeId;
    private Long status;
    private Boolean isPrivate;
    private String idNo;
    private Date idNoIssuedDate;
    private String idNoIssuedPlace;
    private String phone;
    @NotNull(responseMessage = ResponseMessage.CUSTOMER_INFORMATION_PHONE_MUST_BE_NOT_NULL)
    private String mobiPhone;
    private String email;
    private Long areaId;
    private String street;
    private String address;
    private String workingOffice;
    private String officeAddress;
    private String taxCode;
    private Long closelyTypeId;
    private Long cardTypeId;
    private String noted;
}
