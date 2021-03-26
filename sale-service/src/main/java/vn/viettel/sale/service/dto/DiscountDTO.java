package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DiscountDTO {
    private String discountName;
    private String discountType;
    private int discount;
    private float disPercent;
    private String createdBy;
    private String confirmedBy;
}
