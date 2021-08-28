package vn.viettel.sale.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.util.Constants;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ApiModel(description = "Thông tin phiếu nhập")
public class ReceiptImportListDTO implements ReceiptImportDTO {

    @ApiModelProperty(notes = "ID phiếu nhập")
    private Long id;

    @ApiModelProperty(notes = "Mã phiếu nhập")
    private String transCode;

    @ApiModelProperty(notes = "Số hóa đơn đỏ")
    private String redInvoiceNo;

    @ApiModelProperty(notes = "Số nội bộ")
    private String internalNumber;

    @ApiModelProperty(notes = "Số lượng")
    private Integer totalQuantity;

    @ApiModelProperty(notes = "Giá trị")
    private Float totalAmount;

    @ApiModelProperty(notes = "Ngày nhập phiếu")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime transDate;

    @ApiModelProperty(notes = "Ghi chú")
    private String note;

    @ApiModelProperty(notes = "Ghi chú trả hàng")
    private String returnNote;

    @ApiModelProperty(notes = "ID Po")
    private Long poId;
    /**
     *
     *  receiptType = 0 list Po_Confirm
     *  receiptType = 1 list Stock_Adjustment_Trans
     *  receiptType = 2 list Stock_Borrowing_Trans
     *
     */
    @ApiModelProperty(notes = "Loại phiếu nhập: phiếu po = 0, phiếu điều chỉnh = 1, phiếu mượn")
    @NotNull
    private Integer receiptType;

    public ReceiptImportListDTO(Long id, String transCode, String redInvoiceNo, String internalNumber,
                                Integer totalQuantity, Double totalAmount, LocalDateTime transDate, String note,
                                Integer receiptType, Long poId) {
        this.id = id;
        this.transCode = transCode;
        this.redInvoiceNo = redInvoiceNo;
        this.internalNumber = internalNumber;
        this.totalQuantity = totalQuantity;
        this.totalAmount = totalAmount == null ? 0 : totalAmount.floatValue();
        this.transDate = transDate;
        this.note = note;
        this.receiptType = receiptType;
        this.poId = poId;
    }

    public ReceiptImportListDTO(Long id, String transCode, String redInvoiceNo, String internalNumber, Integer totalQuantity,
                                Double totalAmount, LocalDateTime transDate, String note, Integer receiptType) {
        this.id = id;
        this.transCode = transCode;
        this.redInvoiceNo = redInvoiceNo;
        this.internalNumber = internalNumber;
        this.totalQuantity = totalQuantity;
        this.totalAmount = totalAmount == null ? 0 : totalAmount.floatValue();
        this.transDate = transDate;
        this.note = note;
        this.receiptType = receiptType;
    }
}
