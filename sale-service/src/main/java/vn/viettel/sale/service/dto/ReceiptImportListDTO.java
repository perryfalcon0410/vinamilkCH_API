package vn.viettel.sale.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
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
    private LocalDateTime transDate;
    private String note;
    private String returnNote;
    private Long poId;
    /**
     *
     *  receiptType = 0 list Po_Confirm
     *  receiptType = 1 list Stock_Adjustment_Trans
     *  receiptType = 1 list Stock_Borrowing_Trans
     *
     */
    @NotNull
    private Integer receiptType;

    public ReceiptImportListDTO(Long id, String transCode, String redInvoiceNo, String internalNumber, Integer totalQuantity, Float totalAmount, LocalDateTime transDate, String note, Integer type, String returnNote, Long poId) {
        this.id = id;
        this.transCode = transCode;
        this.redInvoiceNo = redInvoiceNo;
        this.internalNumber = internalNumber;
        this.totalQuantity = totalQuantity;
        this.totalAmount = totalAmount;
        this.transDate = transDate;
        this.note = note;
        this.receiptType = receiptType;
        this.returnNote = returnNote;
        this.poId = poId;
    }
}

