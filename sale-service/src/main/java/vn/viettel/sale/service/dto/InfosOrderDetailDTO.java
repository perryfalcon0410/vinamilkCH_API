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
public class InfosOrderDetailDTO {
    @ApiModelProperty(notes = "Tiền tệ")
    private String currency;
    @ApiModelProperty(notes = "Tổng tiền đơn hàng")
    private Double total;
    @ApiModelProperty(notes = "Tiền khách đưa")
    private Double totalPaid;
    @ApiModelProperty(notes = "Tiền thừa")
    private Double balance;
    @ApiModelProperty(notes = "Tên nhân viên")
    private String saleMan;
    @ApiModelProperty(notes = "Tên khách hàng")
    private String customerName;
    @ApiModelProperty(notes = "Số hóa đơn")
    private String orderNumber;
    @ApiModelProperty(notes = "Ngày hóa đơn")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime orderDate;
    @ApiModelProperty(notes = "Tiền tích lũy trong đơn hàng")
    private Double memberCardAmount;
}
