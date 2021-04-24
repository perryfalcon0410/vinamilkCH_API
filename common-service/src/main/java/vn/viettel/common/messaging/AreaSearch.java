package vn.viettel.common.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AreaSearch extends BaseDTO {
    private String areaCode;
    private String areaName;
    private String provinceAndDistrictName;
    private boolean isDefault;
}
