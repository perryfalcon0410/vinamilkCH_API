package vn.viettel.report.messaging;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ExchangeTransFilter {
    private Date fromDate;
    private Date toDate;
    private Long shopId;

    public Date getFromDate() {
        if(fromDate == null) return new Date();
        return fromDate;
    }

    public Date getToDate() {
        if(toDate == null) return new Date();
        return toDate;
    }
}
