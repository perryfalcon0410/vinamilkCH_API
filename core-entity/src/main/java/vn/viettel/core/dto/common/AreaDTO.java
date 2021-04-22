package vn.viettel.core.dto.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

@Getter
@Setter
@NoArgsConstructor
public class AreaDTO extends BaseDTO{
    private String areaCode;
    private String areaName;
    private Long provinceId;
    private Long districtId;
    private Long precinctId;
    private Integer type;
    private Long parentAreaId;
}