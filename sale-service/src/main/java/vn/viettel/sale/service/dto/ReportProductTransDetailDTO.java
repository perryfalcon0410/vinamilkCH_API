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
    private Double totalPriceNotVat = 0D;
    private Double totalPriceVat = 0D;
    private Double totalPrice = 0D;

    private String note;

    public Double getTotalPriceVat() {
        return this.totalPrice - this.totalPriceNotVat;
    }

    public Double addTotalPriceNotVat(Double totalPriceNotVat) {
        this.totalPriceNotVat += totalPriceNotVat;
        return this.totalPriceNotVat;
    }

    public Double addTotalPrice(Double totalPrice) {
        this.totalPrice += totalPrice;
        return this.totalPrice;
    }

}
