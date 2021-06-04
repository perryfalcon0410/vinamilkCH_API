package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

//    public void addPrice(double price) {
//        if(this.price == null) this.price = 0.0;
//        this.price += price;
//    }
}
