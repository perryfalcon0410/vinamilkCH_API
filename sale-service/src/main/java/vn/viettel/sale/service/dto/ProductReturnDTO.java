package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductReturnDTO {
    private String productCode;
    private String productName;
    private String unit;
    private int quantity;
    private Float pricePerUnit;
    private Float totalPrice;
    private Float discount;
    private Float paymentReturn;
}
