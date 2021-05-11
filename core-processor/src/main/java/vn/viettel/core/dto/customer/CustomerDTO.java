package vn.viettel.core.dto.customer;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.dto.common.AreaDTO;
import vn.viettel.core.service.dto.BaseDTO;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class CustomerDTO extends BaseDTO {

    private String firstName;
    private String nameText;
    private Long shopId;
    private String customerCode;
    private String lastName;
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
    private Long cardTypeId;
    private Long closelyTypeId;
    private String noted;
    private Float totalBill;
    private Integer dayOrderNumber;
    private Integer dayOrderAmount;
    private Integer monthOrderNumber;
    private Integer monthOrderAmount;

    @ApiModelProperty(notes = "Chi tiết id tỉnh/tp, quận/huyện, phường/xã")
    private AreaDTO areaDTO;
    @ApiModelProperty(notes = "Điểm tích lũy")
    private Integer scoreCumulated;
    @ApiModelProperty(notes = "Thành tiền tích lũy")
    private Float amountCumulated;
    private String createUser;
    private String updateUser;
}
