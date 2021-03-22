package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReceiptExportRequest extends BaseDTO{
    private Long receiptImportId;
    private Long reId;
    private Integer ReceiptExportType;
    private Long wareHouseId;
    private String note;
    private List<Integer> litQuantityRemain;
    private Boolean isRemainAll;

}
