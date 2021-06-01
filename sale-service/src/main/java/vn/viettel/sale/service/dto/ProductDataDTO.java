package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

@Getter
@Setter
@NoArgsConstructor
public class ProductDataDTO extends BaseDTO {

    private Long productId;
    private Float quantity;
    private Float priceNotVat;
    private Float amountNotVat;
    private Integer vat;
    private Float valueAddedTax;
    private String groupVat;
    private String note;

}

