package vn.viettel.saleservice.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class ReceiptExportDTO extends BaseDTO{
    private String receiptExportCode;
    private Integer receiptExportQuantity;
    private String invoiceNumber;
    private String poNumber;
    private String internalNumber;
    private Long poId;
    private Long wareHouseId;
    private Timestamp receiptExportDate;
    private Timestamp invoiceDate;
    private Float receiptExportTotal;
    private String note;
    private Integer receiptExportType;
    private Integer status;
}
