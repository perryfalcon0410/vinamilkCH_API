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
public class PromotionProgramDTO extends BaseDTO {

    private String promotionProgramCode;
    private String promotionProgramName;
    private Integer status;
    private String type;
    private String proFormat; //
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private Integer relation;
    private Integer multiple;
    private Integer recursive;
    private String description;//
    private Long groupId;//
    private Integer isReturn;
    private Integer objectType;
    private Integer givenType;
    private Integer discountType;
    private Integer discountPriceType;
    private Integer checkMainItem;//
    private Integer promotionDateTime;
    private Long progTypeId;//
    private Integer amountOrderType;
    private Integer isEdited;
    private Long promotionGroupId;

    public PromotionProgramDTO(Long id, Long useInDay){
        this.setId(id);
        this.promotionDateTime = useInDay == null? 0 : useInDay.intValue();
    }

    public PromotionProgramDTO(String promotionProgramCode, Long useInDay){
        this.promotionProgramCode = promotionProgramCode;
        this.promotionDateTime = useInDay == null? 0 : useInDay.intValue();
    }

    public PromotionProgramDTO(Long id, String promotionProgramCode, String promotionProgramName,
                               Integer status, String type, LocalDateTime fromDate, LocalDateTime toDate,
                               Integer relation, Integer multiple, Integer recursive, Integer isReturn, Integer objectType, Integer givenType, Integer discountType,
                               Integer discountPriceType, Integer promotionDateTime, Integer amountOrderType, Integer isEdited) {
        this.setId(id);
        this.promotionProgramCode = promotionProgramCode;
        this.promotionProgramName = promotionProgramName;
        this.status = status;
        this.type = type;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.relation = relation;
        this.multiple = multiple;
        this.recursive = recursive;
        this.isReturn = isReturn;
        this.objectType = objectType;
        this.givenType = givenType;
        this.discountType = discountType;
        this.discountPriceType = discountPriceType;
        this.promotionDateTime = promotionDateTime;
        this.amountOrderType = amountOrderType;
        this.isEdited = isEdited;
    }
}
