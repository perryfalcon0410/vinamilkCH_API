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
public class ShopExportFilter {
    private LocalDateTime fromDate;
    private LocalDateTime toDate;

    private String productCodes;
    private String importType;
    private String searchKeywords;

    private LocalDateTime fromOrderDate;
    private LocalDateTime toOrderDate;
    private Long shopId;

    @Override
    public String toString() {
        return "ShopExportFilter{" +
                "fromDate=" + fromDate +
                ", toDate=" + toDate +
                ", productCodes='" + productCodes + '\'' +
                ", importType='" + importType + '\'' +
                ", searchKeywords='" + searchKeywords + '\'' +
                ", fromOrderDate=" + fromOrderDate +
                ", toOrderDate=" + toOrderDate +
                ", shopId=" + shopId +
                '}';
    }
}
