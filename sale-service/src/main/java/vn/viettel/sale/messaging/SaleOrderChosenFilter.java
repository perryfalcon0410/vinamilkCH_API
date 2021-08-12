package vn.viettel.sale.messaging;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SaleOrderChosenFilter {
    @ApiModelProperty(value = "Số hóa đơn")
    private String orderNumber;
    @ApiModelProperty(value = "Từ khóa")
    private String searchKeyword;
    @ApiModelProperty(value = "Tìm sản phẩm")
    private String product;
    @ApiModelProperty(value = "Từ ngày")
    private LocalDateTime fromDate;
    @ApiModelProperty(value = "Đến ngày")
    private LocalDateTime toDate;
}
