package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ProductDataDTO extends BaseDTO {

    private Date printDate;
    private Long productId;
    private Float quantity;
    private Float price;
    private Float priceNotVat;
    private Float amount;
    private Float amountNotVat;
    private String note;

}

