package vn.viettel.saleservice.service.dto;

import com.amazonaws.services.dynamodbv2.xspec.B;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.sql.Timestamp;


@Getter
@Setter
@NoArgsConstructor
public class ReceiptImportDTO extends BaseDTO {

    private String receiptCode;
    private Integer receiptQuantity;
    private String invoiceNumber;
    private String internalNumber;
    private String poNumber;
    private Long poId;
    private Long wareHouseId;
    private Timestamp receiptDate;
    private Timestamp invoiceDate;
    private Float receiptTotal;
    private String note;
    private Integer receiptType;
    private Integer status;



}
