package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PromotionDTO {
    private String productCode;
    private String productName;
    private String customerName;
    private int quantityPromotion;
}
