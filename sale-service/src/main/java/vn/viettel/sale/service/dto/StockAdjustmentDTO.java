package vn.viettel.sale.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;
import vn.viettel.core.util.Constants;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class StockAdjustmentDTO extends BaseDTO {
    private String adjustmentCode;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime adjustmentDate;
    private Long shopId;
    private Integer type;
    private Integer status;
    private Long wareHouseTypeId;
    private Long reasonId;
    private String description;

    public StockAdjustmentDTO(Long id, String adjustmentCode, LocalDateTime adjustmentDate, Long shopId, Integer type, Integer status, Long wareHouseTypeId, Long reasonId, String description) {
        this.setId(id);
        this.adjustmentCode = adjustmentCode;
        this.adjustmentDate = adjustmentDate;
        this.shopId = shopId;
        this.type = type;
        this.status = status;
        this.wareHouseTypeId = wareHouseTypeId;
        this.reasonId = reasonId;
        this.description = description;
    }
}
