package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ApiModel(description = "Chi tiết giảm giá % hoặc tiền")
public class DiscountDTO {
    @ApiModelProperty(notes = "Tên chương trình Khuyến mãi")
    private String promotionName;

    @ApiModelProperty(notes = "Loại khuyến mãi")
    private String promotionType;

    @ApiModelProperty(notes = "Mã chương trình Khuyến mãi")
    private String voucherType;

    @ApiModelProperty(notes = "Số tiền giảm giá")
    private Double discountPrice;

    @ApiModelProperty(notes = "Phần trăm giảm giá")
    private Double discountPercent;
}
