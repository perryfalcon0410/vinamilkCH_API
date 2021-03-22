package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReceiptSearch {

    private Integer receiptType;
    private String invoiceNumber;
    private String fromDate;
    private String toDate;
    private String receiptCode;
    private String internalNumber;
    private String poNumber;
}
