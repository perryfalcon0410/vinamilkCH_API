package vn.viettel.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShopParamDTO extends BaseDTO {

    private String shopId;

    private String type;

    private String code;

    private String name;

    private String description;

    private Integer status;
}
