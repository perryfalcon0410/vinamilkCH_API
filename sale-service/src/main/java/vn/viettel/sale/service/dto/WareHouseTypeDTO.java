package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

@Getter
@Setter
@NoArgsConstructor
public class WareHouseTypeDTO extends BaseDTO {
    private String wareHouseTypeCode;
    private String wareHouseTypeName;
    private Integer status;
}
