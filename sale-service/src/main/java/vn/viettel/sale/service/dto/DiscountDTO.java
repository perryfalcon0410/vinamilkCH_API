package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.dto.voucher.VoucherDTO;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DiscountDTO {
    private String promotionName;
    private String promotionType;
    private String voucherType;
    private float discountPrice;
    private float discountPercent;
}
