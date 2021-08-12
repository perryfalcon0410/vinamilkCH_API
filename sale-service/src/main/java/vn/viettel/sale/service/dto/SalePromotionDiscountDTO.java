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
@ApiModel(description = "Khuyến mãi tiền hoặc phần trăm giảm giá")
public class SalePromotionDiscountDTO {
    @ApiModelProperty(notes = "Số tiền được khuyến mãi")
    private Double amount = 0.0;

    @ApiModelProperty(notes = "Số tiền tối đa được khuyến mãi")
    private Double maxAmount = 0.0;

    @ApiModelProperty(notes = "Phần trăm giảm giá")
    private Double percentage = 0.0;

    @ApiModelProperty(notes = "Thông tin chi tiết km")
    List<SaleDiscountSaveDTO> discountInfo;

    public Double getAmount() {
        if(amount == null) amount = 0.0;
        return (double) Math.round(amount);
    }

    public Double getMaxAmount() {
        if(maxAmount == null) maxAmount = 0.0;
        return (double) Math.round(maxAmount);
    }
}
