package vn.viettel.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PromotionProductOpenDTO extends BaseDTO {
    private Long promotionProgramId;
    private Long productId;
    private Float quantity;
    private Float amount;
    private Integer status;
}
