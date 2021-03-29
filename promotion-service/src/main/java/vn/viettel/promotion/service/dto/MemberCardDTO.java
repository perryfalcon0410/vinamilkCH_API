package vn.viettel.promotion.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.messaging.BaseRequest;

import java.util.Date;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberCardDTO extends BaseRequest {
    private Long id;
    private String memberCardCode;
    private Date memberCardIssueDate;
    private Long customerTypeId;
    private Integer status;
    private Integer levelCard;

    public MemberCardDTO(String memberCardCode, Date memberCardIssueDate, Long customerTypeId, Integer levelCard,Integer status) {
        this.memberCardCode = memberCardCode;
        this.customerTypeId = customerTypeId;
        this.memberCardIssueDate = memberCardIssueDate;
        this.levelCard = levelCard;
        this.status = status;
    }
}
