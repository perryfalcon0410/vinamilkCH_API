package vn.viettel.core.messaging;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import vn.viettel.core.util.Constants;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.validation.annotation.MaxTextLength;
import vn.viettel.core.validation.annotation.NotBlank;
import vn.viettel.core.validation.annotation.NotNull;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Api(value = "Thông tin tạo khách hàng")
public class CustomerRequest extends BaseRequest {

    @ApiModelProperty(notes = "Họ khách hàng")
    @NotBlank(responseMessage = ResponseMessage.CUSTOMER_FIRST_NAME_MUST_BE_NOT_BLANK)
    @MaxTextLength(length = 250, responseMessage = ResponseMessage.MAX_LENGTH_STRING)
    private String firstName;

    @ApiModelProperty(notes = "Tên khách hàng")
    @NotBlank(responseMessage = ResponseMessage.CUSTOMER_LAST_NAME_MUST_BE_NOT_BLANK)
    @MaxTextLength(length = 250, responseMessage = ResponseMessage.MAX_LENGTH_STRING)
    private String lastName;

    @ApiModelProperty(value = "Id giới tính")
    private Integer genderId;

    @ApiModelProperty(value = "Mã khách hàng")
    private String customerCode;

    @ApiModelProperty(notes = "Mã vạch")
    @MaxTextLength(length = 50, responseMessage = ResponseMessage.MAX_LENGTH_STRING)
    private String barCode;

    @ApiModelProperty(notes = "Ngày sinh")
    @NotNull(responseMessage = ResponseMessage.DATE_OF_BIRTH_MUST_BE_NOT_NULL)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime dob;

    @ApiModelProperty(notes = "Id nhóm khách hàng")
    @NotNull(responseMessage = ResponseMessage.CUSTOMER_TYPE_MUST_BE_NOT_NULL)
    private Long customerTypeId = 0L;

    @ApiModelProperty(notes = "Trạng thái: 1-Hoạt động, 0-Ngưng hoạt động")
    @NotNull(responseMessage = ResponseMessage.CUSTOMER_STATUS_MUST_BE_NOT_NULL)
    private Long status;

    @ApiModelProperty(notes = "1-Khách hàng riêng của cửa hàng")
    private Boolean isPrivate;

    @ApiModelProperty(notes = "Số CMND")
    @MaxTextLength(length = 250, responseMessage = ResponseMessage.MAX_LENGTH_STRING)
    private String idNo;

    @ApiModelProperty(notes = "Ngày cấp CMND")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime idNoIssuedDate;

    @ApiModelProperty(notes = "Nơi cấp CMND")
    @MaxTextLength(length = 250,responseMessage = ResponseMessage.MAX_LENGTH_STRING)
    private String idNoIssuedPlace;

    @ApiModelProperty(notes = "Số điện thoại di động")
    @NotNull(responseMessage = ResponseMessage.CUSTOMER_INFORMATION_PHONE_MUST_BE_NOT_NULL)
    private String mobiPhone;

    @MaxTextLength(length = 250,responseMessage = ResponseMessage.MAX_LENGTH_STRING)
    @ApiModelProperty(notes = "Địa chỉ email")
    private String email;

    @ApiModelProperty(notes = "Id Địa bàn")
    @NotNull(responseMessage = ResponseMessage.AREA_NOT_EXISTS)
    private Long areaId;

    @ApiModelProperty(notes = "Số nhà, đường")
    @NotNull(responseMessage = ResponseMessage.STREET_MUST_BE_NOT_NULL)
    @MaxTextLength(length = 250, responseMessage = ResponseMessage.MAX_LENGTH_STRING)
    private String street;

    @ApiModelProperty(notes = "Tên công ty, tổ chức đang làm việc")
    @MaxTextLength(length = 250, responseMessage = ResponseMessage.MAX_LENGTH_STRING)
    private String workingOffice;

    @ApiModelProperty(notes = "Địa chỉ nơi làm việc")
    @MaxTextLength(length = 250, responseMessage = ResponseMessage.MAX_LENGTH_STRING)
    private String officeAddress;

    @ApiModelProperty(notes = "Mã số thuế")
    @MaxTextLength(length = 50, responseMessage = ResponseMessage.MAX_LENGTH_STRING)
    private String taxCode;

    @ApiModelProperty(notes = "Id loại khách hàng")
    private Long closelyTypeId;

    @ApiModelProperty(notes = "Id loại thẻ")
    private Long cardTypeId;

    @ApiModelProperty(notes = "Ghi chú")
    @MaxTextLength(length = 3950, responseMessage = ResponseMessage.MAX_LENGTH_STRING)
    private String noted;
}
