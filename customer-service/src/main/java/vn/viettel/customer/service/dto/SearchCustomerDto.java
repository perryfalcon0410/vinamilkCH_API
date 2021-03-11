package vn.viettel.customer.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SearchCustomerDto {
    private String idCardNumber;
    private String phoneNumber;
    private String name;
    private String code;
}
