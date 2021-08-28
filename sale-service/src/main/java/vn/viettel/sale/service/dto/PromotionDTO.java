package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ApiModel(description = "Thông tin khuyến mãi")
public class PromotionDTO {

    @ApiModelProperty(notes = "Mã sản phẩm")
    private String productNumber;

    @ApiModelProperty(notes = "Tên sản phẩm")
    private String productName;

    @ApiModelProperty(notes = "Số lượng")
    private int quantity;

    @ApiModelProperty(notes = "Tên chương trình khuyến mãi")
    private String promotionProgramName;
}
