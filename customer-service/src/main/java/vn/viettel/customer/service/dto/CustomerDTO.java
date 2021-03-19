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
    private String cusCode;
    private String barCode;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String taxCode;
    private Date DOB;
    private String email;
    private Long gender;
    private Boolean status;
    private Long cusType;
    private Boolean exclusive;
    private String description;
    private Long addressId;
    private Long shopId;
    private Long groupId;
    private Long idCardId;
    private Long cardMemberId;
    private Long companyId;
    private Long createdBy;
    private Long updatedBy;
    private Long deletedBy;
}
