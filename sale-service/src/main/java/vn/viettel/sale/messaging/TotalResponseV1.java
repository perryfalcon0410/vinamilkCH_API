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
public class TotalResponseV1 {
    @ApiModelProperty(notes = "Tổng số lượng")
    private Integer totalQuantity = 0;
    @ApiModelProperty(notes = "Số sản phẩm")
    private Integer countProduct = 0;
    @ApiModelProperty(notes = "Tổng giá tiền")
    private Double totalPrice = 0D;
    @ApiModelProperty(notes = "Tổng giá tiền trước thuế")
    private Double totalPriceNotVat = 0D;

}
