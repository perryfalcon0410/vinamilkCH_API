package vn.viettel.saleservice.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.WareHouse;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
public class ReceiptCreateRequest extends BaseDTO {
    private String receiptCode;
    private Integer receiptType;
    private String invoiceNumber;
    private String internalNumber;
    private LocalDateTime invoiceDate;
    private LocalDateTime receiptDate;
    private String note;
    private long wareHouseId;
    private long poId;
    private POAdjustedDTO poAdjustedDTO;
    private POConfirmDTO poConfirmDTO;
    private POBorrowDTO poBorrowDTO;
}
