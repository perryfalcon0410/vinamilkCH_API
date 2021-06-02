package vn.viettel.sale.messaging;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.util.Constants;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Api(tags = "Thông tin tìm kiếm")
public class SaleOrderFilter {
    @ApiModelProperty(value = "Từ khóa")
    private String searchKeyword;
    @ApiModelProperty(value = "Số hóa đơn")
    private String orderNumber;
    @ApiModelProperty(value = "Có sử dụng hóa đơn đỏ")
    private Integer usedRedInvoice;
    @ApiModelProperty(value = "Từ ngày")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime fromDate;
    @ApiModelProperty(value = "Đến ngày")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime toDate;
}
