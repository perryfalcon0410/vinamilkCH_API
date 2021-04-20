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
public class PromotionCustATTRDTO extends BaseDTO {
    private Long promotionProgramId;
    private Integer objectType;
    private Long objectId;
    private String fromValue;
    private String toValue;
    private Integer status;
}
