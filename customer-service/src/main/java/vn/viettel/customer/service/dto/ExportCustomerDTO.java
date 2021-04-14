package vn.viettel.customer.service.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.common.ApParam;
import vn.viettel.core.db.entity.common.Customer;
import vn.viettel.core.db.entity.enums.customer.CustomerType;
import vn.viettel.core.db.entity.voucher.MemberCustomer;
import vn.viettel.core.service.dto.BaseDTO;

import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ExportCustomerDTO extends BaseDTO {

    private String customerCode;
    private String firstName;
    private String lastName;
    private Long genderId;
    private String barCode;
    private Date dob;
    private String customerTypeName;
    private Integer status;
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
    private String memberCardName;
    private String apParamName;
    private Timestamp createdAt;
    private String noted;
}
