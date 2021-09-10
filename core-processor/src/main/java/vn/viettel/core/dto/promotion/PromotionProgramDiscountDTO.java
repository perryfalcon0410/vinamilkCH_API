package vn.viettel.core.dto.promotion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    private Double actualDiscountAmount;
    private String customerCode;

    private PromotionProgramDTO program;

    public PromotionProgramDiscountDTO(Long id, Long promotionProgramId, Float minSaleAmount,
                                       Float maxSaleAmount, Float discountAmount, Float discountPercent,
                                       Float maxDiscountAmount, Integer status, String discountCode, Integer type,
           Integer isUsed, LocalDateTime orderDate, String orderNumber, String orderShopCode,
           String orderCustomerCode, Double orderAmount, Double actualDiscountAmount,
           String customerCode) {
        this.setId(id);
        this.promotionProgramId = promotionProgramId;
        this.minSaleAmount = minSaleAmount!=null?minSaleAmount.doubleValue():null;
        this.maxSaleAmount = maxSaleAmount!=null?maxSaleAmount.doubleValue():null;
        this.discountAmount = discountAmount!=null?discountAmount.doubleValue():null;
        this.discountPercent = discountPercent!=null?discountPercent.doubleValue():null;
        this.maxDiscountAmount = maxDiscountAmount!=null?maxDiscountAmount.doubleValue():null;
        this.status = status;
        this.discountCode = discountCode;
        this.type = type;
        this.isUsed = isUsed;
        this.orderDate = orderDate;
        this.orderNumber = orderNumber;
        this.orderShopCode = orderShopCode;
        this.orderCustomerCode = orderCustomerCode;
        this.orderAmount = orderAmount;
        this.actualDiscountAmount = actualDiscountAmount;
        this.customerCode = customerCode;
    }

}
