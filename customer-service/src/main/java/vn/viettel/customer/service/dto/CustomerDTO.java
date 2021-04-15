package vn.viettel.customer.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.common.ApParam;
import vn.viettel.core.db.entity.common.Area;
import vn.viettel.core.db.entity.common.CategoryData;
import vn.viettel.core.db.entity.common.CustomerType;
import vn.viettel.core.db.entity.voucher.MemberCard;
import vn.viettel.core.service.dto.BaseDTO;

import javax.persistence.Column;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CustomerDTO extends BaseDTO {

    private String firstName;
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

    private AreaDTO areaDTO;

}
