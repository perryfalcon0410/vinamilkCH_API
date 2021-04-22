package vn.viettel.sale.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ComboProductTranFilter {

    private Long shopId;

    private String transCode;

    private Integer transType;

    private Date fromDate;

    private Date toDate;

}