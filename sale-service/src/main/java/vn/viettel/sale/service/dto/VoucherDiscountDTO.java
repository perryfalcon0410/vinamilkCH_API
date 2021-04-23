package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VoucherDiscountDTO {
    private String voucherName;
    private String voucherType;
    private float voucherDiscount;
}
