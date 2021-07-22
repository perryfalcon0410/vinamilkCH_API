package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class SaleComboDiscountDTO {
    private Long saleOrderId;
    private LocalDateTime orderDate;
    private Long promotionProgramId;
    private String promotionCode;
    private Long comboProductId;
    private Long productId;
    private Boolean isAutoPromotion;
    private Float discountAmount;
    private Float discountAmountNotVat;
    private Float discountAmountVat;
    private Integer levelNumber;
}
