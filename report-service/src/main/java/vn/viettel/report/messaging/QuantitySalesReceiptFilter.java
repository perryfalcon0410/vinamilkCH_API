package vn.viettel.report.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuantitySalesReceiptFilter {
    private Long shopId;

    private LocalDateTime fromDate;

    private LocalDateTime toDate;

    private Long customerTypeId;

    private String nameOrCodeCustomer;

    private String phoneNumber;

    private Long fromQuantity;

    private Long toQuantity;

}
