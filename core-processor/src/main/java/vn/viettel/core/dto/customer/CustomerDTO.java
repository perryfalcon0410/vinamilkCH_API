package vn.viettel.core.dto.customer;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.dto.common.AreaDetailDTO;
import vn.viettel.core.service.dto.BaseDTO;
import vn.viettel.core.util.Constants;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Api(value = "Thông tin khách hàng trả về")
public class CustomerDTO extends BaseDTO {

    @ApiModelProperty(notes = "Tên khách hàng")
    private String firstName;
    @ApiModelProperty(notes = "Họ khách hàng")
    private String lastName;

    @ApiModelProperty(notes = "Tên đầy đủ")
    private String fullName = "";

    @ApiModelProperty(notes = "Họ và tên không dấu")
    private String nameText;
    @ApiModelProperty(notes = "Id cửa hàng")
    private Long shopId;
    @ApiModelProperty(notes = "Mã khách hàng")
    private String customerCode;

    @ApiModelProperty(notes = "Id giới tính")
    private Integer genderId;

    @ApiModelProperty(notes = "Tên giới tính")
    private String genderName;

    @ApiModelProperty(notes = "Mã vạch")
    private String barCode;
    @ApiModelProperty(notes = "Ngày sinh")

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime dob;
    @ApiModelProperty(notes = "Id nhóm khách hàng")
    private Long customerTypeId;
    @ApiModelProperty(notes = "Trạng thái: 1-Hoạt động, 0-Ngưng hoạt động")
    private Integer status;
    @ApiModelProperty(notes = "1-Khách hàng riêng của cửa hàng")
    private Boolean isPrivate;
    @ApiModelProperty(notes = "Số CMND")
    private String idNo;
    @ApiModelProperty(notes = "Ngày cấp CMND")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime idNoIssuedDate;
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
    private Double dayOrderAmount;
    @ApiModelProperty(notes = "Số đơn hàng đã mua trong tháng")
    private Integer monthOrderNumber;
    @ApiModelProperty(notes = "Doanh số đã mua trong tháng")
    private Double monthOrderAmount;
    @ApiModelProperty(notes = "Chi tiết id tỉnh/tp, quận/huyện, phường/xã")
    private AreaDetailDTO areaDetailDTO;
    @ApiModelProperty(notes = "Thành tiền tích lũy")
    private Double amountCumulated;
    @ApiModelProperty(notes = "Ngày mua hàng cuối cùng")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime lastOrderDate;
    @ApiModelProperty(notes = "Level chỉnh sửa khách hàng")
    private Long isEdit;
    @ApiModelProperty(notes = "Cở chỉnh sửa loại khách hàng")
    private Long isEditCusType;
    @ApiModelProperty(notes = "Danh sách top 5 sản phẩm yêu thích trong vòng 6 tháng")
    private List<String> lstProduct;

    @ApiModelProperty(notes = "Loại khách hàng")
    private CustomerTypeDTO customerType;

    public String getFullName(){
        fullName = "";
        if(firstName != null) fullName = lastName;
        if(lastName != null) fullName = fullName + " " + firstName;

        return fullName == null ? "" : fullName.trim();
    }

    public CustomerDTO(Long id, String firstName, String lastName, String nameText, String customerCode, String mobiPhone, Long customerTypeId
            , String street, String address, Long shopId, String phone, String workingOffice, String officeAddress, String taxCode, Double totalBill, LocalDateTime dob){
        setId(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.nameText = nameText;
        this.customerCode = customerCode;
        this.mobiPhone = mobiPhone;
        this.customerTypeId = customerTypeId;
        this.street = street;
        this.address = address;
        this.shopId = shopId;
        this.phone = phone;
        this.workingOffice = workingOffice;
        this.officeAddress = officeAddress;
        this.taxCode = taxCode;
        this.totalBill = totalBill;
        this.dob = dob;
    }


    public CustomerDTO(Long id, String firstName, String lastName, String customerCode, String mobiPhone){
        setId(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.customerCode = customerCode;
        this.mobiPhone = mobiPhone;
    }

}
