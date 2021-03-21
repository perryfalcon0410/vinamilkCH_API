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
public class IdentityCardCreateRequest extends BaseRequest {
    private String identityCardCode;
    private Date identityCardIssueDate;
    private String identityCardIssuePlace;
    private Date identityCardExpiryDate;
    private Long identityCardType;
}
