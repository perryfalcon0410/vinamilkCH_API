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
public class PromotionProductFilter {
    private Long shopId;
    private String onlineNumber;
    private Date fromDate;
    private Date toDate;
    private String productCodes;
}
