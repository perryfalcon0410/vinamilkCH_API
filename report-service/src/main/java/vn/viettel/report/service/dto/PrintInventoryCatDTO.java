package vn.viettel.report.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PrintInventoryCatDTO {

    @ApiModelProperty(notes = "Id ngành hàng")
    @Column(name = "CAT_ID")
    private Long catId;

    @ApiModelProperty(notes = "Tên ngành hàng")
    @Column(name = "CAT_NAME")
    private String catName;

    @ApiModelProperty(notes = "Tồn đầu kỳ")
    private Long beginningQty = 0L;

    @ApiModelProperty(notes = "Thành tiền đầu kỳ")
    private Double beginningAmount = 0.0;

    @ApiModelProperty(notes = "Tổng số lượng nhập trong kỳ")
    private Long impTotalQty = 0L;

    @ApiModelProperty(notes = "Số lượng nhập mua hàng")
    private Long impQty = 0L;

    @ApiModelProperty(notes = "Tiền nhập mua hàng")
    private Double impAmount = 0.0;

    @ApiModelProperty(notes = "Số lượng nhập điều chỉnh")
    private Long impAdjustmentQty = 0L;

    @ApiModelProperty(notes = "Tiền nhập điều chỉnh")
    private Double impAdjustmentAmount = 0.0;

    @ApiModelProperty(notes = "Tổng số lượng xuất trong kỳ")
    private Long expTotalQty = 0L;

    @ApiModelProperty(notes = "Số lượng xuất bán hàng")
    private Long expSalesQty = 0L;

    @ApiModelProperty(notes = "Thành tiền xuất bán hàng")
    private Double expSalesAmount =  0.0;

    @ApiModelProperty(notes = "Khuyến mãi bán hàng")
    private Long expPromotionQty = 0L;

    @ApiModelProperty(notes = "Thành tiền khuyến mãi bán hàng")
    private Double expPromotionAmount = 0.0;

    @ApiModelProperty(notes = "Số lượng xuất điều chỉnh")
    private Long expAdjustmentQty = 0L;

    @ApiModelProperty(notes = "Thành tiền xuất điều chỉnh")
    private Double expAdjustmentAmount = 0.0;

    @ApiModelProperty(notes = "Số lượng xuất trả hàng")
    private Long expExchangeQty = 0L;

    @ApiModelProperty(notes = "Thành tiền xuất trả hàng")
    private Double expExchangeAmount = 0.0;

    @ApiModelProperty(notes = "Tồn cuối kỳ")
    private Long endingQty = 0L;

    @ApiModelProperty(notes = "Thành tiền cuối kỳ")
    private Double endingAmount = 0.0;

    @ApiModelProperty(notes = "Danh sách sản phẩm")
    List<ImportExportInventoryDTO> products;



    public void addBeginningQty(Long beginningQty){
        if(beginningQty!=null) this.beginningQty += beginningQty;
    }

    public void addBeginningAmount(Double beginningAmount){
        if(beginningAmount!=null) this.beginningAmount += beginningAmount;
    }

    public void addImpTotalQty(Long impTotalQty){
        if(impTotalQty!=null) this.impTotalQty += impTotalQty;
    }

    public void addImpQty(Long impQty){
        if(impQty!=null) this.impQty += impQty;
    }

    public void addImpAmount(Double impAmount){
        if(impAmount!=null) this.impAmount += impAmount;
    }

    public void addImpAdjustmentQty(Long impAdjustmentQty){
        if(impAdjustmentQty!=null) this.impAdjustmentQty += impAdjustmentQty;
    }

    public void addImpAdjustmentAmount(Double impAdjustmentAmount){
        if(impAdjustmentAmount!=null) this.impAdjustmentAmount += impAdjustmentAmount;
    }

    public void addExpTotalQty(Long expTotalQty){
        if(expTotalQty!=null) this.expTotalQty += expTotalQty;
    }

    public void addExpSalesQty(Long expSalesQty){
        if(expSalesQty!=null) this.expSalesQty += expSalesQty;
    }

    public void addExpSalesAmount(Double expSalesAmount){
        if(expSalesAmount!=null) this.expSalesAmount += expSalesAmount;
    }

    public void addExpPromotionQty(Long expPromotionQty){
        if(expPromotionQty!=null) this.expPromotionQty += expPromotionQty;
    }

    public void addExpPromotionAmount(Double expPromotionAmount){
        if(expPromotionAmount!=null) this.expPromotionAmount += expPromotionAmount;
    }

    public void addExpAdjustmentQty(Long expAdjustmentQty){
        if(expAdjustmentQty!=null) this.expAdjustmentQty += expAdjustmentQty;
    }

    public void addExpAdjustmentAmount(Double expAdjustmentAmount){
        if(expAdjustmentAmount!=null) this.expAdjustmentAmount += expAdjustmentAmount;
    }

    public void addExpExchangeQty(Long expExchangeQty){
        if(expExchangeQty!=null) this.expExchangeQty += expExchangeQty;
    }

    public void addExpExchangeAmount(Double expExchangeAmount){
        if(expExchangeAmount!=null) this.expExchangeAmount += expExchangeAmount;
    }

    public void addEndingQty(Long endingQty){
        if(endingQty!=null) this.endingQty += endingQty;
    }

    public void addEndingAmount(Double endingAmount){
        if(endingAmount!=null) this.endingAmount += endingAmount;
    }

}
