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
    @ApiModelProperty(notes = "Tên chương trình khuyến mãi")
    private String promotionName;

    @ApiModelProperty(notes = "Mã chương trình khuyến mãi")
    private String promotionCode;

    @ApiModelProperty(notes = "Tiền khuyến mãi")
    private Double amount;
}
