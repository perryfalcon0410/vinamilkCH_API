package vn.viettel.sale.messaging;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ApiModel(description = "Thông tin voucher được sử dụng trong đơn hàng")
public class OrderVoucherRequest {

    @ApiModelProperty(notes = "Tiền theo mã giảm giá")
    private Double price;

    @ApiModelProperty(notes = "Id voucher")
    private Long id;

    @ApiModelProperty(notes = "Mã voucher")
    private String voucherCode;

}
