package vn.viettel.report.messaging;

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
@ApiModel(description = "Tổng hàng khuyến mãi")
public class ExchangeTransTotal {
    @ApiModelProperty(notes = "Tổng số lượng")
    private Integer totalQuantity;
    @ApiModelProperty(notes = "Tổng thành tiền")
    private Float totalAmount;
}
