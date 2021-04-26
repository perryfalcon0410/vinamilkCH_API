package vn.viettel.sale.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ComboProductTransProductDTO extends BaseDTO {

    private String comboProductCode;

    private String productCode;

    private String productName;

    private Integer factor;

    private Integer quantity;

    private Float price;

}