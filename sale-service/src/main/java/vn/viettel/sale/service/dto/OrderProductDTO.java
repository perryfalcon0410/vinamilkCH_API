package vn.viettel.sale.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderProductDTO {
    private String productName;

    private String productCode;

    private Float price;

    private Integer stockTotal;

    private Integer status;

    private String uom1;

    private Boolean isCombo;

    private Long comboProductId;

    private int quantity = 0;

    private float totalPrice = 0;

    public void setPrice(Float price) {
        this.price = price;
        this.totalPrice = this.price*this.quantity;
    }

}
