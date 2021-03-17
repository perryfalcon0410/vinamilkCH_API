package vn.viettel.customer.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class IDCardDto extends BaseDTO {
    private String idNumber;
    private Long cusId;
    private Date issueDate;
    private String issuePlace;
}
