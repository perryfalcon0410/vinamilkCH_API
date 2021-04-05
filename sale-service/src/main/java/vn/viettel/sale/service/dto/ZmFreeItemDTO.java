package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ZmFreeItemDTO {
    private Long promotionId;
    private String promotionProgramName;
    private String promotionProgramCode;
    private Long productId;
    private String productName;
    private String productCode;
    private Integer promotionQuantity;
    private Double discountPercent;
    private Float discountAmount;
    private String discountCode;
}