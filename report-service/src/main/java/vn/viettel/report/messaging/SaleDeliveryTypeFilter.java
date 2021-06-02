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
public class SaleDeliveryTypeFilter {
    private LocalDate fromDate;
    private LocalDate toDate;
    private Long shopId;
    private String orderNumber;
    private String apValue;
    private String customerKW;
    private String phoneText;
    private Float fromTotal;
    private Float toTotal;
}
