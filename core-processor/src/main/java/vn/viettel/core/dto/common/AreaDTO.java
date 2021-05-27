package vn.viettel.core.dto.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import javax.persistence.Column;

@Getter
@Setter
@NoArgsConstructor
public class AreaDTO extends BaseDTO{
    @ApiModelProperty(notes = "Mã địa bàn")
    private String areaCode;
    @ApiModelProperty(notes = "Tên địa bàn")
    private String areaName;
    @ApiModelProperty(notes = "Id địa bàn cha- Quận/Huyện")
    private Long parentAreaId;
    @ApiModelProperty(notes = "Mã tỉnh của địa bàn")
    private String province;
    @ApiModelProperty(notes = "Tên tỉnh của địa bàn")
    private String provinceName;
    @ApiModelProperty(notes = "Mã huyện của địa bàn")
    private String district;
    @ApiModelProperty(notes = "Tên huyện của địa bàn")
    private String districtName;
    @ApiModelProperty(notes = "Mã phường xã của địa bàn")
    private String precinct;
    @ApiModelProperty(notes = "Tên phường xã của địa bàn")
    private String precinctName;
    @ApiModelProperty(notes = "Id Tỉnh/Tp")
    private Long provinceId;

}
