package vn.viettel.commonservice.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LocationResponse {
    private CountryDto country;
    private ProvinceDto province;
    private DistrictDto district;
    private WardDto ward;
    private AddressDto address;
}
