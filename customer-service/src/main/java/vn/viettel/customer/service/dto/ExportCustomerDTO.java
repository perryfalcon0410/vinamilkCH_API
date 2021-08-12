package vn.viettel.customer.service.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;
import vn.viettel.core.util.Constants;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ExportCustomerDTO extends BaseDTO {

    @ApiModelProperty(notes = "Mã khách hàng")
    private String customerCode;
    @ApiModelProperty(notes = "Tên khách hàng")
    private String firstName;
    @ApiModelProperty(notes = "Họ khách hàng")
    private String lastName;
    @ApiModelProperty(notes = "Id giới tính")
    private Long genderId;
    @ApiModelProperty(notes = "Mã vạch")
    private String barCode;
    @ApiModelProperty(notes = "Ngày sinh")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime dob;
    @ApiModelProperty(notes = "nhóm khách hàng")
    private String customerTypeName;
    @ApiModelProperty(notes = "Trạng thái: 1-Hoạt động, 0-Ngưng hoạt động")
    private Integer status;
    @ApiModelProperty(notes = "1-Khách hàng mặc định của cửa hàng")
    private Boolean isPrivate;
    @ApiModelProperty(notes = "Số CMND")
    private String idNo;
    @ApiModelProperty(notes = "Ngày cấp CMND")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime idNoIssuedDate;
    @ApiModelProperty(notes = "Nơi cấp CMND")
    private String idNoIssuedPlace;
    @ApiModelProperty(notes = "Số điện thoại di động, -đang xài")
    private String mobiPhone;
    @ApiModelProperty(notes = "Địa chỉ email")
    private String email;
    @ApiModelProperty(notes = "Địa chỉ")
    private String address;
    @ApiModelProperty(notes = "Tên công ty, tổ chức đang làm việc")
    private String workingOffice;
    @ApiModelProperty(notes = "Địa chỉ nơi làm việc")
    private String officeAddress;
    @ApiModelProperty(notes = "Mã số thuế")
    private String taxCode;
    @ApiModelProperty(notes = "1-Khách hàng mặc định của cửa hàng")
    private Boolean isDefault;
    @ApiModelProperty(notes = "Tên thẻ thành viên")
    private String memberCardName;
    @ApiModelProperty(notes = "Loại khách hàng")
    private String apParamName;
    @ApiModelProperty(notes = "Ghi chú")
    private String noted;
}
