package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class OrderDetailDTO {
    private Long productId;
    private Integer quantity;
    private Float PricePerUnit;
    private Float totalPrice;
    private Float discount;
    private Float payment;
    private String productCode;
    private String productName;
    private String unit;
    private String note;
    private Boolean isFreeItem;
    private Float zmPromotion;
    private String promotionCode;
    private Date orderDate;
    private Integer levelNumber; // get from where?
}
