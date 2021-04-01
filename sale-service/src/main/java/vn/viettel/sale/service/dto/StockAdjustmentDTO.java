package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import javax.persistence.Column;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class StockAdjustmentDTO extends BaseDTO {
    private String adjustmentCode;
    private Date adjustmentDate;
    private Long shopId;
    private Integer type;
    private Integer status;
    private Long wareHouseTypeId;
    private Long reasonId;
    private String description;
    private String createUser;
    private String updateUser;
}
