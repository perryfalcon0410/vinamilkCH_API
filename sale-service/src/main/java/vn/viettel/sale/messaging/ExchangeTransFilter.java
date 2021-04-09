package vn.viettel.sale.messaging;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ExchangeTransFilter {
    private String transCode;
    private Date fromDate;
    private Date toDate;
    private Long reasonId;
}
