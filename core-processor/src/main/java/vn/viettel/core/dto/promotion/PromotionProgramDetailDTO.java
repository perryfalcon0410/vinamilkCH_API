package vn.viettel.core.dto.promotion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PromotionProgramDetailDTO extends BaseDTO {
    private Long promotionProgramId;
    private String promotionProgramCode;
    private String promotionProgramName;
    private Long productId;
    private Integer saleQty;
    private String saleUom;
    private Double saleAmt;
    private Double discAmt;
    private Double disPer;
    private Long freeProductId;
    private Integer freeQty;
    private String freeUom;
    private Integer required;
    private Double salePer;
    private Integer orderNumber;

    public PromotionProgramDetailDTO(Long id, Long promotionProgramId, Long productId, Integer saleQty, String saleUom,
                 Double saleAmt, Double discAmt, Double disPer, Long freeProductId, Integer freeQty, String freeUom,
                 Integer required, Double salePer, Integer orderNumber) {
        this.setId(id);
        this.promotionProgramId = promotionProgramId;
        this.productId = productId;
        this.saleQty = saleQty;
        this.saleUom = saleUom;
        this.saleAmt = saleAmt;
        this.discAmt = discAmt;
        this.disPer = disPer;
        this.freeProductId = freeProductId;
        this.freeQty = freeQty;
        this.freeUom = freeUom;
        this.required = required;
        this.salePer = salePer;
        this.orderNumber = orderNumber;

    }

}
