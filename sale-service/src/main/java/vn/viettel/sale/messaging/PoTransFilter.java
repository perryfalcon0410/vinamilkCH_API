package vn.viettel.sale.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PoTransFilter {
    private String transCode;
    private String redInvoiceNo;
    private String internalNumber;
    private String poNo;
    private Date fromDate;
    private Date toDate;
}
