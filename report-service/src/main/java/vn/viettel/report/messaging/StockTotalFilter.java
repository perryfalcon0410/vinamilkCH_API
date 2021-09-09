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
public class StockTotalFilter {
   private Date stockDate;
   private String productCodes;
   private Long shopId;
   private Long warehouseTypeId;

    @Override
    public String toString() {
        return "StockTotalFilter{" +
                "stockDate=" + stockDate +
                ", productCodes='" + productCodes + '\'' +
                ", shopId=" + shopId +
                ", warehouseTypeId=" + warehouseTypeId +
                '}';
    }
}
