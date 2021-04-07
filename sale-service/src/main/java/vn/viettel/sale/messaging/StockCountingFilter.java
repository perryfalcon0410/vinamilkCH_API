package vn.viettel.sale.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor@AllArgsConstructor
public class StockCountingFilter {
    private String stockCountingCode;
    private Date fromDate;
    private Date toDate;
}
