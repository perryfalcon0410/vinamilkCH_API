package vn.viettel.sale.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaleOrderFilter {
    private String customer;
    private String orderNumber;
    private int status;
    private Date fromDate;
    private Date toDate;
}
