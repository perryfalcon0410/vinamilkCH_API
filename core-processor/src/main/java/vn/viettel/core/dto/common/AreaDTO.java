package vn.viettel.core.dto.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import javax.persistence.Column;

@Getter
@Setter
@NoArgsConstructor
public class AreaDTO extends BaseDTO{
    private String areaCode;
    private String areaName;
    private Long parentAreaId;
    private String province;
    private String provinceName;
    private String district;
    private String districtName;
    private String precinct;
    private String precinctName;
    private String parentCode;
    private Integer type;
}
