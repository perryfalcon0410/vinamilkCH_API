package vn.viettel.report.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaleCategoryFilter {
    private String customerKW;
    private String customerPhone;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Long customerType;
    private Long shopId;

    public LocalDate getFromDate() {
        if(fromDate == null) return LocalDate.now();
        return fromDate;
    }

    public LocalDate getToDate() {
        if(toDate == null) return LocalDate.now();
        return toDate;
    }
}
