package vn.viettel.sale.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.beans.Transient;
import java.text.DecimalFormat;

@Getter
@Setter
public class PoProductReportDTO {

    private static final DecimalFormat formatter = new DecimalFormat("###,###,###");

    private String productCode;

    private String productName;

    private String unit;

    private Integer quantity;

    private String price;

    private String totalPrice;

    public PoProductReportDTO(String productCode, String productName, String unit) {
        this.productCode = productCode;
        this.productName = productName;
        this.unit = unit;
    }

    public PoProductReportDTO withQuantityAndPrice(Integer quantity, Float price) {
        this.quantity = quantity;
        this.price = formatter.format(price);
        this.totalPrice = formatter.format(price*quantity);
        return this;
    }

}