package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Thông tin khuyến mãi tự động")
public class AutoPromotionDTO {
//    private Boolean isAuto;
    @ApiModelProperty(notes = "Xác định có dược hưởng khuyến mãi này hay không")
    private Boolean isUse;
    @ApiModelProperty(notes = "Id chương trình khuyến mãi")
    private Long programId;
    @ApiModelProperty(notes = "Tên chương trình")
    private String promotionProgramName;

//    @ApiModelProperty(notes = "Id bảng khai báo đơn vị tham gia")
//    private Long promotionShopMapId;

    @ApiModelProperty(notes = "Danh sách sản phẩm khuyến mãi")
    List<FreeProductDTO> freeProducts;

    @ApiModelProperty(notes = "Khuyến mãi tiền")
    private SalePromotionDiscountDTO discount;
}
