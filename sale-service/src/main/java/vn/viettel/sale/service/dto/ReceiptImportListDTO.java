package vn.viettel.sale.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor

public class ReceiptImportListDTO {
    private Long id;
    private String transCode;
    private String redInvoiceNo;
    private String internalNumber;
    private Integer totalQuantity;
    private Float totalAmount;
    private Date transDate;
    private String note;

    public ReceiptImportListDTO(Long id, String transCode, String redInvoiceNo, String internalNumber, Integer totalQuantity, Float totalAmount, Date transDate, String note) {
        this.id = id;
        this.transCode = transCode;
        this.redInvoiceNo = redInvoiceNo;
        this.internalNumber = internalNumber;
        this.totalQuantity = totalQuantity;
        this.totalAmount = totalAmount;
        this.transDate = transDate;
        this.note = note;
    }
}

