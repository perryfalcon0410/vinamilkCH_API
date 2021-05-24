package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TotalStockCounting {
    @ApiModelProperty(notes = "Tổng SL kiểm kê")
    private Integer inventoryTotal;
    @ApiModelProperty(notes = "SL tồn kho")
    private Integer stockTotal;
    @ApiModelProperty(notes = "Tổng tiền")
    private Float totalAmount;
    @ApiModelProperty(notes = "Tổng SL packet")
    private Integer totalPacket;
    @ApiModelProperty(notes = "Tổng SL lẻ")
    private Integer totalUnit;
    @ApiModelProperty(notes = "SL chênh lệch")
    private Integer changeQuantity;
}
