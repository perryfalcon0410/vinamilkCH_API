package vn.viettel.report.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuantitySalesReceiptFilter {
    private Long shopId;

    private LocalDate fromDate;

    private LocalDate toDate;

    private Long customerTypeId;

    private String nameOrCodeCustomer;

    private String phoneNumber;

    private Float fromQuantity;

    private Float toQuantity;
}
