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
    @ApiModelProperty(notes = "Mã hóa đơn")
    private String orderNumber;
    @ApiModelProperty(notes = "Mã đơn tham chiếu")
    private String orderNumberRef;
    @ApiModelProperty(notes = "Người dùng")
    private String userName;
    @ApiModelProperty(notes = "Mã khách hàng")
    private String customerNumber;
    @ApiModelProperty(notes = "Tên khách hàng")
    private String customerName;
    @ApiModelProperty(notes = "số lượng")
    private float amount;
    @ApiModelProperty(notes = "Chiết khấu")
    private float totalPromotion;
    @ApiModelProperty(notes = "Thanh toán")
    private float total;
    @ApiModelProperty(notes = "Ngày trả hàng")
    private Date dateReturn;
    @ApiModelProperty(notes = "Ngày mua hàng")
    private Date orderDate;
}
