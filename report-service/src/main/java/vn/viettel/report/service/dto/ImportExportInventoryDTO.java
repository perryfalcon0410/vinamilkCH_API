package vn.viettel.report.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ApiModel(description = "Thông tin chi tiết xuất nhập tồn")
public class ImportExportInventoryDTO {
    @Id
    @Column(name = "ID")
    private Long id;
    @ApiModelProperty(notes = "Id ngành hàng")
    @Column(name = "CAT_ID")
    private Long catId;
    @ApiModelProperty(notes = "Tên ngành hàng")
    @Column(name = "CAT_NAME")
    private String catName;
    @ApiModelProperty(notes = "Mã sản phẩm")
    @Column(name = "PRODUCT_CODE")
    private String productCode;
    @ApiModelProperty(notes = "Tên sản phẩm")
    @Column(name = "PRODUCT_NAME")
    private String productName;
    @ApiModelProperty(notes = "Đơn vị tính")
    @Column(name = "UOM")
    private String uom;

    @ApiModelProperty(notes = "Tồn đầu kỳ")
    @Column(name = "BEGINNING_QTY")
    private Long beginningQty;
    @ApiModelProperty(notes = "Giá đầu kỳ")
    @Column(name = "BEGINNING_PRICE")
    private Double beginningPrice;
    @ApiModelProperty(notes = "Thành tiền đầu kỳ")
    @Column(name = "BEGINNING_AMOUNT")
    private Double beginningAmount;

    @ApiModelProperty(notes = "Tổng số lượng nhập trong kỳ")
    @Column(name = "IMP_TOTAL_QTY")
    private Long impTotalQty;
    @ApiModelProperty(notes = "Số lượng nhập mua hàng")
    @Column(name = "IMP_QTY")
    private Long impQty;
    @ApiModelProperty(notes = "Tiền nhập mua hàng")
    @Column(name = "IMP_AMOUNT")
    private Double impAmount;
    @ApiModelProperty(notes = "Số lượng nhập điều chỉnh")
    @Column(name = "IMP_ADJUSTMENT_QTY")
    private Long impAdjustmentQty;
    @ApiModelProperty(notes = "Tiền nhập điều chỉnh")
    @Column(name = "IMP_ADJUSTMENT_AMOUNT")
    private Double impAdjustmentAmount;

    @ApiModelProperty(notes = "Tổng số lượng xuất trong kỳ")
    @Column(name = "EXP_TOTAL_QTY")
    private Long expTotalQty;
    @ApiModelProperty(notes = "Số lượng xuất bán hàng")
    @Column(name = "EXP_SALES_QTY")
    private Long expSalesQty;
    @ApiModelProperty(notes = "Thành tiền xuất bán hàng")
    @Column(name = "EXP_SALES_AMOUNT")
    private Double expSalesAmount;
    @ApiModelProperty(notes = "Khuyến mãi bán hàng")
    @Column(name = "EXP_PROMOTION_QTY")
    private Long expPromotionQty;
    @ApiModelProperty(notes = "Thành tiền khuyến mãi bán hàng")
    @Column(name = "EXP_PROMOTION_AMOUNT")
    private Double expPromotionAmount;
    @ApiModelProperty(notes = "Số lượng xuất điều chỉnh")
    @Column(name = "EXP_ADJUSTMENT_QTY")
    private Long expAdjustmentQty;
    @ApiModelProperty(notes = "Thành tiền xuất điều chỉnh")
    @Column(name = "EXP_ADJUSTMENT_AMOUNT")
    private Double expAdjustmentAmount;;
    @ApiModelProperty(notes = "Số lượng xuất trả hàng")
    @Column(name = "EXP_EXCHANGE_QTY")
    private Long expExchangeQty;
    @ApiModelProperty(notes = "Thành tiền xuất trả hàng")
    @Column(name = "EXP_EXCHANGE_AMOUNT")
    private Double expExchangeAmount;

    @ApiModelProperty(notes = "Tồn cuối kỳ")
    @Column(name = "ENDING_QTY")
    private Long endingQty;
    @ApiModelProperty(notes = "Giá cuối kỳ")
    @Column(name = "ENDING_PRICE")
    private Double endingPrice;
    @ApiModelProperty(notes = "Thành tiền cuối kỳ")
    @Column(name = "ENDING_AMOUNT")
    private Double endingAmount;
}
