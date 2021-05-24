package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class OrderReturnDTO extends BaseDTO {
    @ApiModelProperty(notes = "Số hóa đơn")
    private String orderNumber;
    @ApiModelProperty(notes = "Số hóa đơn tham chiếu")
    private String orderNumberRef;
    @ApiModelProperty(notes = "Tên nhân viên")
    private String userName;
    @ApiModelProperty(notes = "Mã khách hàng")
    private String customerNumber;
    @ApiModelProperty(notes = "Tên khách hàng")
    private String customerName;
    @ApiModelProperty(notes = "Tổng tiền trước chiết khấu")
    private float amount;
    @ApiModelProperty(notes = "Tổng khuyến mãi")
    private float totalPromotion;
    @ApiModelProperty(notes = "Tổng tiền")
    private float total;
    @ApiModelProperty(notes = "Ngày trả hàng")
    private Date dateReturn;
    @ApiModelProperty(notes = "Ngày hóa đơn")
    private Date orderDate;
}
