package vn.viettel.report.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.dto.ShopDTO;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Báo cáo hàng khuyến mãi")
public class PromotionProductReportDTO {

    private Date fromDate;

    private Date toDate;

    @ApiModelProperty(notes = "Ngày xuất báo cáo")
    private Date reportDate = new Date();

    @ApiModelProperty(notes = "Tổng số lượng")
    private Integer totalQuantity;

    @ApiModelProperty(notes = "Tổng thành tiền")
    private  Float totalPrice;

    @ApiModelProperty(notes = "Cửa hàng")
    private ShopDTO shop;

    @ApiModelProperty(notes = "Danh sách sản phẩm theo ngành hàng")
    Set<PromotionProductCatDTO> productCats;

    public PromotionProductReportDTO (Date fromDate, Date toDate, ShopDTO shop) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.shop = shop;
    }

    public void addProductCat(PromotionProductCatDTO productCat) {
        if(productCats == null) productCats = new HashSet<>();
        this.productCats.add(productCat);
    }

}
