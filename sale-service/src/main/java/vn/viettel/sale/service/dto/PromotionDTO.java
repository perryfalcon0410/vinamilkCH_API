package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
public class PromotionDTO {
    private String discountName;
    private String discountType;
    private int discount;
    private float disPercent;
    private String createdBy;
    private String confirmedBy;
}
