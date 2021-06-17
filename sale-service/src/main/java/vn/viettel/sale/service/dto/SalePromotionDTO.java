package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Danh sách các khuyến mãi cho đơn hàng")
public class SalePromotionDTO {
    
    @ApiModelProperty(notes = "Loại khuyến mãi: 0 - KM tự động, 1 - KM tay")
    private Integer promotionType = 0;

    @ApiModelProperty(notes = "Xác định có dược hưởng khuyến mãi này hay không")
    private Boolean isUse;

    @ApiModelProperty(notes = "Id chương trình khuyến mãi")
    private Long programId;

    @ApiModelProperty(notes = "Tên chương trình")
    private String promotionProgramName;

    @ApiModelProperty(notes = "Mã chương trình")
    private String promotionProgramCode;

    @ApiModelProperty(notes = "Loại chương trình: zv01 zv02...")
    private String programType;

    @ApiModelProperty(notes = "Khuyến mãi (KM) có được chỉnh sửa số lượng. KM tay có được thêm sản phẩm (tất cả KM tay đều được nhập số lượng)")
    private Boolean isEditable;

    @ApiModelProperty(notes = "Loại rằng buộc số lượng khuyến mãi")
    private Integer contraintType;

    @ApiModelProperty(notes = "Danh sách sản phẩm khuyến mãi")
    List<FreeProductDTO> products;

    @ApiModelProperty(notes = "Giới hạn số suất")
    private Double numberLimited;

    @ApiModelProperty(notes = "Mhuyến mãi tiền hoặc phần trăm")
    SalePromotionDiscountDTO amount;

    @ApiModelProperty(notes = "Tổng số lượng khuyến mãi")
    private Integer totalQty;

    @ApiModelProperty(notes = "Tổng số tiền khuyến mãi gồm thuê")
    private Double totalAmtInTax;

    @ApiModelProperty(notes = "Tổng số tiền khuyến mãi chưa gồm thuế")
    private Double totalAmtExTax;

    @ApiModelProperty(notes = "Đơn hàng có được trả hàng không")
    private Boolean isReturn;

    @ApiModelProperty(notes = "Tiền tích lũy cho zv23")
    private Double zv23Amount;
}
