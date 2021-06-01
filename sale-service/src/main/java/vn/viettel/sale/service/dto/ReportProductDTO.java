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

    private Double price = 0D;

    private Double totalPrice = 0D;

    private Double priceNotVat = 0D;

    private Double totalPriceNotVat = 0D;

    public ReportProductDTO(String productCode, String productName) {
        this.productCode = productCode;
        this.productName = productName;
    }

    public void setPrice(Double price) {
        if(price != null) this.price = price;
    }

    public void setTotalPrice(Double totalPrice) {
        if(totalPrice != null) this.totalPrice = totalPrice;
    }

    public void setPriceNotVat(Double priceNotVat) {
        if(priceNotVat != null) this.priceNotVat = priceNotVat;
    }

    public void setTotalPriceNotVat(Double totalPriceNotVat) {
        if(totalPriceNotVat != null) this.totalPriceNotVat = totalPriceNotVat;
    }

}
