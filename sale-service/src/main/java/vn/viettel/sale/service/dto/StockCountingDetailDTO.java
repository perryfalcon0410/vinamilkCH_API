package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockCountingDetailDTO extends BaseDTO {
    @ApiModelProperty(notes = "Id tồn kho")
    private Long stockCountingId;
    @ApiModelProperty(notes = "Ngành hàng")
    private String productCategory;
    @ApiModelProperty(notes = "Id kho hàng")
    private Long warehouseTypeId;
    @ApiModelProperty(notes = "Id cửa hàng")
    private Long shopId;
    @ApiModelProperty(notes = "Id sản phẩm")
    private Long productId;
    @ApiModelProperty(notes = "Mã sản phẩm")
    private String productCode;
    @ApiModelProperty(notes = "Tên sản phẩm")
    private String productName;
    @ApiModelProperty(notes = "Giá sản phẩm")
    private Float price;
    @ApiModelProperty(notes = "Số lượng tồn kho thực tế")
    private Integer stockQuantity;
    @ApiModelProperty(notes = "Số lượng kiểm kê")
    private Integer inventoryQuantity;
    @ApiModelProperty(notes = "Số lượng chênh lệch")
    private Integer changeQuantity;
    @ApiModelProperty(notes = "Tổng tiền")
    private Float totalAmount;
    @ApiModelProperty(notes = "Giá trị quy đổi")
    private Integer convfact;
    @ApiModelProperty(notes = "Đơn vị packet")
    private String packetUnit;
    @ApiModelProperty(notes = "Đơn vị lẻ")
    private String unit;
    @ApiModelProperty(notes = "Số lượng Packet kiểm kê")
    private Integer packetQuantity;
    @ApiModelProperty(notes = "Số lượng lẻ kiểm kê")
    private Integer unitQuantity;
}
