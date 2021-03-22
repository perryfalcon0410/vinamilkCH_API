package vn.viettel.saleservice.service.dto;

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
    private String barCode;
    private String firstName;
    private String lastName;
    private Date birthday;
    private Long gender;
    private Long customerGroupId;
    private Long status;
    private Long specialCustomer;
    private String noted;
    private String phoneNumber;
    private String email;
    private Long countryId;
    private Long areaId;
    private Long provinceId;
    private Long districtId;
    private Long wardId;
    private String address;
    private Long shopId;
    private Long identityCardId;
    private String companyName;
    private String companyAddress;
    private String taxCode;
    private Long cardMemberId;

    private Long createdBy;
    private Long updatedBy;
    private Long deletedBy;
}
