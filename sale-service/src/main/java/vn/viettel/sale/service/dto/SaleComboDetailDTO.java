package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class SaleComboDetailDTO {
    private Long saleOrderId;
    private LocalDateTime orderDate;
    private Long comboProductId;
    private Long productId;
    private Integer comboQuantity;
    private Integer quantity;
    private Double price;
    private Double amount;
    private Double total;
    private Double autoPromotion;
    private Double autoPromotionNotVat;
    private Double autoPromotionVat;
    private Double zmPromotion;
    private Double zmPromotionNotVat;
    private Double zmPromotionVat;
    private Double priceNotVat;
    private Boolean isFreeItem;
    private String promotionCode;
    private String promotionName;
    private Integer levelNumber;
}
