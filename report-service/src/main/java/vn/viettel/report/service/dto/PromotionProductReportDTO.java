package vn.viettel.report.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.util.Constants;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Báo cáo hàng khuyến mãi")
public class PromotionProductReportDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDate fromDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDate toDate;

    @ApiModelProperty(notes = "Ngày xuất báo cáo")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDate reportDate = LocalDate.now();

    @ApiModelProperty(notes = "Tổng số lượng")
    private Integer totalQuantity;

    @ApiModelProperty(notes = "Tổng thành tiền")
    private  Float totalPrice;

    @ApiModelProperty(notes = "Cửa hàng")
    private ShopDTO shop;

    @ApiModelProperty(notes = "Danh sách sản phẩm theo ngành hàng")
    Set<PromotionProductCatDTO> productCats;

    public PromotionProductReportDTO (LocalDate fromDate, LocalDate toDate, ShopDTO shop) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.shop = shop;
    }

    public void addProductCat(PromotionProductCatDTO productCat) {
        if(productCats == null) productCats = new HashSet<>();
        this.productCats.add(productCat);
    }

}
