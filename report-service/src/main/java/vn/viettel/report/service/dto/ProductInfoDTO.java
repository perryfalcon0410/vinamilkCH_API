package vn.viettel.report.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductInfoDTO extends BaseDTO {
    private String productInfoCode;
    private String productInfoName;
    private String description;
    private Integer type;
    private Integer status;
}
