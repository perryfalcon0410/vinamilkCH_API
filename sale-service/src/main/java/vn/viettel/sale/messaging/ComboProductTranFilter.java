package vn.viettel.sale.messaging;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.util.Constants;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ComboProductTranFilter {
    @ApiModelProperty(notes = "Id shop")
    private Long shopId;
    @ApiModelProperty(notes = "Số giao dịch")
    private String transCode;
    @ApiModelProperty(notes = "Loại giao dịch")
    private Integer transType;
    @ApiModelProperty(notes = "Từ ngày")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime fromDate;
    @ApiModelProperty(notes = "Đến ngày")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime toDate;

}