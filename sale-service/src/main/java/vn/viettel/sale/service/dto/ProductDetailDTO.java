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
public class ProductDetailDTO extends BaseDTO {

    private String productName;

    private String productCode;

    private String uom1;

    private String uom2;

    private Integer quantity;

    private Float unitPrice;

    private Float intoMoney;
}
