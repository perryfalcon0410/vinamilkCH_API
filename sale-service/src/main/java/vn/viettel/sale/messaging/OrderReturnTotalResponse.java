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
public class OrderReturnTotalResponse {
    @ApiModelProperty(notes = "Tổng tiền")
    private float totalAmount;
    @ApiModelProperty(notes = "Tổng tiền phải trả")
    private float totalPayment;
}
