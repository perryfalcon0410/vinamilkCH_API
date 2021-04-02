package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VoucherDTO {
    private String voucherName;
    private String voucherCode;
    private float voucherPrice;
}
