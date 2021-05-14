package vn.viettel.report.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeTransFilter {
    private String transCode;
    private Date fromDate;
    private Date toDate;
    private String reason;
    private String productKW;
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
