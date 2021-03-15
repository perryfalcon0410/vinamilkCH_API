package vn.viettel.saleservice.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReceiptExportRequest extends BaseDTO{
    private Long receiptImportId;
    private Integer ReceiptExportType;
    private Long wareHouseId;
    private String note;

}
