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
public class StockBorrowingDTO extends BaseDTO {
    private String poBorrowCode;
    private Long shopId;
    private Long toShopId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime borrowDate;
    private Integer status;
    private Long wareHouseTypeId;
    private Integer totalQuantity;
    private Float totalAmount;
    private String note;
}
