package vn.viettel.sale.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;
import vn.viettel.core.util.Constants;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class NewOrderReturnDetailDTO {
    private Long saleOrderId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime orderDate;
    private Long shopId;
    private Long productId;
    private Integer quantity;
    private Float price;
    private Float amount;
    private Float total;
    private Boolean isFreeItem;
    private Float autoPromotion;
    private Float zmPromotion;
    private Float priceNotVat;
    private Float autoPromotionNotVat;
    private Float autoPromotionVat;
    private Float zmPromotionNotVat;
    private Float zmPromotionVat;
    private String promotionCode;
    private String promotionName;
    private Integer levelNumber;
}
