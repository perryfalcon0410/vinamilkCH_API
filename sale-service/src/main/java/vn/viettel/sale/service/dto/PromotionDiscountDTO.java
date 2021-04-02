package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PromotionDiscountDTO {
    private String promotionName;
    private int promotionType;
    private float discount;
    private float discountPercent;
}
