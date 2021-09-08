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
public class CustomerNotTradeFilter {
    private Date fromDate;
    private Date toDate;
    private Long ShopId;

    @Override
    public String toString() {
        return "CustomerNotTradeFilter{" +
                "fromDate=" + fromDate +
                ", toDate=" + toDate +
                ", ShopId=" + ShopId +
                '}';
    }
}
