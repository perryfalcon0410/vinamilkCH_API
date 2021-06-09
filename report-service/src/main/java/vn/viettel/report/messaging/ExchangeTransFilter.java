package vn.viettel.report.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeTransFilter {
    private String transCode;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String reason;
    private String productKW;
    private Long shopId;
}
