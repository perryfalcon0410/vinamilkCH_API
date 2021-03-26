package vn.viettel.customer.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.messaging.BaseRequest;

import javax.persistence.Column;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberCardUpdateRequest extends BaseRequest {
    private Long id;
    private String memberCardCode;
    private String memberCardName;
    private Long customerTypeId;
    private Integer status;
    private Integer levelCard;
}
