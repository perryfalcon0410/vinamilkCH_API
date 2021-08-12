package vn.viettel.sale.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PriceType {
    NOT_VAT(0), VAT(1);

    int value;
}
