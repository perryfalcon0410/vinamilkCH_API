package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Danh sách sản phẩm khuyến mãi")
public class FreeProductDTO {
    @ApiModelProperty(notes = "Id sản phẩm")
    private Long productId;

    @ApiModelProperty(notes = "Tên sản phẩm")
    private String productName;

    @ApiModelProperty(notes = "Mã sản phẩm")
    private String productCode;

    @ApiModelProperty(notes = "Số lượng")
    private Integer quantity = 0;

    @ApiModelProperty(notes = "Số lượng tồn kho")
    private Integer stockQuantity = 0;

    @ApiModelProperty(notes = "Số lượng khuyến mãi tối đa")
    private Integer quantityMax;

    public Integer getQuantityMax(){
        if (quantityMax == null)
            quantityMax = quantity;

        return  quantityMax;
    }
}
