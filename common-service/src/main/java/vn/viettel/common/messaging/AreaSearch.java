package vn.viettel.common.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AreaSearch extends BaseDTO {
    private String areaCode;
    private String areaName;
    private String provinceAndDistrictName;
    private boolean isDefault;


    public AreaSearch(Long id, String areaCode, String areaName, String provinceName, String districtName, boolean isDefault) {
        setId(id);
        if(provinceName == null) provinceName ="";
        if(districtName == null) districtName ="";
        this.areaCode = areaCode;
        this.areaName = areaName;
        this.provinceAndDistrictName = provinceName +" - "+ districtName;
        this.isDefault = isDefault;
    }

}
