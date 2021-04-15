package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AreaDTO {
    private Long provinceId;
    private Long districtId;
    private Long precinctId;
}
