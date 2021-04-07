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

    private String productCode;

    private String productName;

    private String unit;

    private Integer quantity;

    private String price;

    private String totalPrice;

    private String priceNotVar;

    private String totalPriceNotVar;

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

    public void setPriceNotVar(Float priceNotVar) {
        if(priceNotVar == null){
            this.priceNotVar = "0";
        }else {
            this.priceNotVar = formatter.format(priceNotVar);
        }
    }

    public void setTotalPriceNotVar(Float totalPriceNotVar) {
        if(totalPriceNotVar == null){
            this.totalPriceNotVar = "0";
        }else{
            this.totalPriceNotVar = formatter.format(totalPriceNotVar);
        }

    }
}
