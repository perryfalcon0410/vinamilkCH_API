package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockCountingUpdateDTO {
    @ApiModelProperty(notes = "Id sản phẩm")
    private Long productId;
    @ApiModelProperty(notes = "Số lượng package kiểm kê")
    private Integer packetQuantity;
    @ApiModelProperty(notes = "Số lượng lẻ kiểm kê")
    private Integer unitQuantity;
    @ApiModelProperty(notes = "Quy đổi")
    private Integer convfact;
}
