package vn.viettel.core.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberCustomerRequest extends BaseRequest{

    private Double scoreCumulated;
}
