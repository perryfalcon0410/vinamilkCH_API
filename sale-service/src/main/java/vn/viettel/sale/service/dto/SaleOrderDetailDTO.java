package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(notes = "Danh sách chi tiết đơn bán hàng")
    private CoverResponse<List<OrderDetailDTO>, OrderDetailTotalResponse> orderDetail;
    @ApiModelProperty(notes = "Giảm giá")
    private List<DiscountDTO> discount;
    @ApiModelProperty(notes = "Khuyến mãi")
    private List<PromotionDTO> promotion;
    @ApiModelProperty(notes = "Thông tin đơn hàng")
    private InfosOrderDetailDTO infos;
}
