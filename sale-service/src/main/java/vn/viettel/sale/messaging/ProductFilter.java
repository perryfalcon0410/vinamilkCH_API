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
public class ProductFilter {
    @ApiModelProperty(notes = "Id cửa hàng")
    private Long shopId;
    @ApiModelProperty(notes = "Từ khóa")
    private String keyWord;
    @ApiModelProperty(notes = "Id khách hàng")
    private Long customerId;
    @ApiModelProperty(notes = "Id sản phẩm")
    private Long productInfoId;
    @ApiModelProperty(notes = "Tình trạng")
    private Integer status;
}
