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
    private Double totalAmount = 0D;
    @ApiModelProperty(notes = "Tổng giảm giá")
    private Double totalDiscount = 0D;
    @ApiModelProperty(notes = "Tổng thanh toán")
    private Double allTotal = 0D;

    public TotalOrderReturnDetail addTotalQuantity(Integer totalQuantity) {
        this.totalQuantity += totalQuantity;
        return this;
    }

    public TotalOrderReturnDetail addTotalAmount(Double totalAmount) {
        this.totalAmount += totalAmount;
        return this;
    }

    public TotalOrderReturnDetail addTotalDiscount(Double totalDiscount) {
        this.totalDiscount += totalDiscount;
        return this;
    }

    public TotalOrderReturnDetail addAllTotal(Double allTotal) {
        this.allTotal += allTotal;
        return this;
    }
}
