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
    @Column(name = "BEGINNING_QUANTITY")
    private Integer beginningQuantity;
    @ApiModelProperty(notes = "Giá đầu kỳ")
    @Column(name = "BEGINNING_PRICE")
    private Float beginningPrice;
    @ApiModelProperty(notes = "Thành tiền đầu kỳ")
    @Column(name = "BEGINNING_AMOUNT")
    private Float beginningAmount;

    @ApiModelProperty(notes = "Tổng số lượng nhập trong kỳ")
    @Column(name = "IMPORT_TOTAL_QUANTITY")
    private Integer importTotalQuantity;
    @ApiModelProperty(notes = "Số lượng nhập hàng")
    @Column(name = "IMPORT_QUANTITY")
    private Integer importQuantity;
    @ApiModelProperty(notes = "Tiền nhập hàng")
    @Column(name = "IMPORT_AMOUNT")
    private Float importAmount;
    @ApiModelProperty(notes = "Số lượng điều chỉnh")
    @Column(name = "IMPORT_ADJUSTMENT_QUANTITY")
    private Integer importAdjustmentQuantity;
    @ApiModelProperty(notes = "Tiền điều chỉnh")
    @Column(name = "IMPORT_ADJUSTMENT_AMOUNT")
    private Float importAdjustmentAmount;

    @ApiModelProperty(notes = "Tổng số lượng xuất trong kỳ")
    @Column(name = "EXPORT_TOTAL_QUANTITY")
    private Integer exportTotalQuantity;
    @ApiModelProperty(notes = "Số lượng bán hàng")
    @Column(name = "EXPORT_SALES_QUANTITY")
    private Integer exportSalesQuantity;
    @ApiModelProperty(notes = "Thành tiền bán hàng")
    @Column(name = "EXPORT_SALES_AMOUNT")
    private Float exportSalesAmount;
    @ApiModelProperty(notes = "Khuyến mãi bán hàng")
    @Column(name = "EXPORT_PROMOTION_QUANTITY")
    private Integer exportPromotionQuantity;
    @ApiModelProperty(notes = "Thành tiền khuyến mãi bán hàng")
    @Column(name = "EXPORT_PROMOTION_AMOUNT")
    private Float exportPromotionAmount;
    @ApiModelProperty(notes = "Số lượng xuất điều chỉnh")
    @Column(name = "EXPORT_ADJUSTMENT_QUANTITY")
    private Integer exportAdjustmentQuantity;
    @ApiModelProperty(notes = "Thành tiền xuất điều chỉnh")
    @Column(name = "EXPORT_ADJUSTMENT_AMOUNT")
    private Float exportAdjustmentAmount;;
    @ApiModelProperty(notes = "Số lượng xuất đổi hàng")
    @Column(name = "EXPORT_EXCHANGE_QUANTITY")
    private Integer exportExchangeQuantity;
    @ApiModelProperty(notes = "Thành tiền xuất đổi hàng")
    @Column(name = "EXPORT_EXCHANGE_AMOUNT")
    private Float exportExchangeAmount;

    @ApiModelProperty(notes = "Tồn cuối kỳ")
    @Column(name = "ENDING_QUANTITY")
    private Integer endingQuantity;
    @ApiModelProperty(notes = "Giá cuối kỳ")
    @Column(name = "ENDING_PRICE")
    private Float endingPrice;
    @ApiModelProperty(notes = "Thành tiền cuối kỳ")
    @Column(name = "ENDING_AMOUNT")
    private Float endingAmount;
}
