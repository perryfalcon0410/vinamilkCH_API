package vn.viettel.promotion.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

@Getter
@Setter
@NoArgsConstructor
public class PromotionItemProductDTO extends BaseDTO {
    private Long promotionItemGroupId;
    private Long productId;
    private Integer status;
}
