package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

@Getter
@Setter
@NoArgsConstructor
public class WareHouseTypeDTO extends BaseDTO {
    @ApiModelProperty(notes = "Mã loại kho hàng")
    private String wareHouseTypeCode;
    @ApiModelProperty(notes = "Tên loại kho hàng")
    private String wareHouseTypeName;
    @ApiModelProperty(notes = "Tình trạng")
    private Integer status;
    @ApiModelProperty(notes = "Trường mặc định")
    private Integer isDefault;

    public WareHouseTypeDTO(Long id, String wareHouseTypeCode, String wareHouseTypeName, Integer isDefault) {
        this.setId(id);
        this.wareHouseTypeCode = wareHouseTypeCode;
        this.wareHouseTypeName = wareHouseTypeName;
        this.isDefault = isDefault;
    }

}
