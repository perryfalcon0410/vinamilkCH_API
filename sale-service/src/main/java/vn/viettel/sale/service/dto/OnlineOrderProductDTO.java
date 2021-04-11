package vn.viettel.sale.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OnlineOrderProductDTO {

    private Long id;

    private String productName;

    private String productCode;

    private String uom1;

    private Integer safetyStock;

    private Integer quantity;

    private float price;

    private float totalPrice;

    public void setPrice(Float price) {
        this.price = price;
        this.totalPrice = this.price*this.quantity;
    }
}
