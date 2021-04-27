package vn.viettel.sale.service.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportProductDTO {

    private String productCode;

    private String productName;

    private String unit;

    private Integer quantity;

    private Float price = 0F;

    private Float totalPrice = 0F;

    private Float priceNotVat = 0F;

    private Float totalPriceNotVat = 0F;

    public ReportProductDTO(String productCode, String productName) {
        this.productCode = productCode;
        this.productName = productName;
    }

    public void setPrice(Float price) {
        if(price != null) this.price = price;
    }

    public void setTotalPrice(Float totalPrice) {
        if(totalPrice != null) this.totalPrice = totalPrice;
    }

    public void setPriceNotVat(Float priceNotVat) {
        if(priceNotVat != null) this.priceNotVat = priceNotVat;
    }

    public void setTotalPriceNotVat(Float totalPriceNotVat) {
        if(totalPriceNotVat != null) this.totalPriceNotVat = totalPriceNotVat;
    }

}
