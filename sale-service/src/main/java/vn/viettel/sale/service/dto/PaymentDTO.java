package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaymentDTO{
    private String currency;
    private int needPayment;
    private int paid;
    private float change;
    private String createdBy;
}
