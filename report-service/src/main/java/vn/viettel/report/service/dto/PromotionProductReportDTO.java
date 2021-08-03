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

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Báo cáo hàng khuyến mãi")
public class PromotionProductReportDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime fromDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime toDate;

    @ApiModelProperty(notes = "Ngày xuất báo cáo")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime reportDate = LocalDateTime.now();

    @ApiModelProperty(notes = "Tổng số lượng")
    private Integer totalQuantity;

    @ApiModelProperty(notes = "Cửa hàng")
    private ShopDTO shop;

    @ApiModelProperty(notes = "Danh sách sản phẩm theo ngành hàng")
    List<PromotionProductCatDTO> productCats;

    public PromotionProductReportDTO (LocalDateTime fromDate, LocalDateTime toDate, ShopDTO shop) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.shop = shop;
    }

}
