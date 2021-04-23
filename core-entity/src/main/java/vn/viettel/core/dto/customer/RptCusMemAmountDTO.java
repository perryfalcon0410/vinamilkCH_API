package vn.viettel.core.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import javax.persistence.Column;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RptCusMemAmountDTO extends BaseDTO {
    private Long customerTypeId;
    private Long memberCardId;
    private Date fromDate;
    private Long custShopId;
    private Long customerId;
    private Integer quantity;
    private Float amount;
    private Integer score;
    private Integer status;
    private Date rptDate;
}
