package vn.viettel.core.messaging;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Api(value = "Thông tin cập nhật bảng rpt_zv23 của bán hàng")
public class RPT_ZV23Request extends BaseRequest{

    @ApiModelProperty(notes = "Tổng doanh số")
    private Double totalAmount;
}
