package vn.viettel.core.messaging;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.validation.annotation.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Api(value = "Thông tin cập nhật bảng tổng hợp doanh số theo thẻ thành viên khách hàng của bán hàng")
public class RptCusMemAmountRequest extends BaseRequest{

    @ApiModelProperty(notes = "Tổng doanh số tích lũy")
    @NotNull(responseMessage = ResponseMessage.RPT_CUST_MEM_AMOUNT_AMOUNT_MUST_BE_NOT_NULL)
    private Double amount;
}
