package vn.viettel.sale.messaging;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.util.Constants;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PoTransFilter {
    @ApiModelProperty(notes = "Mã giao dịch")
    private String transCode;
    @ApiModelProperty(notes = "Số hóa đơn đỏ")
    private String redInvoiceNo;
    @ApiModelProperty(notes = "Số nội bộ")
    private String internalNumber;
    @ApiModelProperty(notes = "Số Po")
    private String poNo;
    @ApiModelProperty(notes = "Từ ngày")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime fromDate;
    @ApiModelProperty(notes = "Đến ngày")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime toDate;
}
