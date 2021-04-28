package vn.viettel.sale.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportProductTransDetailDTO extends BaseDTO {

    private String type;
    private String transCode;
    private String poNumber;
    private String invoiceNumber;
    private Date transDate;
    private String internalNumber;
    private Date orderDate;

    private Integer totalQuantity;
    private Float totalPriceNotVat = 0F;
    private Float totalPriceVat = 0F;
    private Float totalPrice = 0F;

    private String note;

    public Float getTotalPriceVat() {
        return this.totalPrice - this.totalPriceNotVat;
    }

    public Float addTotalPriceNotVat(Float totalPriceNotVat) {
        this.totalPriceNotVat += totalPriceNotVat;
        return this.totalPriceNotVat;
    }

    public Float addTotalPrice(Float totalPrice) {
        this.totalPrice += totalPrice;
        return this.totalPrice;
    }

}
