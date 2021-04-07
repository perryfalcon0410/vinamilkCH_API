package vn.viettel.sale.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import javax.persistence.Column;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockCountingDTO extends BaseDTO {
    private String stockCountingCode;
    private Date countingDate;
    private Long shopId;
    private Long wareHouseTypeId;
    private String createUser;
    private String updateUser;
}
