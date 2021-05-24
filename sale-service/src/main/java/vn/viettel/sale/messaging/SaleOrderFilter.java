package vn.viettel.sale.messaging;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Api(tags = "Thông tin tìm kiếm")
public class SaleOrderFilter {
    @ApiModelProperty(value = "Từ khóa")
    private String searchKeyword;
    private String orderNumber;
    private Integer usedRedInvoice;
    private Date fromDate;
    private Date toDate;
}
