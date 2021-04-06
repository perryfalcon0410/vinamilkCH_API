package vn.viettel.sale.controller;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PromotionReturnDTO {
    private String productCode;
    private String productName;
    private String unit;
    private int quantity;
    private float pricePerUnit;
    private float paymentReturn;
}
