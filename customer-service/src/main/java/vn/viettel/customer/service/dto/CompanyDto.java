package vn.viettel.customer.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

@Getter
@Setter
@NoArgsConstructor
public class CompanyDto extends BaseDTO {
    private String name;
    private String address;
}
