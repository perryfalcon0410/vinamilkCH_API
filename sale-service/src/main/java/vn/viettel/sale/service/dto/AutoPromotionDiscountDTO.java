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
@ApiModel(description = "Khuyến mãi tiền")
public class AutoPromotionDiscountDTO {
    @ApiModelProperty(notes = "Tổng số tiền được khuyến mãi")
    private Double price;

    public void addPrice(double price) {
        if(this.price == null) this.price = 0.0;
        this.price += price;
    }
}
