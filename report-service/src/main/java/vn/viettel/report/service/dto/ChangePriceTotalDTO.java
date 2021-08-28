package vn.viettel.report.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class ChangePriceTotalDTO {
    private String redInvoiceNo;
    private Long stt;
    private LocalDateTime orderDate;
    private String poNumber;
    private String internalNumber;
    private String transCode;
    private Long totalQuantity;
    private Double totalPriceInput;
    private Double totalPriceOutput;

    public ChangePriceTotalDTO(String redInvoiceNo, Long stt, LocalDateTime orderDate, String poNumber, String internalNumber, String transCode) {
        this.redInvoiceNo = redInvoiceNo;
        this.stt = stt;
        this.orderDate = orderDate;
        this.poNumber = poNumber;
        this.internalNumber = internalNumber;
        this.transCode = transCode;

    }
    public ChangePriceTotalDTO(String redInvoiceNo, Long stt, LocalDateTime orderDate, String poNumber, String internalNumber, String transCode, Long totalQuantity, Double totalPriceInput, Double totalPriceOutput) {
        this.redInvoiceNo = redInvoiceNo;
        this.stt = stt;
        this.orderDate = orderDate;
        this.poNumber = poNumber;
        this.internalNumber = internalNumber;
        this.transCode = transCode;
        this.totalQuantity = totalQuantity;
        this.totalPriceInput = totalPriceInput;
        this.totalPriceOutput = totalPriceOutput;
    }


}
