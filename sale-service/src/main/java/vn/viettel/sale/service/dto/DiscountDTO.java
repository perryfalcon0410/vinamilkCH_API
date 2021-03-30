package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DiscountDTO {
    private String promotionName;
    private int promotionType;
    private float discount;
    private float discountPercent;
}
