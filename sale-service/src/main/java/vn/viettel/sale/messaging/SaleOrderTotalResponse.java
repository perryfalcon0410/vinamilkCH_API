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
public class SaleOrderTotalResponse {
    @ApiModelProperty(notes = "Tổng thành tiền")
    private Double totalAmount = 0D;
    @ApiModelProperty(notes = "Tổng tiền phải trả")
    private Double allTotal = 0D;

    public SaleOrderTotalResponse addTotalAmount(Double totalAmount) {
        this.totalAmount += totalAmount;
        return this;
    }
    public SaleOrderTotalResponse addAllTotal(Double allTotal) {
        this.allTotal += allTotal;
        return this;
    }
}
