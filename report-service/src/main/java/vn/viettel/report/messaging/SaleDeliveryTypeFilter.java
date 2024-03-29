package vn.viettel.report.messaging;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.util.Constants;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaleDeliveryTypeFilter {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime fromDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime toDate;
    private Long shopId;
    private String orderNumber;
    private String apValue;
    private String customerKW;
    private String phoneText;
    private Float fromTotal;
    private Float toTotal;

    @Override
    public String toString() {
        return "SaleDeliveryTypeFilter{" +
                "fromDate=" + fromDate +
                ", toDate=" + toDate +
                ", shopId=" + shopId +
                ", orderNumber='" + orderNumber + '\'' +
                ", apValue='" + apValue + '\'' +
                ", customerKW='" + customerKW + '\'' +
                ", phoneText='" + phoneText + '\'' +
                ", fromTotal=" + fromTotal +
                ", toTotal=" + toTotal +
                '}';
    }
}
