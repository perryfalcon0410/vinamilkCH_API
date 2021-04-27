package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.sale.messaging.OrderDetailTotalResponse;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SaleOrderDetailDTO {
    private CoverResponse<List<OrderDetailDTO>, OrderDetailTotalResponse> orderDetail;
    private List<DiscountDTO> discount;
    private List<PromotionDTO> promotion;
    private InfosOrderDetailDTO infos;
}
