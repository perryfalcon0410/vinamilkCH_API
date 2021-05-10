package vn.viettel.core.dto.promotion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PromotionSaleProductDTO extends BaseDTO {
    private Long promotionProgramId;
    private Long productId;
    private Integer quantity;
    private Integer status;
}
