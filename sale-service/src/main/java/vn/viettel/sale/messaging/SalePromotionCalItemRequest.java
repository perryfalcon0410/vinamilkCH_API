package vn.viettel.sale.messaging;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.sale.service.dto.SalePromotionDiscountDTO;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Danh sách các khuyến mãi cho đơn hàng")
public class SalePromotionCalItemRequest {
    
    @ApiModelProperty(notes = "Loại khuyến mãi: 0 - KM tự động, 1 - KM tay")
    private Integer promotionType = 0;

    @ApiModelProperty(notes = "Id chương trình khuyến mãi")
    private Long programId;

    @ApiModelProperty(notes = "Khuyến mãi tiền hoặc phần trăm")
    SalePromotionDiscountDTO amount;
}
