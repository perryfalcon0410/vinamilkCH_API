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
public class RedInvoiceFilter {
    private String searchKeywords;
    private String orderNumber;
    private Date toDate;
    private Date fromDate;
}
