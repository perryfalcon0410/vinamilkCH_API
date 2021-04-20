package vn.viettel.sale.service.dto;

import lombok.*;
import vn.viettel.core.dto.PromotionShopMapDTO;

@Getter
@Setter
@AllArgsConstructor
public class ShopMapDTO {
    private PromotionShopMapDTO promotionShopMap;
    private Float amount;
    private Integer quantity;
}
