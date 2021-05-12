package vn.viettel.report.service.dto;

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
@ApiModel(description = "Tổng số lượng và giá trong xuất nhập tồn")
public class ImportExportInventoryTotalDTO {
    @ApiModelProperty(notes = "Tồn đầu kỳ")
    private Integer beginningQuantity;

    @ApiModelProperty(notes = "Thành tiền đầu kỳ")
    private Float beginningAmount;

    @ApiModelProperty(notes = "Tổng số lượng nhập trong kỳ")
    private Integer importTotalQuantity;

    @ApiModelProperty(notes = "Số lượng nhập mua hàng")
    private Integer importQuantity;

    @ApiModelProperty(notes = "Tiền nhập mua hàng")
    private Float importAmount;

    @ApiModelProperty(notes = "Số lượng nhập điều chỉnh")
    private Integer importAdjustmentQuantity;

    @ApiModelProperty(notes = "Tiền nhập điều chỉnh")
    private Float importAdjustmentAmount;

    @ApiModelProperty(notes = "Tổng số lượng xuất trong kỳ")
    private Integer exportTotalQuantity;

    @ApiModelProperty(notes = "Số lượng xuất bán hàng")
    private Integer exportSalesQuantity;

    @ApiModelProperty(notes = "Thành tiền xuất bán hàng")
    private Float exportSalesAmount;

    @ApiModelProperty(notes = "Khuyến mãi bán hàng")
    private Integer exportPromotionQuantity;

    @ApiModelProperty(notes = "Thành tiền khuyến mãi bán hàng")
    private Float exportPromotionAmount;

    @ApiModelProperty(notes = "Số lượng xuất điều chỉnh")
    private Integer exportAdjustmentQuantity;

    @ApiModelProperty(notes = "Thành tiền xuất điều chỉnh")
    private Float exportAdjustmentAmount;

    @ApiModelProperty(notes = "Số lượng xuất trả hàng")
    private Integer exportExchangeQuantity;

    @ApiModelProperty(notes = "Thành tiền xuất trả hàng")
    private Float exportExchangeAmount;

    @ApiModelProperty(notes = "Tồn cuối kỳ")
    private Integer endingQuantity;

    @ApiModelProperty(notes = "Thành tiền cuối kỳ")
    private Float endingAmount;
}
