package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class OrderDiscountReturnDTO{
    private Long saleOrderId;
    private LocalDateTime orderDate;
    private Long promotionProgramId;
    private String promotionCode;
    private String promotionName;
    private String promotionType;
    private Long productId;
    private Boolean isAutoPromotion;
    private Float discountAmount;
    private Float discountAmountNotVat;
    private Float discountAmountVat;
    private Float maxDiscountAmount;
    private Integer levelNumber;
}
