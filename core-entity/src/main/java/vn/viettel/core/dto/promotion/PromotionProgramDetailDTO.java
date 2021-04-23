package vn.viettel.core.dto.promotion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import javax.persistence.Column;
import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PromotionProgramDetailDTO extends BaseDTO {
    private Long promotionProgramId;
    private Long productId;
    private Integer saleQty;
    private String saleUom;
    private Float saleAmt;
    private Float discAmt;
    private Float disPer;
    private Long freeProductId;
    private Integer freeQty;
    private String freeUom;
    private Integer required;
    private Float salePer;
    private Integer orderNumber;
}
