package vn.viettel.sale.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.util.Constants;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class NewOrderReturnDetailDTO {
    @ApiModelProperty(notes = "id hóa đơn")
    private Long saleOrderId;
    @ApiModelProperty(notes = "ngày mua hàng")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime orderDate;
    @ApiModelProperty(notes = "id cửa hàng")
    private Long shopId;
    @ApiModelProperty(notes = "id sản phẩm")
    private Long productId;
    @ApiModelProperty(notes = "số lượng sản phẩm")
    private Integer quantity;
    @ApiModelProperty(notes = "giá sản phẩm")
    private Float price;
    @ApiModelProperty(notes = "tổng tiến trước chiết khấu")
    private Float amount;
    @ApiModelProperty(notes = "tổng tiến sau trước khấu")
    private Float total;
    @ApiModelProperty(notes = "sản phẩm khuyễn mãi hay không")
    private Boolean isFreeItem;
    @ApiModelProperty(notes = "tiền khuyến mãi tự động")
    private Float autoPromotion;
    @ApiModelProperty(notes = "tiền khuyến mãi tay")
    private Float zmPromotion;
    @ApiModelProperty(notes = "giá trước thuế")
    private Float priceNotVat;
    @ApiModelProperty(notes = "Khuyến mãi tự động trước thuế")
    private Float autoPromotionNotVat;
    @ApiModelProperty(notes = "Khuyến mãi tự động sau thuế")
    private Float autoPromotionVat;
    @ApiModelProperty(notes = "khuyễn mãi tay trước thuế")
    private Float zmPromotionNotVat;
    @ApiModelProperty(notes = "khuyễn mãi tay sau thuế")
    private Float zmPromotionVat;
    @ApiModelProperty(notes = "mã chương trình khuyễn mãi")
    private String promotionCode;
    @ApiModelProperty(notes = "Loại chương trình khuyến mãi")
    private String promotionType;
    @ApiModelProperty(notes = "tên chương trình khuyến mãi")
    private String promotionName;
    @ApiModelProperty(notes = "mức chương trình khuyễn mãi")
    private Integer levelNumber;
}
