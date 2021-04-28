package vn.viettel.sale.messaging;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductOrderRequest {
    private Long productId;
    private String productCode;
    private Integer quantity;
//    private Float pricePerUnit;
//    private Float amount;
//    private Float discount;
//    private Float payment;
//    private String productCode;
//    private String productName;
//    private String unit;
//    private String note;
//    private Boolean isFreeItem;
    private Float zmPromotion;
//    private String promotionCode;
//    private Integer levelNumber; // get from where?

}
