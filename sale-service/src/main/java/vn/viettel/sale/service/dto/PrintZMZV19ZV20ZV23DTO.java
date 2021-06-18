package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ApiModel(description = "Thông khuyến mãi zv19, zv20, zv21, zv23")
public class PrintZMZV19ZV20ZV23DTO {
    @ApiModelProperty(notes = "Tên sản phẩm")
    private String promotionName;

    @ApiModelProperty(notes = "Mã sản phẩm")
    private String promotionCode;

    @ApiModelProperty(notes = "Số lượng")
    private Double amount;
}