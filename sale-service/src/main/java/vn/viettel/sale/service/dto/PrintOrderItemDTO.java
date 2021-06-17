package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ApiModel(description = "Thông tin in hóa đơn bán hàng chi tiết sản phẩm")
public class PrintOrderItemDTO {
    private Long productId;

    @ApiModelProperty(notes = "Tên sản phẩm")
    private String productName;

    @ApiModelProperty(notes = "Mã sản phẩm")
    private String productCode;

    @ApiModelProperty(notes = "Đơn giá")
    private Double price;

    @ApiModelProperty(notes = "Số lượng")
    private Integer quantity;

    @ApiModelProperty(notes = "Tổng tiền")
    private Double totalPrice;

    @ApiModelProperty(notes = "Đơn giá giảm")
    private Double discountPrice;

    @ApiModelProperty(notes = "Tổng tiền giảm")
    private Double totalDiscountPrice;
}
