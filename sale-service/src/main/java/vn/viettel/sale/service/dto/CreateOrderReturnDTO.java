package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class CreateOrderReturnDTO {
    @ApiModelProperty(notes = "Ngày trả lại")
    private Date orderDate;
    @ApiModelProperty(notes = "Tên nhân viên")
    private String userName;
    @ApiModelProperty(notes = "Tên khách hàng")
    private String customerName;
    @ApiModelProperty(notes = "Tổng tiền trả lại")
    private float totalPaidReturn;
}
