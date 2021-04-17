package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ProductDTO extends BaseDTO {

    private String productName;

    private String productCode;

    private Integer status;

    private String uom1;

    private Boolean isCombo;

    private Long comboProductId;

    private Float price;

    private Integer stockTotal;
}

