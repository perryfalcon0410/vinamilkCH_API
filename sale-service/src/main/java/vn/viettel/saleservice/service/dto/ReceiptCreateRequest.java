package vn.viettel.saleservice.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.PoPromotionalDetail;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ReceiptCreateRequest extends BaseDTO {
    private String receiptCode;
    private Integer receiptType;
    private String invoiceNumber;
    private String internalNumber;
    private String invoiceDate;
    private String note;
    private String poNumber;
    private long wareHouseId;
    private long poId;
    private List<Long> lstIdRemove;
    private List<PoPromotionalDetail> lstPoPromotionDetail;
    private POAdjustedDTO poAdjustedDTO;
    private POConfirmDTO poConfirmDTO;
    private POBorrowDTO poBorrowDTO;
}
