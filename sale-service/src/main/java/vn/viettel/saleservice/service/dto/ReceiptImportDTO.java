package vn.viettel.saleservice.service.dto;

import com.amazonaws.services.dynamodbv2.xspec.B;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ReceiptImportDTO extends BaseDTO {

    private String receiptCode;
    private Integer receiptQuantity;
    private String invoiceNumber;
    private String internalNumber;
    private Long poId;
    private Long wareHouseId;
    private LocalDateTime receiptDate;
    private LocalDateTime invoiceDate;
    private Float receiptTotal;
    private String note;
    private Integer receiptType;
    private Integer status;


}
