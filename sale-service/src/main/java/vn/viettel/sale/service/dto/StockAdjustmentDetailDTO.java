package vn.viettel.sale.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;
import vn.viettel.core.util.Constants;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class StockAdjustmentDetailDTO extends BaseDTO {
    private Long adjustmentId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime adjustmentDate;
    private Long shopId;
    private Long productId;
    private String productCode;
    private String productName;
    private String licenseNumber;
    private String unit;
    private Integer quantity;
    private Double price;
    private Double totalPrice;
}
