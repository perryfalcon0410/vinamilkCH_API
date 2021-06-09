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
public class StockAdjustmentTransDTO extends BaseDTO {
    private String transCode;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime transDate;
    private Long shopId;
    private String note;
    private Integer type;
    private Integer ReceiptType;
    private Integer status;
    private Long wareHouseTypeId;
    private String wareHouseTypeName;
    private Long adjustmentId;
    private String redInvoiceNo;
    private String internalNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime adjustmentDate;
    private Float totalAmount;
    private Integer totalQuantity;
    private Long reasonId;
}
