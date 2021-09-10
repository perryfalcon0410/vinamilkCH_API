package vn.viettel.report.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangePriceFilter {
   private String searchKey;
   private Long shopId;
    private LocalDateTime fromTransDate;
    private LocalDateTime toTransDate;
    private LocalDateTime fromOrderDate;
    private LocalDateTime toOrderDate;
    private String ids;

    @Override
    public String toString() {
        return "ChangePriceFilter{" +
                "searchKey='" + searchKey + '\'' +
                ", shopId=" + shopId +
                ", fromTransDate=" + fromTransDate +
                ", toTransDate=" + toTransDate +
                ", fromOrderDate=" + fromOrderDate +
                ", toOrderDate=" + toOrderDate +
                ", ids='" + ids + '\'' +
                '}';
    }
}
