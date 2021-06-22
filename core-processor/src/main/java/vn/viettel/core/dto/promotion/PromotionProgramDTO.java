package vn.viettel.core.dto.promotion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PromotionProgramDTO extends BaseDTO {

    private String promotionProgramCode;
    private String promotionProgramName;
    private Integer status;
    private String type;
    private String proFormat;

    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private Integer relation;
    private Integer multiple;
    private Integer recursive;
    private String description;
    private Long groupId;
    private Integer isReturn;
    private Integer objectType;
    private Integer givenType;
    private Integer discountType;
    private Integer discountPriceType;
    private Integer checkMainItem;
    private Integer promotionDateTime;
    private Long progTypeId;
    private Integer amountOrderType;
    private Integer isEdited;
    private Long promotionGroupId;
}
