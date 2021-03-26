package vn.viettel.sale.messaging;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class StockAdjustmentRequest {

    private Integer importType;
    private Long adjustmentId;
    //////////////////////////////////////////////////////
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
