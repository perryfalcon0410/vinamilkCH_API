package vn.viettel.core.dto.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AreaDetailDTO {
    private Long provinceId;
    private Long districtId;
    private Long precinctId;
    private String province;
    private String district;
    private String precinct;
    private String provinceName;
    private String districtName;
    private String precinctName;
}
