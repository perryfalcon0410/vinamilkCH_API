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
public class EntryMenuDetailsReportsRequest {
    private Long shopId;
    private Date fromDate;
    private Date toDate;

//    public Date getFromDate() {
//        if(fromDate == null) return new Date();
//        return fromDate;
//    }
//
//    public Date getToDate() {
//        if(toDate == null) return new Date();
//        return toDate;
//    }
}
