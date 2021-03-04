package vn.viettel.saleservice.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ReceiptSearch {

    private Integer receiptType;
    private String invoiceNumber;
    private String fromDate;
    private String toDate;
}
