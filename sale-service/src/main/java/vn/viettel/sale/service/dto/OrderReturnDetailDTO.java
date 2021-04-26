package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderReturnDetailDTO {
    private InfosReturnDetailDTO infos;
    private List<ProductReturnDTO> productReturn;
    private List<PromotionReturnDTO> promotionReturn;
}
