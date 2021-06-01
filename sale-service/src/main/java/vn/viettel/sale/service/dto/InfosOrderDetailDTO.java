package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private Date orderDate;
}
