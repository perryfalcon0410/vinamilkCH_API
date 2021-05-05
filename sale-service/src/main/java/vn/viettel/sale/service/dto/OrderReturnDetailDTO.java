package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.dto.common.ApParamDTO;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderReturnDetailDTO {
    private InfosReturnDetailDTO infos;
    private List<ReasonReturnDTO> reasonReturn;
    private List<ProductReturnDTO> productReturn;
    private List<PromotionReturnDTO> promotionReturn;
}
