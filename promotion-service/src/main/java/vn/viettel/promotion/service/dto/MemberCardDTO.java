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
    private Long memberCardId;
    private String memberCardCode;
    private Date memberCardIssueDate;
    private Long customerTypeId;
    private Integer status;
    private Long cardTypeId;
}
