package vn.viettel.sale.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.beans.Transient;
import java.text.DecimalFormat;

@Getter
@Setter
@NoArgsConstructor
public class PoReportProductDetailDTO {

    private static final DecimalFormat formatter = new DecimalFormat("###,###,###");

    private int orderNumber;

    private String productCode;

    private String productName;

    private String unit;

    private Integer quantity;

    private String price;

    private String totalPrice;

    private String priceNotVat;

    private String totalPriceNotVat;

    public PoReportProductDetailDTO(String productCode, String productName, String unit) {
        this.productCode = productCode;
        this.productName = productName;
        this.unit = unit;
    }

    public void setPrice(Float price) {
        if(price == null){
            this.price = "0";
        }else{
            this.price = formatter.format(price);
        }
    }

    public void setTotalPrice(Float totalPrice) {
        if(totalPrice == null){
            this.totalPrice = "0";
        }else {
            this.totalPrice = formatter.format(totalPrice);
        }
    }

    public void setPriceNotVat(Float priceNotVat) {
        if(priceNotVat == null){
            this.priceNotVat = "0";
        }else {
            this.priceNotVat = formatter.format(priceNotVat);
        }
    }

    public void setTotalPriceNotVat(Float totalPriceNotVat) {
        if(totalPriceNotVat == null){
            this.totalPriceNotVat = "0";
        }else{
            this.totalPriceNotVat = formatter.format(totalPriceNotVat);
        }

    }
}
