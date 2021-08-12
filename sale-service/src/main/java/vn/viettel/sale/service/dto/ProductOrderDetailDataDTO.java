package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductOrderDetailDataDTO {
    private Long productId;
    private String productCode;
    private Integer quantity;
    private Double price;
    private Double totalPrice;
    private Double priceNotVAT;
    private Double totalPriceNotVAT;

    public void setPrice(Double price) {
        if(price == null) price = 0.0;
        this.price = price;
        this.totalPrice = this.quantity*this.price;
    }

    public void setPriceNotVAT(Double priceNotVAT) {
        if(priceNotVAT == null) priceNotVAT =0.0;
        this.priceNotVAT = priceNotVAT;
        this.totalPriceNotVAT = this.quantity*this.priceNotVAT;
    }

}
