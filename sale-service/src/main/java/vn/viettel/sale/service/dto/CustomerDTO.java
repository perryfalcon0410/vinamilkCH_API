package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class CustomerDTO extends BaseDTO {
    private String customerCode;
    private Long shopId;
    private String firstName;
    private String lastName;
    private String phone;
    private Date dob;
    private String placeOfBirth;
    private Long genderId;
    private String gender;
    private Long status;
    private Long customerTypeId;
    private String customerType;
    private Long occupationId;
    private String occupationDescription;
    private Long martialStatusId;
    private String idNo;
    private Date idNoIssuedDate;
    private String idNoIssuedPlace;
    private String passportNo;
    private Date passportNoIssuedDate;
    private String passportNoIssuedPlace;
    private String fax;
    private String mobiPhone;
    private String email;
    private Long areaId;
    private String street;
    private String address;
    private String workingOffice;
    private String officeAddress;
    private String taxCode;

    private Integer scoreCumulated;

    private Long createdBy;
    private Long updatedBy;
    private Long deletedBy;
}
