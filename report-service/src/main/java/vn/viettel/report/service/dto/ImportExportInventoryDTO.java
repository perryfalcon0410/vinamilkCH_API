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
    @ApiModelProperty(notes = "Tên ngành hàng")
    @Column(name = "CAT_NAME")
    private String catName;
    @ApiModelProperty(notes = "Mã sản phẩm")
    @Column(name = "PRODUCT_CODE")
    private String productCode;
    @ApiModelProperty(notes = "Tên sản phẩm")
    @Column(name = "PRODUCT_NAME")
    private String productName;
    @ApiModelProperty(notes = "Ngành hàng")
    @Column(name = "UOM")
    private String uom;

    @ApiModelProperty(notes = "Tồn đầu kỳ")
    @Column(name = "BEGINNING_QTY")
    private Integer beginningQty;
    @ApiModelProperty(notes = "Giá đầu kỳ")
    @Column(name = "BEGINNING_PRICE")
    private Float beginningPrice;
    @ApiModelProperty(notes = "Thành tiền đầu kỳ")
    @Column(name = "BEGINNING_AMOUNT")
    private Float beginningAmount;

    @ApiModelProperty(notes = "Tổng số lượng nhập trong kỳ")
    @Column(name = "IMP_TOTAL_QTY")
    private Integer impTotalQty;
    @ApiModelProperty(notes = "Số lượng nhập mua hàng")
    @Column(name = "IMP_QTY")
    private Integer impQty;
    @ApiModelProperty(notes = "Tiền nhập mua hàng")
    @Column(name = "IMP_AMOUNT")
    private Float impAmount;
    @ApiModelProperty(notes = "Số lượng nhập điều chỉnh")
    @Column(name = "IMP_ADJUSTMENT_QTY")
    private Integer impAdjustmentQty;
    @ApiModelProperty(notes = "Tiền nhập điều chỉnh")
    @Column(name = "IMP_ADJUSTMENT_AMOUNT")
    private Float impAdjustmentAmount;

    @ApiModelProperty(notes = "Tổng số lượng xuất trong kỳ")
    @Column(name = "EXP_TOTAL_QTY")
    private Integer expTotalQty;
    @ApiModelProperty(notes = "Số lượng xuất bán hàng")
    @Column(name = "EXP_SALES_QTY")
    private Integer expSalesQty;
    @ApiModelProperty(notes = "Thành tiền xuất bán hàng")
    @Column(name = "EXP_SALES_AMOUNT")
    private Float expSalesAmount;
    @ApiModelProperty(notes = "Khuyến mãi bán hàng")
    @Column(name = "EXP_PROMOTION_QTY")
    private Integer expPromotionQty;
    @ApiModelProperty(notes = "Thành tiền khuyến mãi bán hàng")
    @Column(name = "EXP_PROMOTION_AMOUNT")
    private Float expPromotionAmount;
    @ApiModelProperty(notes = "Số lượng xuất điều chỉnh")
    @Column(name = "EXP_ADJUSTMENT_QTY")
    private Integer expAdjustmentQty;
    @ApiModelProperty(notes = "Thành tiền xuất điều chỉnh")
    @Column(name = "EXP_ADJUSTMENT_AMOUNT")
    private Float expAdjustmentAmount;;
    @ApiModelProperty(notes = "Số lượng xuất trả hàng")
    @Column(name = "EXP_EXCHANGE_QTY")
    private Integer expExchangeQty;
    @ApiModelProperty(notes = "Thành tiền xuất trả hàng")
    @Column(name = "EXP_EXCHANGE_AMOUNT")
    private Float expExchangeAmount;

    @ApiModelProperty(notes = "Tồn cuối kỳ")
    @Column(name = "ENDING_QTY")
    private Integer endingQty;
    @ApiModelProperty(notes = "Giá cuối kỳ")
    @Column(name = "ENDING_PRICE")
    private Float endingPrice;
    @ApiModelProperty(notes = "Thành tiền cuối kỳ")
    @Column(name = "ENDING_AMOUNT")
    private Float endingAmount;
}
