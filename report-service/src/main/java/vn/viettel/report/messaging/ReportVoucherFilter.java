package vn.viettel.report.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportVoucherFilter {
    private Date fromProgramDate;
    private Date toProgramDate;
    private Date fromUseDate;
    private Date toUseDate;
    private String voucherProgramName;
    private String voucherKeywords;
    private String customerKeywords;
    private String customerMobiPhone;
    private Long shopId;
}
