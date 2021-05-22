package vn.viettel.report.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SaleOrderAmountFilter {

    private Long shopId;

    private Date fromDate;

    private Date toDate;

    private Long customerTypeId;

    private String nameOrCodeCustomer;

    private String phoneNumber;

    private Float fromAmount;

    private Float toAmount;

}
