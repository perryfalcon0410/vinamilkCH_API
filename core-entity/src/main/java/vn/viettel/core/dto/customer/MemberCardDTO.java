package vn.viettel.core.dto.customer;

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
    private String memberCardName;
    private Date memberCardIssueDate;
    private Long closelyTypeId;
    private Integer status;
    private Long cardTypeId;
}
