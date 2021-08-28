package vn.viettel.sale.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import vn.viettel.core.dto.promotion.PromotionShopMapDTO;

@Getter
@Setter
@AllArgsConstructor
public class ShopMapDTO {
    private PromotionShopMapDTO promotionShopMap;
    private Float amount;
    private Integer quantity;
}
