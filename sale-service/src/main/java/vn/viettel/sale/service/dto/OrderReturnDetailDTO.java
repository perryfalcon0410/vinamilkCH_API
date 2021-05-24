package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private List<ProductReturnDTO> productReturn;
    @ApiModelProperty(notes = "Danh sách khuyến mãi của đơn trả")
    private List<PromotionReturnDTO> promotionReturn;
}
