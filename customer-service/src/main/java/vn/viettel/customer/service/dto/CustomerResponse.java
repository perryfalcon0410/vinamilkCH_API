package vn.viettel.customer.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

@Getter
@Setter
@NoArgsConstructor
public class CustomerResponse extends BaseDTO {
    private Long id;
    private String cusCode;
    private String firstName;
    private String lastName;
    private String barCode;
    private String DOB;
    private String gender;
    private String cusGroup;
    private String status;
    private String isExclusive;
    private String IdNumber;
    private String issueDate;
    private String issuePlace;
    private String phoneNumber;
    private String email;
    private String address;
    private String company;
    private String companyAddress;
    private String taxCode;
    private String memberCardNumber;
    private String memberCardCreateDate;
    private String memberCardType;
    private String cusType;
    private String customerCreateDate;
    private String description;

}
