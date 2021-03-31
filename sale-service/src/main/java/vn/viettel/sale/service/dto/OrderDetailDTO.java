package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
public class OrderDetailDTO {
    private Long productId;
    private int quantity;
    private float price;
    private float discount;
    private float totalPrice;
    private float payment;
    private String productCode;
    private String productName;
    private String unit;
    private String note;
    private boolean isFreeItem;
    private float zmPromotion;
    private String promotionCode;
    private Date orderDate;
    private Integer levelNumber; // get from where?
}
