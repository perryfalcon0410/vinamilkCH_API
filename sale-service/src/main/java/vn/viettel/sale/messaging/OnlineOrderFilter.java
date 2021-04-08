package vn.viettel.sale.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OnlineOrderFilter {
    private String orderNumber;
    private Long shopId;
    private Integer synStatus;
    private Date fromDate;
    private Date toDate;
}
