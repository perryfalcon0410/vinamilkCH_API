package vn.viettel.sale.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class StockCountingDTO extends BaseDTO {
    @ApiModelProperty(notes = "Mã kiểm kê")
    private String stockCountingCode;
    @ApiModelProperty(notes = "Ngày kiểm kê")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime countingDate;
    @ApiModelProperty(notes = "Id cửa hàng")
    private Long shopId;
    @ApiModelProperty(notes = "Id kho hàng")
    private Long wareHouseTypeId;

}
