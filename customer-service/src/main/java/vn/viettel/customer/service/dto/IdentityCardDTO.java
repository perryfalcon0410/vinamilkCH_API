package vn.viettel.customer.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IdentityCardDTO extends BaseDTO {
    private String identityCardCode;
    private Date identityCardIssueDate;
    private String identityCardIssuePlace;
    private Date identityCardExpiryDate;
    private Long identityCardType;
}
