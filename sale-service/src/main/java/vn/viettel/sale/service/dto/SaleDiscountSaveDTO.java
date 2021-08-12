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
public class SaleDiscountSaveDTO {
    @ApiModelProperty(notes = "Số tiền được khuyến mãi")
    private Double amount = 0.0;

    @ApiModelProperty(notes = "Số tiền tối đa được khuyến mãi")
    private Double maxAmount = 0.0;

    @ApiModelProperty(notes = "Mức hưởng khuyến mãi")
    private Integer levelNumber;

    @ApiModelProperty(notes = "Id sản phẩm mua")
    private Long productId;

    @ApiModelProperty(notes = "Tiền khuyến mãi gồm thuế")
    private Double amountInTax;

    @ApiModelProperty(notes = "Tiền khuyến mãi chưa gồm thuế")
    private Double amountExTax;

    public Double getAmount() {
        if(amount == null) amount = 0.0;
        return (double)Math.round(amount);
    }

    public Double getAmountInTax() {
        if(amountInTax == null) amountInTax = 0.0;
        return (double)Math.round(amountInTax);
    }

    public Double getAmountExTax() {
        if(amountExTax == null) amountExTax = 0.0;
        return (double)Math.round(amountExTax);
    }
}
