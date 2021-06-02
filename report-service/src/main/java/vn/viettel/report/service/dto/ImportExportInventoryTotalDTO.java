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
    private Long beginningQty;

    @ApiModelProperty(notes = "Thành tiền đầu kỳ")
    private Double beginningAmount;

    @ApiModelProperty(notes = "Tổng số lượng nhập trong kỳ")
    private Long impTotalQty;

    @ApiModelProperty(notes = "Số lượng nhập mua hàng")
    private Long impQty;

    @ApiModelProperty(notes = "Tiền nhập mua hàng")
    private Double impAmount;

    @ApiModelProperty(notes = "Số lượng nhập điều chỉnh")
    private Long impAdjustmentQty;

    @ApiModelProperty(notes = "Tiền nhập điều chỉnh")
    private Double impAdjustmentAmount;

    @ApiModelProperty(notes = "Tổng số lượng xuất trong kỳ")
    private Long expTotalQty;

    @ApiModelProperty(notes = "Số lượng xuất bán hàng")
    private Long expSalesQty;

    @ApiModelProperty(notes = "Thành tiền xuất bán hàng")
    private Double expSalesAmount;

    @ApiModelProperty(notes = "Khuyến mãi bán hàng")
    private Long expPromotionQty;

    @ApiModelProperty(notes = "Thành tiền khuyến mãi bán hàng")
    private Double expPromotionAmount;

    @ApiModelProperty(notes = "Số lượng xuất điều chỉnh")
    private Long expAdjustmentQty;

    @ApiModelProperty(notes = "Thành tiền xuất điều chỉnh")
    private Double expAdjustmentAmount;

    @ApiModelProperty(notes = "Số lượng xuất trả hàng")
    private Long expExchangeQty;

    @ApiModelProperty(notes = "Thành tiền xuất trả hàng")
    private Double expExchangeAmount;

    @ApiModelProperty(notes = "Tồn cuối kỳ")
    private Long endingQty;

    @ApiModelProperty(notes = "Thành tiền cuối kỳ")
    private Double endingAmount;
}
