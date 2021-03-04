package vn.viettel.commonservice.Service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProDisDto {
    private long provinceId;
    private String province;
    private long districtId;
    private String district;

    public ProDisDto(long provinceId, String province, long districtId, String district) {
        this.provinceId = provinceId;
        this.province = province;
        this.districtId = districtId;
        this.district = district;
    }
}
