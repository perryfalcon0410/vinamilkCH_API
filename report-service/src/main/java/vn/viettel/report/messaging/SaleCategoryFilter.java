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
public class SaleCategoryFilter {
    private String customerKW;
    private String customerPhone;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private Long customerType;
    private Long shopId;

    public LocalDateTime getFromDate() {
        if(fromDate == null) return LocalDateTime.now();
        return fromDate;
    }

    public LocalDateTime getToDate() {
        if(toDate == null) return LocalDateTime.now();
        return toDate;
    }

    @Override
    public String toString() {
        return "SaleCategoryFilter{" +
                "customerKW='" + customerKW + '\'' +
                ", customerPhone='" + customerPhone + '\'' +
                ", fromDate=" + fromDate +
                ", toDate=" + toDate +
                ", customerType=" + customerType +
                ", shopId=" + shopId +
                '}';
    }
}
