package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DiscountDTO {
    private String promotionName;
    private String promotionType;
    private String voucherType;
    private Double discountPrice;
    private Double discountPercent;
}
