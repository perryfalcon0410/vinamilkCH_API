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
    private Integer beginningQty;

    @ApiModelProperty(notes = "Thành tiền đầu kỳ")
    private Float beginningAmount;

    @ApiModelProperty(notes = "Tổng số lượng nhập trong kỳ")
    private Integer impTotalQty;

    @ApiModelProperty(notes = "Số lượng nhập mua hàng")
    private Integer impQty;

    @ApiModelProperty(notes = "Tiền nhập mua hàng")
    private Float impAmount;

    @ApiModelProperty(notes = "Số lượng nhập điều chỉnh")
    private Integer impAdjustmentQty;

    @ApiModelProperty(notes = "Tiền nhập điều chỉnh")
    private Float impAdjustmentAmount;

    @ApiModelProperty(notes = "Tổng số lượng xuất trong kỳ")
    private Integer expTotalQty;

    @ApiModelProperty(notes = "Số lượng xuất bán hàng")
    private Integer expSalesQty;

    @ApiModelProperty(notes = "Thành tiền xuất bán hàng")
    private Float expSalesAmount;

    @ApiModelProperty(notes = "Khuyến mãi bán hàng")
    private Integer expPromotionQty;

    @ApiModelProperty(notes = "Thành tiền khuyến mãi bán hàng")
    private Float expPromotionAmount;

    @ApiModelProperty(notes = "Số lượng xuất điều chỉnh")
    private Integer expAdjustmentQty;

    @ApiModelProperty(notes = "Thành tiền xuất điều chỉnh")
    private Float expAdjustmentAmount;

    @ApiModelProperty(notes = "Số lượng xuất trả hàng")
    private Integer expExchangeQty;

    @ApiModelProperty(notes = "Thành tiền xuất trả hàng")
    private Float expExchangeAmount;

    @ApiModelProperty(notes = "Tồn cuối kỳ")
    private Integer endingQty;

    @ApiModelProperty(notes = "Thành tiền cuối kỳ")
    private Float endingAmount;
}
