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
    private float price;
    private float totalPrice;
    private float discount;
    private float payment;
    private String productCode;
    private String productName;
    private String unit;
    private String note;
    private boolean isFreeItem;
    private float zmPromotion;
    private String promotionCode;
    private Integer levelNumber; // get from where?
}
