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
public class ReportVoucherFilter {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime fromProgramDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime toProgramDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime fromUseDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime toUseDate;
    private String voucherProgramName;
    private String voucherKeywords;
    private String customerKeywords;
    private String customerMobiPhone;
    private Long shopId;

    @Override
    public String toString() {
        return "ReportVoucherFilter{" +
                "fromProgramDate=" + fromProgramDate +
                ", toProgramDate=" + toProgramDate +
                ", fromUseDate=" + fromUseDate +
                ", toUseDate=" + toUseDate +
                ", voucherProgramName='" + voucherProgramName + '\'' +
                ", voucherKeywords='" + voucherKeywords + '\'' +
                ", customerKeywords='" + customerKeywords + '\'' +
                ", customerMobiPhone='" + customerMobiPhone + '\'' +
                ", shopId=" + shopId +
                '}';
    }
}
