package vn.viettel.report.messaging;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InOutAdjustmentFilter {
        private Date fromDate;
        private Date toDate;
        private String productCodes;
}
