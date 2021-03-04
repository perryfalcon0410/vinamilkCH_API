package vn.viettel.customer.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomerResponse {
    private long id;
    private String cusCode;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String DOB;
    private String gender;
    private String address;
    private String status;
    private String cusGroup;
    private String createDate;
}
