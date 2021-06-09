package vn.viettel.core.dto.promotion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PromotionShopMapDTO extends BaseDTO {

    private Long promotionProgramId;
    private Long shopId;
    private Double quantityMax;
    private Double quantityReceived;
    private Double quantityCustomer;
    private Double amountMax;
    private Double amountReceived;
    private Timestamp fromDate;
    private Timestamp toDate;
    private Integer status;
    private Integer isQuantityMaxEdit;
}
