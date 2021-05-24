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
    @ApiModelProperty(notes = "Mã kho kiểm kê")
    private Long stockCountingId;
    @ApiModelProperty(notes = "Ngành hàng")
    private String productCategory;
    @ApiModelProperty(notes = "Nhóm hàng")
    private String productGroup;
    @ApiModelProperty(notes = "Mã loại kho kiểm kê")
    private Long warehouseTypeId;
    @ApiModelProperty(notes = "ID cửa hàng")
    private Long shopId;
    @ApiModelProperty(notes = "ID sản phẩm")
    private Long productId;
    @ApiModelProperty(notes = "Mã sản phẩm")
    private String productCode;
    @ApiModelProperty(notes = "Tên sản phẩm")
    private String productName;
    @ApiModelProperty(notes = "Số lượng")
    private Integer quantity;
    @ApiModelProperty(notes = "Đơn giá")
    private Float price;
    @ApiModelProperty(notes = "Số lượng tồn kho")
    private Integer stockQuantity;
    @ApiModelProperty(notes = "Số lượng kiểm kê")
    private Integer inventoryQuantity;
    @ApiModelProperty(notes = "Chênh lệch")
    private Integer changeQuantity;
    @ApiModelProperty(notes = "Thành tiền")
    private Float totalAmount;
    @ApiModelProperty(notes = "Quy đổi")
    private Integer convfact;
    @ApiModelProperty(notes = "Đơn vị packet")
    private String packetUnit;
    @ApiModelProperty(notes = "Đơn vị lẻ")
    private String unit;
    @ApiModelProperty(notes = "Số lượng packet")
    private Integer packetQuantity;
    @ApiModelProperty(notes = "Số lượng lẻ")
    private Integer unitQuantity;
}
