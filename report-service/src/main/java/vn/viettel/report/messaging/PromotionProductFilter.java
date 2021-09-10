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
@AllArgsConstructor
@NoArgsConstructor
public class PromotionProductFilter {
    private Long shopId;
    private String orderNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime fromDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime toDate;
    private String productCodes;

    @Override
    public String toString() {
        return "PromotionProductFilter{" +
                "shopId=" + shopId +
                ", orderNumber='" + orderNumber + '\'' +
                ", fromDate=" + fromDate +
                ", toDate=" + toDate +
                ", productCodes='" + productCodes + '\'' +
                '}';
    }
}
