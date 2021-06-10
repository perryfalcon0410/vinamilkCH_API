package vn.viettel.sale.messaging;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.validation.annotation.NotEmpty;
import vn.viettel.core.validation.annotation.NotNull;
import vn.viettel.sale.service.dto.SalePromotionDTO;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ApiModel(description = "Thông tin voucher được sử dụng trong đơn hàng")
public class OrderVoucherRequest {

    @ApiModelProperty(notes = "Tiền theo mã giảm giá")
    private Double voucherAmount;

    @ApiModelProperty(notes = "Id voucher")
    private Long voucherId;

    @ApiModelProperty(notes = "Mã voucher")
    private String voucherCode;

}
