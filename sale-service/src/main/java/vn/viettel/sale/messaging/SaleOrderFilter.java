package vn.viettel.sale.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SaleOrderFilter {
    private String searchKeyword;
    private String orderNumber;
    private Integer usedRedInvoice;
    private Date fromDate;
    private Date toDate;
}
