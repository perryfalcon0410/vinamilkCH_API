package vn.viettel.core.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberCustomerDTO {
    private Long memberCardId;
    private Long customerId;
    private Date issueDate;
    private Long shopId;
}
