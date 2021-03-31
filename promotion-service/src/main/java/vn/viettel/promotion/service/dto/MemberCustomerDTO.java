package vn.viettel.promotion.service.dto;

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
    private Long MemberCardId;
    private Long CustomerId;
    private Date Issue_Date;
    private Long ShopId;
}
