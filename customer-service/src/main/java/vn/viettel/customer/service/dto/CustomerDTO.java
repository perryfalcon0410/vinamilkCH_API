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
    private String customerCode;
    private String firstName;
    private String lastName;
    private String phone;
    private Date dob;
    private Long genderId;
    private String gender;
    private Long status;
    private Long customerTypeId;
    private String customerType;

    private Long createdBy;
    private Long updatedBy;
    private Long deletedBy;
}
