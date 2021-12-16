package vn.viettel.core.dto.promotion;

import io.swagger.annotations.ApiModelProperty;
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
public class PromotionShopMapDTO extends BaseDTO {

    private Long promotionProgramId;
    private Long shopId;
    private Double quantityMax;
    private Double quantityReceived;
    private Double quantityCustomer;
    private Double amountMax;
    private Double amountReceived;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private Integer status;
    private Integer isQuantityMaxEdit;

    @ApiModelProperty(notes = "Số lượng thêm vào")
    private Double quantityAdd;
}
