package vn.viettel.promotion.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberCardDTO {
    private Long id;
    private String memberCardCode;
    private String memberCardName;
    private Long customerTypeId;
    private Integer status;
    private Integer levelCard;
}
