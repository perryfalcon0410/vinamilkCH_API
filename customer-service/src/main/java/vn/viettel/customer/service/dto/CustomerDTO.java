package vn.viettel.customer.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import javax.persistence.Column;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class CustomerDTO extends BaseDTO {

    private String firstName;
    private Long shopId;
    private String customerCode;
    private String lastName;
    private Integer genderId;
    private String gender;
    private String barCode;
    private Date dob;
    private Long customerTypeId;
    private String customerType;
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
    private String noted;


    private Float totalBill;
//    private Integer limitDayOrder;
//    private Integer limitDayAmount;
//    private Integer limitMonthOrder;
//    private Integer limitMonthAmount;
    private Integer dayOrderNumber;
    private Integer dayOrderAmount;
    private Integer monthOrderNumber;
    private Integer monthOrderAmount;


    private MemberCardDTO memberCardDTO;
}
