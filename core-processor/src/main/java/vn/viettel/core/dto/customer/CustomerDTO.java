package vn.viettel.core.dto.customer;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.dto.common.AreaDetailDTO;
import vn.viettel.core.service.dto.BaseDTO;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Api(value = "Thông tin khách hàng trả về")
public class CustomerDTO extends BaseDTO {

    @ApiModelProperty(notes = "Tên khách hàng")
    private String firstName;
    @ApiModelProperty(notes = "Họ và tên không dấu")
    private String nameText;
    @ApiModelProperty(notes = "Id cửa hàng")
    private Long shopId;
    @ApiModelProperty(notes = "Mã khách hàng")
    private String customerCode;
    @ApiModelProperty(notes = "Họ khách hàng")
    private String lastName;
    @ApiModelProperty(notes = "Id giới tính")
    private Integer genderId;
    @ApiModelProperty(notes = "Mã vạch")
    private String barCode;
    @ApiModelProperty(notes = "Ngày sinh")
    private Date dob;
    @ApiModelProperty(notes = "Id nhóm khách hàng")
    private Long customerTypeId;
    @ApiModelProperty(notes = "Trạng thái: 1-Hoạt động, 0-Ngưng hoạt động")
    private Long status;
    @ApiModelProperty(notes = "1-Khách hàng riêng của cửa hàng")
    private Boolean isPrivate;
    @ApiModelProperty(notes = "Số CMND")
    private String idNo;
    @ApiModelProperty(notes = "Ngày cấp CMND")
    private Date idNoIssuedDate;
    @ApiModelProperty(notes = "Nơi cấp CMND")
    private String idNoIssuedPlace;
    @ApiModelProperty(notes = "Số điện thoại")
    private String phone;
    @ApiModelProperty(notes = "Số điện thoại di động, -đang xài")
    private String mobiPhone;
    @ApiModelProperty(notes = "Địa chỉ email")
    private String email;
    @ApiModelProperty(notes = "Id Địa bàn")
    private Long areaId;
    @ApiModelProperty(notes = "Số nhà, đường")
    private String street;
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
    @ApiModelProperty(notes = "Id loại thẻ")
    private Long cardTypeId;
    @ApiModelProperty(notes = "Id loại khách hàng")
    private Long closelyTypeId;
    @ApiModelProperty(notes = "Ghi chú")
    private String noted;
    @ApiModelProperty(notes = "Tổng tiền tích lũy của khách hàng")
    private Double totalBill;
    @ApiModelProperty(notes = "Số đơn hàng đã mua trong ngày")
    private Integer dayOrderNumber;
    @ApiModelProperty(notes = "Doanh số đã mua trong ngày")
    private Integer dayOrderAmount;
    @ApiModelProperty(notes = "Số đơn hàng đã mua trong tháng")
    private Integer monthOrderNumber;
    @ApiModelProperty(notes = "Doanh số đã mua trong tháng")
    private Integer monthOrderAmount;
    @ApiModelProperty(notes = "Chi tiết id tỉnh/tp, quận/huyện, phường/xã")
    private AreaDetailDTO areaDetailDTO;
    @ApiModelProperty(notes = "Điểm tích lũy")
    private Integer scoreCumulated;
    @ApiModelProperty(notes = "Thành tiền tích lũy")
    private Double amountCumulated;

}
