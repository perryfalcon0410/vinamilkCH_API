package vn.viettel.sale.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;
import vn.viettel.core.util.Constants;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportProductTransDetailDTO extends BaseDTO {

    private Integer transType;
    private String type;
    private String transCode;
    private String poNumber;
    private String invoiceNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime transDate;
    private String internalNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime orderDate;

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
