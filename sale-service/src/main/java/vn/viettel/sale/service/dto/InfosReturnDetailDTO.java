package vn.viettel.sale.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.util.Constants;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class InfosReturnDetailDTO {
    @ApiModelProperty(notes = "Ngày hóa đon")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime orderDate;
    @ApiModelProperty(notes = "Tên khách hàng")
    private String CustomerName;
    @ApiModelProperty(notes = "Lý do trả hàng")
    private String reason;
    @ApiModelProperty(notes = "Mô tả trả hàng")
    private String reasonDesc;
    @ApiModelProperty(notes = "Ngày trả lại")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime returnDate;
    @ApiModelProperty(notes = "Tên nhân viên")
    private String userName;
    @ApiModelProperty(notes = "Ghi chú")
    private String note;
    @ApiModelProperty(notes = "Mã trả hàng")
    private String returnNumber;
}
