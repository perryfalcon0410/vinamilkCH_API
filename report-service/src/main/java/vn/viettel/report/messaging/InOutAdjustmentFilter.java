package vn.viettel.report.messaging;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InOutAdjustmentFilter {
        private LocalDateTime fromDate;
        private LocalDateTime toDate;
        private String productCodes;
        private  Long shopId;
}
