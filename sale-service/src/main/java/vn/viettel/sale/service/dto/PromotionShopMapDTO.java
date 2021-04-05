package vn.viettel.sale.service.dto;

import lombok.*;
import vn.viettel.core.db.entity.promotion.PromotionShopMap;

@Getter
@Setter
@AllArgsConstructor
public class PromotionShopMapDTO {
    private PromotionShopMap promotionShopMap;
    private Float amount;
    private Integer quantity;
}
