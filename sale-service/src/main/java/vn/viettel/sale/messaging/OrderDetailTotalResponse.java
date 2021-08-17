package vn.viettel.sale.messaging;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailTotalResponse {
    @ApiModelProperty(notes = "Tổng số lượng")
    private Integer totalQuantity;
    @ApiModelProperty(notes = "Tổng tiền")
    private Double totalAmount;
    @ApiModelProperty(notes = "Tổng tiền giảm giá")
    private Double totalDiscount;
    @ApiModelProperty(notes = "Tổng tiền phải trả")
    private Double totalPayment;
}
