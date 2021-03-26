package vn.viettel.sale.messaging;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ReceiptUpdateRequest {
    private Long poId;
    private Integer importType;
    private Long shopId;
    ////////////////////////////////////////////
    private String transCode;
    private Date transDate;
    private Long wareHouseTypeId;
    private Integer type;
    private String redInvoiceNo;
    private String internalNumber;
    private String poNumber;
    private Date orderDate;
    private String note;
}
