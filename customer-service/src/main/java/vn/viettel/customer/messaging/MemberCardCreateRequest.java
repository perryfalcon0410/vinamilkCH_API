package vn.viettel.customer.messaging;

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
public class MemberCardCreateRequest extends BaseRequest {
    private String memberCardCode;
    private Date memberCardIssueDate;
    private Integer levelCard;
    private Long customerTypeId;
}
