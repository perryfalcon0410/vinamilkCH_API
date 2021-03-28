package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderDetailDTO {
    private Long productId;
    private int quantity;
    private boolean isFreeItem;
    private float zmPromotion;
    private String promotionCode;
    private Integer levelNumber; // get from where?
}
