package vn.viettel.sale.messaging;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductDataResponse {

    @ApiModelProperty(notes = "Tên sản phẩm")
    private String productName;
    @ApiModelProperty(notes = "Mã sản phẩm")
    private String productCode;
    @ApiModelProperty(notes = "Đơn giá có vat ")
    private Double price;
    @ApiModelProperty(notes = "ĐVT")
    private String uom1;
    @ApiModelProperty(notes = "Số lượng")
    private Integer quantity;
    @ApiModelProperty(notes = "Thành tiền")
    private Double intoMoney;
    @ApiModelProperty(notes = "Ghi chú")
    private String note;


}

