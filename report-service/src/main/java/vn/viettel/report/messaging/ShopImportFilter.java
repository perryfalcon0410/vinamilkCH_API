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
public class ShopImportFilter {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime fromDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime toDate;
    private String productCodes;
    private String importType;
    private String internalNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime fromOrderDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime toOrderDate;
    private Long shopId;

    @Override
    public String toString() {
        return "ShopImportFilter{" +
                "fromDate=" + fromDate +
                ", toDate=" + toDate +
                ", productCodes='" + productCodes + '\'' +
                ", importType='" + importType + '\'' +
                ", internalNumber='" + internalNumber + '\'' +
                ", fromOrderDate=" + fromOrderDate +
                ", toOrderDate=" + toOrderDate +
                ", shopId=" + shopId +
                '}';
    }
}
