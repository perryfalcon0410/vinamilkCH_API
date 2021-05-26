package vn.viettel.sale.messaging;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TotalOrderReturnDetail {
    @ApiModelProperty(notes = "Tổng số lượng")
    private Integer totalQuantity = 0;
    @ApiModelProperty(notes = "Tổng thành tiền")
    private Float totalAmount = 0F;
    @ApiModelProperty(notes = "Tổng giảm giá")
    private Float totalDiscount = 0F;
    @ApiModelProperty(notes = "Tổng thanh toán")
    private Float allTotal = 0F;

    public TotalOrderReturnDetail addTotalQuantity(Integer totalQuantity) {
        this.totalQuantity += totalQuantity;
        return this;
    }

    public TotalOrderReturnDetail addTotalAmount(Float totalAmount) {
        this.totalAmount += totalAmount;
        return this;
    }

    public TotalOrderReturnDetail addTotalDiscount(Float totalDiscount) {
        this.totalDiscount += totalDiscount;
        return this;
    }

    public TotalOrderReturnDetail addAllTotal(Float allTotal) {
        this.allTotal += allTotal;
        return this;
    }
}
