package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.sale.messaging.ExchangeTransDetailRequest;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ExchangeTransDTO {
    private Long id;
    private String transCode;
    private Date transDate;
    private Long shopId;
    private Long customerId;
    private String customerName;
    private String customerAddress;
    private String customerPhone;
    private Long reasonId;
    private String reason;
    private Integer quantity;
    private Float totalAmount;
    private List<ExchangeTransDetailRequest> listProducts;
}
