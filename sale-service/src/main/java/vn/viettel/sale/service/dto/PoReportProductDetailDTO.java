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

    public PoReportProductDetailDTO(String productCode, String productName, String unit) {
        this.productCode = productCode;
        this.productName = productName;
        this.unit = unit;
    }

    public PoReportProductDetailDTO setPrice(Float price) {
        this.price = formatter.format(price);
        return this;
    }

    public PoReportProductDetailDTO setTotalPrice(Float totalPrice) {
        this.totalPrice = formatter.format(totalPrice);
        return this;
    }

}
