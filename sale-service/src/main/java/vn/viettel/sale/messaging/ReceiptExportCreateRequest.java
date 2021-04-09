package vn.viettel.sale.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.messaging.BaseRequest;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptExportCreateRequest extends BaseRequest {
    private Integer importType;
    private Long receiptImportId;
    private List<Integer> litQuantityRemain;
    private Boolean isRemainAll;
    ////////////////////////////////////////////
    private String transCode;
    private Date transDate;
    private Integer type;
    private String redInvoiceNo;
    private String internalNumber;
    private String poNumber;
    private Date orderDate;
    private String note;
}
