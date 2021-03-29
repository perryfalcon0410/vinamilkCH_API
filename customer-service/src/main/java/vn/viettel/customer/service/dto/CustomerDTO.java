package vn.viettel.customer.service.dto;



import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class CustomerDTO extends BaseDTO {

    private Long id;
    private String firstName;
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

    private MemberCardDTO memberCardDTO;
}
