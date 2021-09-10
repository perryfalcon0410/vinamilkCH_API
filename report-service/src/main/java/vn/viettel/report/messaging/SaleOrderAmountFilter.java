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
public class SaleOrderAmountFilter {

    private Long shopId;

    private LocalDateTime fromDate;

    private LocalDateTime toDate;

    private Long customerTypeId;

    private String nameOrCodeCustomer;

    private String phoneNumber;

    private Double fromAmount;

    private Double toAmount;

    @Override
    public String toString() {
        return "SaleOrderAmountFilter{" +
                "shopId=" + shopId +
                ", fromDate=" + fromDate +
                ", toDate=" + toDate +
                ", customerTypeId=" + customerTypeId +
                ", nameOrCodeCustomer='" + nameOrCodeCustomer + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", fromAmount=" + fromAmount +
                ", toAmount=" + toAmount +
                '}';
    }
}
