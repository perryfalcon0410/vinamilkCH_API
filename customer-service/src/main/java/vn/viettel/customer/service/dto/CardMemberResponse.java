package vn.viettel.customer.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardMemberResponse extends BaseDTO {
    private Timestamp createDate;
    private String cardType;
    private String customerType;
}
