package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ExchangeTransDTO {
    private Long id;
    private String transCode;
    private Date transDate;
    private Long shopId;
    private Long customerId;
    private Long reasonId;
    private String reason;
    private Integer quantity;
    private Float totalAmount;
}
