package vn.viettel.report.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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
