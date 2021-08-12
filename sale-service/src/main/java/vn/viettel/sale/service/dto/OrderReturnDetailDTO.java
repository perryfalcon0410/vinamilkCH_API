package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.sale.messaging.TotalOrderReturnDetail;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderReturnDetailDTO {
    @ApiModelProperty(notes = "Thông tin đơn trả hàng")
    private InfosReturnDetailDTO infos;
    @ApiModelProperty(notes = "Lý do trả hàng")
    private List<ReasonReturnDTO> reasonReturn;
    @ApiModelProperty(notes = "Danh sách hàng trả lại")
    private CoverResponse<List<ProductReturnDTO>,TotalOrderReturnDetail> productReturn;
    @ApiModelProperty(notes = "Danh sách khuyến mãi của đơn trả")
    private CoverResponse<List<PromotionReturnDTO>,TotalOrderReturnDetail> promotionReturn;
}
