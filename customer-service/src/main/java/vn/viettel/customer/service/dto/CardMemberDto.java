package vn.viettel.customer.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.enums.customer.CardMemberType;
import vn.viettel.core.service.dto.BaseDTO;

@Getter
@Setter
@NoArgsConstructor
public class CardMemberDto extends BaseDTO {
    private long customerId;
    private int cardType;
    private long groupId;
}
