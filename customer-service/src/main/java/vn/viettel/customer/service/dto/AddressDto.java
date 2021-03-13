package vn.viettel.customer.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto extends BaseDTO {
    private long countryId;
    private long areaId;
    private long provinceId;
    private long districtId;
    private long wardId;
    private long addressId;
}
