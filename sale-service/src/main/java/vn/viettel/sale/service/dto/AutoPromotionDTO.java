package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.dto.promotion.PromotionProgramDetailDTO;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AutoPromotionDTO {
    private Float discountAmount;
    private List<ZmFreeItemDTO> freeItems = new ArrayList<>();
    private List<PromotionProgramDetailDTO> listPromotion = new ArrayList<>();
}
