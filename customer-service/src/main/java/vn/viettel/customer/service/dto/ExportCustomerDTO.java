package vn.viettel.customer.service.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ExportCustomerDTO extends BaseDTO {

    private String customerCode;
    private String firstName;
    private String lastName;
    private Integer genderId;
    private String gender;
    private String barCode;
    private Date dob;
    private String customerType;
    private Long status;
    private Boolean isPrivate;
    private String idNo;
    private Date idNoIssuedDate;
    private String idNoIssuedPlace;
    private String mobiPhone;
    private String email;
    private String address;
    private String workingOffice;
    private String officeAddress;
    private String taxCode;
    private Boolean isDefault;
    private String noted;
    private MemberCardDTO memberCard;
}
