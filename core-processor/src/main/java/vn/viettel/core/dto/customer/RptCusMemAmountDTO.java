package vn.viettel.core.dto.customer;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;
import vn.viettel.core.util.Constants;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RptCusMemAmountDTO extends BaseDTO {
    @ApiModelProperty(notes = "Id nhóm khách hàng")
    private Long customerTypeId;
    @ApiModelProperty(notes = "Id thẻ thành viên")
    private Long memberCardId;
    @ApiModelProperty(notes = "Ngày bắt đầu của thẻ")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime fromDate;
    @ApiModelProperty(notes = "Id đơn vị của khách hàng")
    private Long custShopId;
    @ApiModelProperty(notes = "Id khách hàng")
    private Long customerId;
    @ApiModelProperty(notes = "Tổng số lượng tích lũy")
    private Integer quantity;
    @ApiModelProperty(notes = "Tổng doanh số tích lũy")
    private Float amount;
    @ApiModelProperty(notes = "Tổng điểm tích lũy")
    private Integer score;
    @ApiModelProperty(notes = "Trạng thái: 1-Hoạt động")
    private Integer status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime rptDate;
}
