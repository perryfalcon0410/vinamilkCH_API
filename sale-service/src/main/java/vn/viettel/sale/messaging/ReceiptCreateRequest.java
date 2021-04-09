package vn.viettel.sale.messaging;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ReceiptCreateRequest {
    private Long poId;
    private Integer importType;

    ////////////////////////////////////////////
    private String transCode;
    private Date transDate;
    private Integer type;
    private String redInvoiceNo;
    private String internalNumber;
    private String poNumber;
    private Date orderDate;
    private String note;
    private List<ReceiptCreateDetailRequest> lst;


}
