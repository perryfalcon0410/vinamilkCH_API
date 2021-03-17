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
    private Long countryId;
    private Long areaId;
    private Long provinceId;
    private Long districtId;
    private Long wardId;
    private long addressId;
}
