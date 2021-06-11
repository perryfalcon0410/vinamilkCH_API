package vn.viettel.core.dto.promotion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PromotionProgramDiscountDTO extends BaseDTO {
    private Long promotionProgramId;
    private Double minSaleAmount;
    private Double maxSaleAmount;
    private Double discountAmount;
    private Double discountPercent;
    private Double maxDiscountAmount;
    private Integer status;
    private String discountCode;
    private Integer type;
    private Integer isUsed;
    private LocalDateTime orderDate;
    private String orderNumber;
    private String orderShopCode;
    private String orderCustomerCode;
    private Double orderAmount;
    private Double ActualDiscountAmount;
    private String customerCode;
}
