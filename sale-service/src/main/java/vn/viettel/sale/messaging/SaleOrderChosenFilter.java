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
public class SaleOrderChosenFilter {
    private String orderNumber;
    private String searchKeyword;
    private String product;
    private Date fromDate;
    private Date toDate;
}
