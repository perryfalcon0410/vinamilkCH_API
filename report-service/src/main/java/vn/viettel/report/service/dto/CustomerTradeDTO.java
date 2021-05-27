package vn.viettel.report.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ApiModel(description = "Thông tin khách hàng")
public class CustomerTradeDTO  {
    @Id
    @Column(name = "ID")
    private Long id;
    @ApiModelProperty(notes = "Mã khách hàng")
    @Column(name = "CUSTOMER_CODE")
    private String customerCode;
    @ApiModelProperty(notes = "Tên đầy đủ")

    @Column(name = "FULL_NAME")
    private String fullName;
    @ApiModelProperty(notes = "Tên")
    @Column(name = "FIRST_NAME")
    private String firstName;
    @ApiModelProperty(notes = "Họ")
    @Column(name = "LAST_NAME")
    private String lastName;

    @ApiModelProperty(notes = "Mã loại khách hàng")
    @Column(name = "CUS_TYPE_CODE")
    private String cusTypeCode;
    @ApiModelProperty(notes = "Tên loại khách hàng")
    @Column(name = "CUS_TYPE_NAME")
    private String cusTypeName;

    @ApiModelProperty(notes = "Ngày tháng năm sinh")
    @Column(name = "BIRTH_DAY")
    private Date birthDay;
    @ApiModelProperty(notes = "Năm sinh")
    @Column(name = "YEAR_DOB")
    private String yearDob;
    @ApiModelProperty(notes = "Nơi Sinh")
    @Column(name = "PLACE_OF_BIRTH")
    private String placeOfBirth;

    @ApiModelProperty(notes = "Số điện thoại")
    @Column(name = "PHONE")
    private String phone;
    @ApiModelProperty(notes = "Số điện thoại")
    @Column(name = "MOBIPHONE")
    private String mobiPhone;
    @ApiModelProperty(notes = "Email")
    @Column(name = "EMAIL")
    private String email;
    @ApiModelProperty(notes = "Fax")
    @Column(name = "FAX")
    private String fax;

    @ApiModelProperty(notes = "Địa chỉ")
    @Column(name = "ADDRESS")
    private String address;
    @ApiModelProperty(notes = "Quốc gia")
    @Column(name = "COUNTRY")
    private String country;
    @ApiModelProperty(notes = "Tỉnh, thành phố")
    @Column(name = "PROVINCE")
    private String province;
    @ApiModelProperty(notes = "Quận, huyện")
    @Column(name = "DISTRICT")
    private String district;
    @ApiModelProperty(notes = "Xã, phương")
    @Column(name = "PRECINCT")
    private String precinct;
    @ApiModelProperty(notes = "Số nhà")
    @Column(name = "STREET")
    private String street;

    @ApiModelProperty(notes = "Nơi công tác")
    @Column(name = "WORKING_OFFICE")
    private String workingOffice;
    @ApiModelProperty(notes = "Địa chỉ công tác")
    @Column(name = "OFFICE_ADDRESS")
    private String officeAddress;

    @ApiModelProperty(notes = "Giới tính")
    @Column(name = "GENDER")
    private String gender;
    @ApiModelProperty(notes = "Trạng thái hoạt động")
    @Column(name = "STATUS")
    private String status;

    @ApiModelProperty(notes = "Nghề nghiệp")
    @Column(name = "JOB")
    private String job;
    @ApiModelProperty(notes = "Hôn nhân")
    @Column(name = "MARITAL")
    private String marital;

    @ApiModelProperty(notes = "Số CMND")
    @Column(name = "ID_NO")
    private String idNo;
    @ApiModelProperty(notes = "Ngày cấp CMND")
    @Column(name = "ID_NO_ISSUED_DATE")
    private Date idNoIssuedDate;
    @ApiModelProperty(notes = "Nơi cấp CMND")
    @Column(name = "ID_NO_ISSUED_PLACE")
    private String idNoIssuedPlace;

    @ApiModelProperty(notes = "Số hộ chiếu")
    @Column(name = "PASSPORT_NO")
    private String passportNo;
    @ApiModelProperty(notes = "Ngày cấp hộ chiếu")
    @Column(name = "PASSPORT_NO_ISSUED_DATE")
    private Date passportNoIssuedDate;
    @ApiModelProperty(notes = "Ngày hết hạn hộ chiếu")
    @Column(name = "PASSPORT_NO_EXPIRY_DATE")
    private Date passportNoExpirydPlace;
    @ApiModelProperty(notes = "Nơi cấp hộ chiếu")
    @Column(name = "PASSPORT_NO_ISSUED_PLACE")
    private String passportNoIssuedPlace;

    @ApiModelProperty(notes = "Ghi chú")
    @Column(name = "NOTED")
    private String noted;

    @ApiModelProperty(notes = "Người tạo")
    @Column(name = "CREATED_BY")
    private String createBy;
    @ApiModelProperty(notes = "Ngày tạo")
    @Column(name = "CREATED_AT")
    private Date createAt;
    @ApiModelProperty(notes = "Người cập nhật")
    @Column(name = "UPDATED_BY")
    private String updatedBy;
    @ApiModelProperty(notes = "Ngày cập nhật")
    @Column(name = "UPDATED_AT")
    private Date updatedAt;

    @ApiModelProperty(notes = "Ngày mua hàng cuối")
    @Column(name = "ORDER_DATE")
    private Date orderDate;
    @ApiModelProperty(notes = "Doanh số tích lũy")
    @Column(name = "SALE_AMOUNT")
    private String saleAmount;

}
