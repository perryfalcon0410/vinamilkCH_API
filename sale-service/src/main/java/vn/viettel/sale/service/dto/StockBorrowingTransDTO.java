package vn.viettel.sale.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;
import vn.viettel.core.util.Constants;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class StockBorrowingTransDTO extends BaseDTO {
    private String transCode;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime transDate;
    private Long shopId;
    private Integer type;
    private Integer ReceiptType;
    private String note;
    private Long wareHouseTypeId;
    private String wareHouseTypeName;
    private Long stockBorrowingId;
    private Long fromShopId;
    private Long toShopId;
    private String redInvoiceNo;
    private String internalNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime borrowDate;
    private Float totalAmount;
    private Integer totalQuantity;
    private Integer status;

}
