package vn.viettel.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PromotionCustObjectType {
    CUSTOMER_TYPE(2), MEMBER_CARD(4), LOYAL_CUSTOMER(5), CUSTOMER_CARD_TYPE(6), ORDER_TYPE(20);

    Integer value;
}
